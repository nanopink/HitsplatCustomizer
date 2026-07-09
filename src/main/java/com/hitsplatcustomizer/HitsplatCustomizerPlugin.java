package com.hitsplatcustomizer;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.client.callback.RenderCallback;
import net.runelite.client.callback.RenderCallbackManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Hitsplat Customizer",
	description = "Take control of hitsplats. Show more damage and healing splats with layouts, filters, spacing, and timing.",
	tags = {"hitsplat", "hitsplats", "hit", "splat", "damage", "heal", "healing", "combat", "customize", "layout", "filter"}
)
public class HitsplatCustomizerPlugin extends Plugin
{
	private static final int GAME_CYCLE_MILLIS = 20;
	private static final int MIN_NATIVE_SUPPRESSION_CYCLES = 100;
	private static final String LEGACY_TRIANGULAR_LAYOUT_MODE = "TRIANGULAR";

	@Inject
	private HitsplatCustomizerConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private RenderCallbackManager renderCallbackManager;

	@Inject
	private HitsplatCustomizerOverlay overlay;

	@Getter
	private final Map<Actor, CopyOnWriteArrayList<HitsplatCustomizerHitsplat>> hitsplats = new ConcurrentHashMap<>();

	@Getter
	private final Map<Actor, Integer> nativeUiSuppressedUntilGameCycle = new ConcurrentHashMap<>();

	private long nextSequence;
	private boolean applyingPreset;

	private final RenderCallback nativeActorUiHider = new RenderCallback()
	{
		@Override
		public boolean addEntity(Renderable renderable, boolean ui)
		{
			if (!ui || !(renderable instanceof Actor))
			{
				return true;
			}

			return !isNativeUiSuppressed((Actor) renderable, client.getGameCycle());
		}
	};

	@Override
	protected void startUp()
	{
		migrateLegacyConfig();
		renderCallbackManager.register(nativeActorUiHider);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		renderCallbackManager.unregister(nativeActorUiHider);
		hitsplats.clear();
		nativeUiSuppressedUntilGameCycle.clear();
		nextSequence = 0;
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Actor actor = event.getActor();
		Hitsplat hitsplat = event.getHitsplat();
		if (actor == null || hitsplat == null)
		{
			return;
		}

		int gameCycle = client.getGameCycle();
		suppressNativeActorUi(actor, gameCycle);

		if (shouldDisableHitsplatsForActor(actor))
		{
			return;
		}

		if (config.hideZeroHitsplats() && hitsplat.getAmount() == 0)
		{
			return;
		}

		boolean mine = shouldTreatAsMine(hitsplat);
		if (config.onlyDisplayMine() && !mine)
		{
			return;
		}

		CopyOnWriteArrayList<HitsplatCustomizerHitsplat> actorHitsplats = hitsplats.computeIfAbsent(actor, ignored -> new CopyOnWriteArrayList<>());
		actorHitsplats.removeIf(activeHitsplat -> shouldRemoveFromTracking(actor, activeHitsplat, gameCycle));

		int maxHitsplats = config.maxHitsplats();
		int position = getAvailablePosition(actorHitsplats, maxHitsplats, gameCycle);
		if (position < 0 && mine && config.prioritizeMine())
		{
			position = evictForPrioritizedMine(actor, actorHitsplats, maxHitsplats, gameCycle);
		}

		if (position < 0)
		{
			return;
		}

		int fadeInCycles = fadeInCycles();
		int fullOpacityCycles = fullOpacityCycles();
		int fadeOutCycles = fadeOutCycles();
		actorHitsplats.add(new HitsplatCustomizerHitsplat(
				hitsplat.getHitsplatType(),
				hitsplat.getAmount(),
				position,
				gameCycle,
				gameCycle + fadeInCycles,
				gameCycle + fadeInCycles + fullOpacityCycles,
				gameCycle + fadeInCycles + fullOpacityCycles + fadeOutCycles,
				nextSequence++,
				mine));
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!HitsplatCustomizerConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}

		if (!HitsplatCustomizerConfig.PRESET_KEY.equals(event.getKey()))
		{
			if (!applyingPreset && isPresetControlledSetting(event.getKey()) && config.preset() != HitsplatCustomizerPreset.CUSTOM)
			{
				configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.PRESET_KEY, HitsplatCustomizerPreset.CUSTOM.name());
				refreshConfigPanel();
			}

			return;
		}

		HitsplatCustomizerPreset preset = presetFromValue(event.getNewValue());
		if (preset == null || preset == HitsplatCustomizerPreset.CUSTOM)
		{
			return;
		}

		applyingPreset = true;
		try
		{
			applyPreset(preset);
			refreshConfigPanel();
		}
		finally
		{
			applyingPreset = false;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		cleanupExpiredHitsplats();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			hitsplats.clear();
			nativeUiSuppressedUntilGameCycle.clear();
			nextSequence = 0;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		hitsplats.remove(event.getNpc());
		nativeUiSuppressedUntilGameCycle.remove(event.getNpc());
	}

	@Subscribe
	public void onPlayerDespawned(PlayerDespawned event)
	{
		hitsplats.remove(event.getPlayer());
		nativeUiSuppressedUntilGameCycle.remove(event.getPlayer());
	}

	private void cleanupExpiredHitsplats()
	{
		int gameCycle = client.getGameCycle();
		hitsplats.entrySet().removeIf(entry ->
		{
			entry.getValue().removeIf(hitsplat -> shouldRemoveFromTracking(entry.getKey(), hitsplat, gameCycle));
			return entry.getValue().isEmpty();
		});
		nativeUiSuppressedUntilGameCycle.entrySet().removeIf(entry -> entry.getValue() < gameCycle);
	}

	private void migrateLegacyConfig()
	{
		migrateLegacyLayoutConfig();
		migrateLegacySpacingConfig();
	}

	private void migrateLegacyLayoutConfig()
	{
		String layoutMode = configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_LAYOUT_MODE_KEY);
		String layoutStyle = configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_LAYOUT_STYLE_KEY);
		if (layoutMode == null && layoutStyle == null)
		{
			return;
		}

		HitsplatCustomizerLayoutShape layoutShape = null;
		HitsplatCustomizerLayoutBehavior layoutBehavior = null;
		HitsplatCustomizerLayoutDirection layoutDirection = null;
		if (layoutMode != null)
		{
			if ("SYMMETRIC".equals(layoutMode))
			{
				layoutShape = HitsplatCustomizerLayoutShape.HEXAGONAL;
				layoutBehavior = HitsplatCustomizerLayoutBehavior.SYMMETRIC;
			}
			else if ("COUNTERCLOCKWISE".equals(layoutMode))
			{
				layoutDirection = HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE;
			}
			else if ("CLOCKWISE".equals(layoutMode) || LEGACY_TRIANGULAR_LAYOUT_MODE.equals(layoutMode))
			{
				layoutDirection = HitsplatCustomizerLayoutDirection.CLOCKWISE;
			}
		}

		if (layoutStyle != null)
		{
			if ("DIAMOND".equals(layoutStyle))
			{
				layoutShape = HitsplatCustomizerLayoutShape.DIAMOND;
				if (layoutBehavior == null)
				{
					layoutBehavior = HitsplatCustomizerLayoutBehavior.INCREMENTAL;
				}
			}
			else if ("CIRCULAR".equals(layoutStyle))
			{
				layoutShape = HitsplatCustomizerLayoutShape.HEXAGONAL;
				if (layoutBehavior == null)
				{
					layoutBehavior = HitsplatCustomizerLayoutBehavior.INCREMENTAL;
				}
			}
			else if ("SYMMETRIC".equals(layoutStyle))
			{
				layoutShape = HitsplatCustomizerLayoutShape.HEXAGONAL;
				layoutBehavior = HitsplatCustomizerLayoutBehavior.SYMMETRIC;
			}
		}

		if (layoutShape != null && configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LAYOUT_SHAPE_KEY) == null)
		{
			configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LAYOUT_SHAPE_KEY, layoutShape.name());
		}

		if (layoutBehavior != null && configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LAYOUT_BEHAVIOR_KEY) == null)
		{
			configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LAYOUT_BEHAVIOR_KEY, layoutBehavior.name());
		}

		if (layoutDirection != null && configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LAYOUT_DIRECTION_KEY) == null)
		{
			configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LAYOUT_DIRECTION_KEY, layoutDirection.name());
		}

		configManager.unsetConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_LAYOUT_MODE_KEY);
		configManager.unsetConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_LAYOUT_STYLE_KEY);
	}

	private void migrateLegacySpacingConfig()
	{
		String slotSpacing = configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_SLOT_SPACING_KEY);
		String xSpacing = configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_X_SPACING_KEY);
		String ySpacing = configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_Y_SPACING_KEY);
		if (slotSpacing == null && xSpacing == null && ySpacing == null)
		{
			return;
		}

		if (configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.X_SPACING_KEY) == null)
		{
			configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.X_SPACING_KEY, roundedSpacing(xSpacing != null ? xSpacing : slotSpacing));
		}

		if (configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.Y_SPACING_KEY) == null)
		{
			configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.Y_SPACING_KEY, roundedSpacing(ySpacing != null ? ySpacing : slotSpacing));
		}

		configManager.unsetConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_SLOT_SPACING_KEY);
		configManager.unsetConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_X_SPACING_KEY);
		configManager.unsetConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_Y_SPACING_KEY);
	}

	private void applyPreset(HitsplatCustomizerPreset preset)
	{
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.ONLY_DISPLAY_MINE_KEY, preset.isOnlyDisplayMine());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.PRIORITIZE_MINE_KEY, preset.isPrioritizeMine());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.HIDE_ZERO_HITSPLATS_KEY, preset.isHideZeroHitsplats());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LAYOUT_SHAPE_KEY, preset.getLayoutShape().name());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LAYOUT_DIRECTION_KEY, preset.getLayoutDirection().name());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LAYOUT_BEHAVIOR_KEY, preset.getLayoutBehavior().name());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.MIN_RADIUS_KEY, preset.getMinRadius());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.MAX_RADIUS_KEY, preset.getMaxRadius());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.X_SPACING_KEY, preset.getXSpacing());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.Y_SPACING_KEY, preset.getYSpacing());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.GLOBAL_X_OFFSET_KEY, preset.getGlobalXOffset());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.GLOBAL_Y_OFFSET_KEY, preset.getGlobalYOffset());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.FADE_IN_DURATION_KEY, preset.getFadeInDuration());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.FULL_OPACITY_DURATION_KEY, preset.getFullOpacityDuration());
		configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.FADE_OUT_DURATION_KEY, preset.getFadeOutDuration());
	}

	private void refreshConfigPanel()
	{
		// RuneLite's config controls are rebuilt on profile refreshes, not on sibling config writes.
		eventBus.post(new ProfileChanged());
	}

	private static HitsplatCustomizerPreset presetFromValue(String value)
	{
		if (value == null)
		{
			return null;
		}

		for (HitsplatCustomizerPreset preset : HitsplatCustomizerPreset.values())
		{
			if (preset.toString().equals(value))
			{
				return preset;
			}
		}

		try
		{
			return HitsplatCustomizerPreset.valueOf(value);
		}
		catch (IllegalArgumentException ex)
		{
			return null;
		}
	}

	private static boolean isPresetControlledSetting(String key)
	{
		return HitsplatCustomizerConfig.ONLY_DISPLAY_MINE_KEY.equals(key)
			|| HitsplatCustomizerConfig.PRIORITIZE_MINE_KEY.equals(key)
			|| HitsplatCustomizerConfig.HIDE_ZERO_HITSPLATS_KEY.equals(key)
			|| HitsplatCustomizerConfig.LAYOUT_SHAPE_KEY.equals(key)
			|| HitsplatCustomizerConfig.LAYOUT_DIRECTION_KEY.equals(key)
			|| HitsplatCustomizerConfig.LAYOUT_BEHAVIOR_KEY.equals(key)
			|| HitsplatCustomizerConfig.MIN_RADIUS_KEY.equals(key)
			|| HitsplatCustomizerConfig.MAX_RADIUS_KEY.equals(key)
			|| HitsplatCustomizerConfig.X_SPACING_KEY.equals(key)
			|| HitsplatCustomizerConfig.Y_SPACING_KEY.equals(key)
			|| HitsplatCustomizerConfig.GLOBAL_X_OFFSET_KEY.equals(key)
			|| HitsplatCustomizerConfig.GLOBAL_Y_OFFSET_KEY.equals(key)
			|| HitsplatCustomizerConfig.FADE_IN_DURATION_KEY.equals(key)
			|| HitsplatCustomizerConfig.FULL_OPACITY_DURATION_KEY.equals(key)
			|| HitsplatCustomizerConfig.FADE_OUT_DURATION_KEY.equals(key);
	}

	private static int roundedSpacing(String value)
	{
		if (value == null)
		{
			return 0;
		}

		try
		{
			return Math.max(-64, Math.min(64, (int) Math.round(Double.parseDouble(value))));
		}
		catch (NumberFormatException ex)
		{
			return 0;
		}
	}

	private int getAvailablePosition(CopyOnWriteArrayList<HitsplatCustomizerHitsplat> actorHitsplats, int maxHitsplats, int gameCycle)
	{
		if (config.layoutBehavior() == HitsplatCustomizerLayoutBehavior.RANDOM)
		{
			return getRandomAvailablePosition(actorHitsplats, maxHitsplats, gameCycle);
		}

		return getLowestAvailablePosition(actorHitsplats, maxHitsplats, gameCycle);
	}

	private int getLowestAvailablePosition(CopyOnWriteArrayList<HitsplatCustomizerHitsplat> actorHitsplats, int maxHitsplats, int gameCycle)
	{
		int limit = positionLimit(actorHitsplats.size(), maxHitsplats);
		for (int position = 0; position < limit; position++)
		{
			if (!isPositionTaken(actorHitsplats, position, gameCycle))
			{
				return position;
			}
		}

		return -1;
	}

	private int getRandomAvailablePosition(CopyOnWriteArrayList<HitsplatCustomizerHitsplat> actorHitsplats, int maxHitsplats, int gameCycle)
	{
		int limit = hardPositionLimit(maxHitsplats);
		for (int position = 0; position < limit; )
		{
			int radius = HitsplatCustomizerLayout.radiusForPosition(position, config.minRadius(), config.layoutShape());
			List<Integer> availablePositions = new ArrayList<>();
			while (position < limit && HitsplatCustomizerLayout.radiusForPosition(position, config.minRadius(), config.layoutShape()) == radius)
			{
				if (!isPositionTaken(actorHitsplats, position, gameCycle))
				{
					availablePositions.add(position);
				}
				position++;
			}

			if (!availablePositions.isEmpty())
			{
				return availablePositions.get(ThreadLocalRandom.current().nextInt(availablePositions.size()));
			}
		}

		return -1;
	}

	private static boolean isPositionTaken(CopyOnWriteArrayList<HitsplatCustomizerHitsplat> actorHitsplats, int position, int gameCycle)
	{
		for (HitsplatCustomizerHitsplat hitsplat : actorHitsplats)
		{
			if (!hitsplat.isExpired(gameCycle) && hitsplat.getPosition() == position)
			{
				return true;
			}
		}

		return false;
	}

	static boolean shouldTreatAsMine(Hitsplat hitsplat)
	{
		return hitsplat.isMine() || !hitsplat.isOthers();
	}

	private int evictForPrioritizedMine(Actor actor, CopyOnWriteArrayList<HitsplatCustomizerHitsplat> actorHitsplats, int maxHitsplats, int gameCycle)
	{
		HitsplatCustomizerHitsplat victim = findOldestEvictableHitsplat(actor, actorHitsplats, maxHitsplats, gameCycle, false);
		if (victim == null)
		{
			victim = findOldestEvictableHitsplat(actor, actorHitsplats, maxHitsplats, gameCycle, true);
		}

		if (victim == null)
		{
			return -1;
		}

		actorHitsplats.remove(victim);
		return victim.getPosition();
	}

	private HitsplatCustomizerHitsplat findOldestEvictableHitsplat(
		Actor actor,
		CopyOnWriteArrayList<HitsplatCustomizerHitsplat> actorHitsplats,
		int maxHitsplats,
		int gameCycle,
		boolean includeMine)
	{
		HitsplatCustomizerHitsplat oldest = null;
		for (HitsplatCustomizerHitsplat hitsplat : actorHitsplats)
		{
			if (shouldRemoveFromTracking(actor, hitsplat, gameCycle)
				|| maxHitsplats > 0 && hitsplat.getPosition() >= maxHitsplats
				|| !includeMine && hitsplat.isMine())
			{
				continue;
			}

			if (oldest == null || hitsplat.getSequence() < oldest.getSequence())
			{
				oldest = hitsplat;
			}
		}

		return oldest;
	}

	private boolean shouldRemoveFromTracking(Actor actor, HitsplatCustomizerHitsplat hitsplat, int gameCycle)
	{
		return hitsplat.isExpired(gameCycle)
			|| shouldDisableHitsplatsForActor(actor)
			|| config.hideZeroHitsplats() && hitsplat.getAmount() == 0
			|| config.onlyDisplayMine() && !hitsplat.isMine()
			|| !HitsplatCustomizerLayout.isPositionWithinRadiusLimit(hitsplat.getPosition(), config.minRadius(), config.maxRadius(), config.layoutShape());
	}

	boolean shouldDisableHitsplatsForActor(Actor actor)
	{
		Player localPlayer = client.getLocalPlayer();
		if (actor == localPlayer)
		{
			return config.disableMyHitsplats();
		}

		if (actor instanceof NPC)
		{
			return config.disableEnemyHitsplats();
		}

		if (actor instanceof Player)
		{
			return config.disableAllyHitsplats();
		}

		return false;
	}

	private int positionLimit(int activeHitsplatCount, int maxHitsplats)
	{
		int limit = maxHitsplats <= 0 ? activeHitsplatCount + 1 : maxHitsplats;
		int radiusLimit = HitsplatCustomizerLayout.slotLimit(config.minRadius(), config.maxRadius(), config.layoutShape());
		if (radiusLimit != Integer.MAX_VALUE)
		{
			limit = Math.min(limit, radiusLimit);
		}

		return Math.max(0, limit);
	}

	private int hardPositionLimit(int maxHitsplats)
	{
		int limit = maxHitsplats <= 0 ? Integer.MAX_VALUE : maxHitsplats;
		int radiusLimit = HitsplatCustomizerLayout.slotLimit(config.minRadius(), config.maxRadius(), config.layoutShape());
		if (radiusLimit != Integer.MAX_VALUE)
		{
			limit = Math.min(limit, radiusLimit);
		}

		return Math.max(0, limit);
	}

	private int fadeInCycles()
	{
		return Math.max(0, config.fadeInDuration() / GAME_CYCLE_MILLIS);
	}

	private int fullOpacityCycles()
	{
		return Math.max(0, config.fullOpacityDuration() / GAME_CYCLE_MILLIS);
	}

	private int fadeOutCycles()
	{
		return Math.max(0, config.fadeOutDuration() / GAME_CYCLE_MILLIS);
	}

	private int totalDurationCycles()
	{
		return fadeInCycles() + fullOpacityCycles() + fadeOutCycles();
	}

	private void suppressNativeActorUi(Actor actor, int gameCycle)
	{
		int suppressUntilGameCycle = gameCycle + Math.max(MIN_NATIVE_SUPPRESSION_CYCLES, totalDurationCycles());
		nativeUiSuppressedUntilGameCycle.merge(actor, suppressUntilGameCycle, Math::max);
	}

	boolean isNativeUiSuppressed(Actor actor, int gameCycle)
	{
		Integer suppressedUntilGameCycle = nativeUiSuppressedUntilGameCycle.get(actor);
		return suppressedUntilGameCycle != null && suppressedUntilGameCycle >= gameCycle;
	}

	@Provides
	HitsplatCustomizerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HitsplatCustomizerConfig.class);
	}
}
