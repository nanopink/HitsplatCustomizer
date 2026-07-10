package com.hitsplatcustomizer;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Iterator;
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
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.Skill;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.StatChanged;
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
	private static final int FAKE_HIT_RENDER_DELAY_CYCLES = 2;
	private static final int FAKE_HIT_MATCH_WINDOW_CYCLES = 8;
	private static final int FAKE_HIT_REAL_SUPPRESSION_CYCLES = 45;
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

	private final Map<Actor, Integer> nativeUiAllowedUntilGameCycle = new ConcurrentHashMap<>();

	private final List<RecentMineHit> recentMineHits = new ArrayList<>();
	private final List<PendingFakeHit> pendingFakeHits = new ArrayList<>();

	private int lastHitpointsExperience = -1;
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
		refreshHitpointsExperience();
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
		nativeUiAllowedUntilGameCycle.clear();
		recentMineHits.clear();
		pendingFakeHits.clear();
		lastHitpointsExperience = -1;
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
		if (shouldDisableHitsplatsForActor(actor))
		{
			suppressNativeActorUi(actor, gameCycle);
			return;
		}

		if (config.hideZeroHitsplats() && hitsplat.getAmount() == 0)
		{
			suppressNativeActorUi(actor, gameCycle);
			return;
		}

		boolean realMine = shouldTreatAsMine(hitsplat);
		boolean matchedFakeMineHit = hitsplat.getAmount() > 0
			&& shouldUseFakeMineHits()
			&& (consumeMatchingPendingFakeHit(actor, hitsplat.getAmount(), gameCycle)
				|| consumeMatchingActiveFakeHit(actor, hitsplat.getAmount(), gameCycle));
		boolean mine = realMine || matchedFakeMineHit;
		if (mine)
		{
			recordRealMineHit(actor, hitsplat.getAmount(), gameCycle);
		}

		if (config.onlyDisplayMine() && !mine)
		{
			suppressNativeActorUi(actor, gameCycle);
			return;
		}

		int hitsplatType = mine ? mineHitsplatTypeFor(hitsplat.getHitsplatType()) : hitsplat.getHitsplatType();
		addTrackedHitsplat(actor, hitsplatType, hitsplat.getAmount(), mine, gameCycle, false);
	}

	private HitsplatCustomizerHitsplat addTrackedHitsplat(Actor actor, Hitsplat hitsplat, boolean mine, int gameCycle)
	{
		return addTrackedHitsplat(
			actor,
			hitsplat.getHitsplatType(),
			hitsplat.getAmount(),
			mine,
			gameCycle,
			false);
	}

	private HitsplatCustomizerHitsplat addTrackedHitsplat(
		Actor actor,
		int hitsplatType,
		int amount,
		boolean mine,
		int gameCycle,
		boolean fake)
	{
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
			if (mine)
			{
				allowNativeActorUi(actor, gameCycle);
			}
			return null;
		}
		if (mine)
		{
			nativeUiAllowedUntilGameCycle.remove(actor);
		}
		suppressNativeActorUi(actor, gameCycle);

		int fadeInCycles = fadeInCycles();
		int fullOpacityCycles = fullOpacityCycles();
		int fadeOutCycles = fadeOutCycles();
		HitsplatCustomizerHitsplat trackedHitsplat = new HitsplatCustomizerHitsplat(
				hitsplatType,
				amount,
				position,
				gameCycle,
				gameCycle + fadeInCycles,
				gameCycle + fadeInCycles + fullOpacityCycles,
				gameCycle + fadeInCycles + fullOpacityCycles + fadeOutCycles,
				nextSequence++,
				mine,
				fake);
		actorHitsplats.add(trackedHitsplat);
		return trackedHitsplat;
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
			if ((HitsplatCustomizerConfig.FAKE_MINE_HITS_KEY.equals(event.getKey())
				|| HitsplatCustomizerConfig.ONLY_DISPLAY_MINE_KEY.equals(event.getKey()))
				&& !shouldUseFakeMineHits())
			{
				clearFakeMineHits();
			}

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
	public void onClientTick(ClientTick event)
	{
		flushPendingFakeHits(client.getGameCycle());
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		cleanupExpiredHitsplats();
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (event.getSkill() != Skill.HITPOINTS)
		{
			return;
		}

		int hitpointsExperience = event.getXp();
		if (lastHitpointsExperience < 0)
		{
			lastHitpointsExperience = hitpointsExperience;
			return;
		}

		int hitpointsExperienceGained = hitpointsExperience - lastHitpointsExperience;
		lastHitpointsExperience = hitpointsExperience;
		if (hitpointsExperienceGained <= 0 || !shouldUseFakeMineHits())
		{
			return;
		}

		int amount = damageFromHitpointsExperience(hitpointsExperienceGained);
		if (amount > 0)
		{
			queueFakeMineHit(amount, client.getGameCycle());
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			hitsplats.clear();
			nativeUiSuppressedUntilGameCycle.clear();
			nativeUiAllowedUntilGameCycle.clear();
			recentMineHits.clear();
			pendingFakeHits.clear();
			lastHitpointsExperience = -1;
			nextSequence = 0;
		}
		else
		{
			refreshHitpointsExperience();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		hitsplats.remove(event.getNpc());
		nativeUiSuppressedUntilGameCycle.remove(event.getNpc());
		nativeUiAllowedUntilGameCycle.remove(event.getNpc());
		removeFakeStateForActor(event.getNpc());
	}

	@Subscribe
	public void onPlayerDespawned(PlayerDespawned event)
	{
		hitsplats.remove(event.getPlayer());
		nativeUiSuppressedUntilGameCycle.remove(event.getPlayer());
		nativeUiAllowedUntilGameCycle.remove(event.getPlayer());
		removeFakeStateForActor(event.getPlayer());
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
		nativeUiAllowedUntilGameCycle.entrySet().removeIf(entry -> entry.getValue() < gameCycle);
		recentMineHits.removeIf(hit -> hit.isExpired(gameCycle));
		pendingFakeHits.removeIf(hit -> hit.isExpired(gameCycle));
	}

	private void refreshHitpointsExperience()
	{
		try
		{
			lastHitpointsExperience = client.getSkillExperience(Skill.HITPOINTS);
		}
		catch (RuntimeException ex)
		{
			lastHitpointsExperience = -1;
		}
	}

	private void recordRealMineHit(Actor actor, int amount, int gameCycle)
	{
		if (amount <= 0)
		{
			return;
		}

		recentMineHits.add(new RecentMineHit(actor, gameCycle));
		removeActiveFakeHits(actor, gameCycle);
	}

	private void queueFakeMineHit(int amount, int gameCycle)
	{
		Actor target = fakeHitTarget();
		if (target == null)
		{
			return;
		}

		if (shouldDisableHitsplatsForActor(target))
		{
			return;
		}

		if (hasRecentMineHit(target, gameCycle))
		{
			return;
		}

		if (useMatchingRealHitsplat(target, amount, gameCycle) || hasPendingFakeHit(target, amount, gameCycle))
		{
			return;
		}

		pendingFakeHits.add(new PendingFakeHit(
			target,
			amount,
			gameCycle,
			gameCycle + FAKE_HIT_RENDER_DELAY_CYCLES,
			gameCycle + FAKE_HIT_MATCH_WINDOW_CYCLES));
	}

	private Actor fakeHitTarget()
	{
		Player localPlayer = client.getLocalPlayer();
		return localPlayer == null ? null : localPlayer.getInteracting();
	}

	private boolean shouldUseFakeMineHits()
	{
		return config.onlyDisplayMine() && config.fakeMineHits();
	}

	private boolean hasRecentMineHit(Actor actor, int gameCycle)
	{
		for (RecentMineHit hit : recentMineHits)
		{
			if (hit.isExpired(gameCycle))
			{
				continue;
			}

			if (hit.matchesActor(actor))
			{
				return true;
			}
		}

		return false;
	}

	private void removeActiveFakeHits(Actor actor, int gameCycle)
	{
		CopyOnWriteArrayList<HitsplatCustomizerHitsplat> actorHitsplats = hitsplats.get(actor);
		if (actorHitsplats == null)
		{
			return;
		}

		actorHitsplats.removeIf(hitsplat -> hitsplat.isFake() && !hitsplat.isExpired(gameCycle));
	}

	private void removeFakeStateForActor(Actor actor)
	{
		recentMineHits.removeIf(hit -> hit.getActor() == actor);
		pendingFakeHits.removeIf(hit -> hit.getActor() == actor);
	}

	private void clearFakeMineHits()
	{
		recentMineHits.clear();
		pendingFakeHits.clear();
		hitsplats.values().forEach(actorHitsplats -> actorHitsplats.removeIf(HitsplatCustomizerHitsplat::isFake));
	}

	private void flushPendingFakeHits(int gameCycle)
	{
		Iterator<PendingFakeHit> it = pendingFakeHits.iterator();
		while (it.hasNext())
		{
			PendingFakeHit pendingHit = it.next();
			if (pendingHit.isExpired(gameCycle) || shouldDisableHitsplatsForActor(pendingHit.getActor()))
			{
				it.remove();
				continue;
			}

			if (!pendingHit.isReady(gameCycle))
			{
				continue;
			}

			it.remove();
			if (hasRecentMineHit(pendingHit.getActor(), gameCycle)
				|| useMatchingRealHitsplat(pendingHit.getActor(), pendingHit.getAmount(), gameCycle))
			{
				continue;
			}

			addTrackedHitsplat(
				pendingHit.getActor(),
				fakeHitsplatTypeFor(pendingHit.getAmount()),
				pendingHit.getAmount(),
				true,
				gameCycle,
				true);
		}
	}

	private boolean hasPendingFakeHit(Actor actor, int amount, int gameCycle)
	{
		for (PendingFakeHit pendingHit : pendingFakeHits)
		{
			if (pendingHit.matches(actor, amount, gameCycle))
			{
				return true;
			}
		}

		return false;
	}

	private boolean consumeMatchingPendingFakeHit(Actor actor, int amount, int gameCycle)
	{
		Iterator<PendingFakeHit> it = pendingFakeHits.iterator();
		while (it.hasNext())
		{
			PendingFakeHit pendingHit = it.next();
			if (pendingHit.isExpired(gameCycle))
			{
				it.remove();
				continue;
			}

			if (pendingHit.matches(actor, amount, gameCycle))
			{
				it.remove();
				return true;
			}
		}

		return false;
	}

	private boolean consumeMatchingActiveFakeHit(Actor actor, int amount, int gameCycle)
	{
		CopyOnWriteArrayList<HitsplatCustomizerHitsplat> actorHitsplats = hitsplats.get(actor);
		if (actorHitsplats == null)
		{
			return false;
		}

		return actorHitsplats.removeIf(hitsplat -> hitsplat.isFake()
			&& hitsplat.getAmount() == amount
			&& !hitsplat.isExpired(gameCycle)
			&& isWithinFakeMatchWindow(hitsplat.getAppearsOnGameCycle(), gameCycle));
	}

	private boolean useMatchingRealHitsplat(Actor actor, int amount, int gameCycle)
	{
		CopyOnWriteArrayList<HitsplatCustomizerHitsplat> actorHitsplats = hitsplats.get(actor);
		if (actorHitsplats == null)
		{
			return false;
		}

		int matchingIndex = -1;
		HitsplatCustomizerHitsplat matchingHitsplat = null;
		for (int i = 0; i < actorHitsplats.size(); i++)
		{
			HitsplatCustomizerHitsplat hitsplat = actorHitsplats.get(i);
			if (!hitsplat.isFake()
				&& !hitsplat.isMine()
				&& hitsplat.getAmount() == amount
				&& !hitsplat.isExpired(gameCycle)
				&& isWithinFakeMatchWindow(hitsplat.getAppearsOnGameCycle(), gameCycle))
			{
				if (matchingHitsplat != null)
				{
					return !config.onlyDisplayMine();
				}

				matchingIndex = i;
				matchingHitsplat = hitsplat;
			}
		}

		if (matchingHitsplat == null)
		{
			return false;
		}

		actorHitsplats.set(matchingIndex, matchingHitsplat.asMine(mineHitsplatTypeFor(matchingHitsplat.getHitsplatType())));
		recordRealMineHit(actor, amount, gameCycle);
		return true;
	}

	private static boolean isWithinFakeMatchWindow(int hitsplatGameCycle, int gameCycle)
	{
		return Math.abs(gameCycle - hitsplatGameCycle) <= FAKE_HIT_MATCH_WINDOW_CYCLES;
	}

	private void migrateLegacyConfig()
	{
		migrateLegacyOpacityConfig();
		migrateLegacyLayoutConfig();
		migrateLegacySpacingConfig();
	}

	private void migrateLegacyOpacityConfig()
	{
		String opacity = configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_OPACITY_KEY);
		if (opacity == null)
		{
			return;
		}

		if (configManager.getConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.OPACITY_PERCENT_KEY) == null)
		{
			configManager.setConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.OPACITY_PERCENT_KEY, opacityPercent(opacity));
		}

		configManager.unsetConfiguration(HitsplatCustomizerConfig.GROUP, HitsplatCustomizerConfig.LEGACY_OPACITY_KEY);
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

	static int opacityPercent(String value)
	{
		if (value == null)
		{
			return 100;
		}

		try
		{
			double opacity = Double.parseDouble(value);
			if (Double.isNaN(opacity) || Double.isInfinite(opacity))
			{
				return 100;
			}

			double percent = opacity <= 1.0 ? opacity * 100.0 : opacity;
			return Math.max(0, Math.min(100, (int) Math.round(percent)));
		}
		catch (NumberFormatException ex)
		{
			return 100;
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
		return hitsplat.isMine();
	}

	static int mineHitsplatTypeFor(int hitsplatType)
	{
		switch (hitsplatType)
		{
			case HitsplatID.DAMAGE_OTHER:
				return HitsplatID.DAMAGE_ME;
			case HitsplatID.DAMAGE_OTHER_CYAN:
				return HitsplatID.DAMAGE_ME_CYAN;
			case HitsplatID.DAMAGE_OTHER_ORANGE:
				return HitsplatID.DAMAGE_ME_ORANGE;
			case HitsplatID.DAMAGE_OTHER_YELLOW:
				return HitsplatID.DAMAGE_ME_YELLOW;
			case HitsplatID.DAMAGE_OTHER_WHITE:
				return HitsplatID.DAMAGE_ME_WHITE;
			case HitsplatID.DAMAGE_OTHER_POISE:
				return HitsplatID.DAMAGE_ME_POISE;
			default:
				return hitsplatType;
		}
	}

	private int fakeHitsplatTypeFor(int amount)
	{
		return fakeHitsplatTypeFor(amount, config.fakeMaxHitAmount());
	}

	static int fakeHitsplatTypeFor(int amount, int fakeMaxHitAmount)
	{
		return fakeMaxHitAmount > 0 && amount >= fakeMaxHitAmount
			? HitsplatID.DAMAGE_MAX_ME
			: HitsplatID.DAMAGE_ME;
	}

	static int damageFromHitpointsExperience(int hitpointsExperienceGained)
	{
		if (hitpointsExperienceGained <= 0)
		{
			return -1;
		}

		int amount = (int) Math.round(hitpointsExperienceGained * 3.0 / 4.0);
		return hitpointsExperienceMatchesDamage(hitpointsExperienceGained, amount) ? amount : -1;
	}

	static boolean hitpointsExperienceMatchesDamage(int hitpointsExperienceGained, int amount)
	{
		if (hitpointsExperienceGained <= 0 || amount <= 0)
		{
			return false;
		}

		double expectedHitpointsExperience = amount * 4.0 / 3.0;
		return Math.abs(hitpointsExperienceGained - expectedHitpointsExperience) <= 1.0;
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
		if (isNativeUiAllowed(actor, gameCycle))
		{
			return;
		}

		int suppressUntilGameCycle = gameCycle + Math.max(MIN_NATIVE_SUPPRESSION_CYCLES, totalDurationCycles());
		nativeUiSuppressedUntilGameCycle.merge(actor, suppressUntilGameCycle, Math::max);
	}

	private void allowNativeActorUi(Actor actor, int gameCycle)
	{
		int allowedUntilGameCycle = gameCycle + Math.max(MIN_NATIVE_SUPPRESSION_CYCLES, totalDurationCycles());
		nativeUiAllowedUntilGameCycle.merge(actor, allowedUntilGameCycle, Math::max);
		nativeUiSuppressedUntilGameCycle.remove(actor);
	}

	boolean isNativeUiAllowed(Actor actor, int gameCycle)
	{
		Integer allowedUntilGameCycle = nativeUiAllowedUntilGameCycle.get(actor);
		return allowedUntilGameCycle != null && allowedUntilGameCycle >= gameCycle;
	}

	boolean isNativeUiSuppressed(Actor actor, int gameCycle)
	{
		if (isNativeUiAllowed(actor, gameCycle))
		{
			return false;
		}

		Integer suppressedUntilGameCycle = nativeUiSuppressedUntilGameCycle.get(actor);
		return suppressedUntilGameCycle != null && suppressedUntilGameCycle >= gameCycle;
	}

	private static final class RecentMineHit
	{
		private final Actor actor;
		private final int gameCycle;

		private RecentMineHit(Actor actor, int gameCycle)
		{
			this.actor = actor;
			this.gameCycle = gameCycle;
		}

		private Actor getActor()
		{
			return actor;
		}

		private boolean matchesActor(Actor actor)
		{
			return this.actor == actor;
		}

		private boolean isExpired(int currentGameCycle)
		{
			return gameCycle + FAKE_HIT_REAL_SUPPRESSION_CYCLES < currentGameCycle;
		}
	}

	private static final class PendingFakeHit
	{
		private final Actor actor;
		private final int amount;
		private final int createdOnGameCycle;
		private final int renderOnGameCycle;
		private final int expiresOnGameCycle;

		private PendingFakeHit(Actor actor, int amount, int createdOnGameCycle, int renderOnGameCycle, int expiresOnGameCycle)
		{
			this.actor = actor;
			this.amount = amount;
			this.createdOnGameCycle = createdOnGameCycle;
			this.renderOnGameCycle = renderOnGameCycle;
			this.expiresOnGameCycle = expiresOnGameCycle;
		}

		private Actor getActor()
		{
			return actor;
		}

		private int getAmount()
		{
			return amount;
		}

		private boolean isReady(int gameCycle)
		{
			return gameCycle >= renderOnGameCycle;
		}

		private boolean isExpired(int gameCycle)
		{
			return gameCycle > expiresOnGameCycle;
		}

		private boolean matches(Actor actor, int amount, int gameCycle)
		{
			return this.actor == actor
				&& this.amount == amount
				&& gameCycle >= createdOnGameCycle
				&& !isExpired(gameCycle);
		}
	}

	@Provides
	HitsplatCustomizerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HitsplatCustomizerConfig.class);
	}
}
