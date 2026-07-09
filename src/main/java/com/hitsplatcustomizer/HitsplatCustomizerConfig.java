package com.hitsplatcustomizer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(HitsplatCustomizerConfig.GROUP)
public interface HitsplatCustomizerConfig extends Config
{
	String GROUP = "hitsplat-customizer";
	String LEGACY_LAYOUT_MODE_KEY = "layoutMode";
	String LEGACY_LAYOUT_STYLE_KEY = "layoutStyle";
	String LEGACY_SLOT_SPACING_KEY = "slotSpacing";
	String LEGACY_X_SPACING_KEY = "xSpacing";
	String LEGACY_Y_SPACING_KEY = "ySpacing";
	String PRESET_KEY = "preset";
	String DISABLE_ENEMY_HITSPLATS_KEY = "disableEnemyHitsplats";
	String DISABLE_ALLY_HITSPLATS_KEY = "disableAllyHitsplats";
	String DISABLE_MY_HITSPLATS_KEY = "disableMyHitsplats";
	String MAX_HITSPLATS_KEY = "maxHitsplats";
	String OPACITY_KEY = "opacity";
	String ONLY_DISPLAY_MINE_KEY = "onlyDisplayMine";
	String PRIORITIZE_MINE_KEY = "prioritizeMine";
	String HIDE_ZERO_HITSPLATS_KEY = "hideZeroHitsplats";
	String LAYOUT_SHAPE_KEY = "layoutShape";
	String LAYOUT_DIRECTION_KEY = "layoutDirection";
	String LAYOUT_BEHAVIOR_KEY = "layoutBehavior";
	String MIN_RADIUS_KEY = "minRadius";
	String MAX_RADIUS_KEY = "maxRadius";
	String X_SPACING_KEY = "xSpacingPixels";
	String Y_SPACING_KEY = "ySpacingPixels";
	String GLOBAL_X_OFFSET_KEY = "globalXOffset";
	String GLOBAL_Y_OFFSET_KEY = "globalYOffset";
	String LARGE_TARGET_SIZE_KEY = "largeTargetSize";
	String LARGE_TARGET_X_OFFSET_KEY = "largeTargetXOffset";
	String LARGE_TARGET_Y_OFFSET_KEY = "largeTargetYOffset";
	String FADE_IN_DURATION_KEY = "fadeInDuration";
	String FULL_OPACITY_DURATION_KEY = "fullOpacityDuration";
	String FADE_OUT_DURATION_KEY = "fadeOutDuration";

	@ConfigSection(
		name = "General",
		description = "Plugin-wide controls that presets do not change.",
		position = 1
	)
	String GENERAL_SECTION = "general";

	@ConfigSection(
		name = "Display",
		description = "Hitsplat filtering, layout, spacing, offsets, and animation.",
		position = 2
	)
	String DISPLAY_SECTION = "display";

	@ConfigItem(
		keyName = PRESET_KEY,
		name = "Preset",
		description = "Choose a preset to copy its values into the settings once. Editing any setting marks this as Custom.",
		position = 0
	)
	default HitsplatCustomizerPreset preset()
	{
		return HitsplatCustomizerPreset.DEFAULT;
	}

	@ConfigItem(
		keyName = DISABLE_ENEMY_HITSPLATS_KEY,
		name = "Disable all enemy hitsplats",
		description = "Hide hitsplats on NPCs. Native hitsplats are suppressed while hidden.",
		section = GENERAL_SECTION,
		position = 1
	)
	default boolean disableEnemyHitsplats()
	{
		return HitsplatCustomizerPreset.DEFAULT.isDisableEnemyHitsplats();
	}

	@ConfigItem(
		keyName = DISABLE_ALLY_HITSPLATS_KEY,
		name = "Disable all ally hitsplats",
		description = "Hide hitsplats on other players. Native hitsplats are suppressed while hidden.",
		section = GENERAL_SECTION,
		position = 2
	)
	default boolean disableAllyHitsplats()
	{
		return HitsplatCustomizerPreset.DEFAULT.isDisableAllyHitsplats();
	}

	@ConfigItem(
		keyName = DISABLE_MY_HITSPLATS_KEY,
		name = "Disable all my hitsplats",
		description = "Hide hitsplats on your player. Native hitsplats are suppressed while hidden.",
		section = GENERAL_SECTION,
		position = 3
	)
	default boolean disableMyHitsplats()
	{
		return HitsplatCustomizerPreset.DEFAULT.isDisableMyHitsplats();
	}

	@ConfigItem(
		keyName = MAX_HITSPLATS_KEY,
		name = "Max hitsplats",
		description = "Maximum visible hitsplats per actor. Set to 0 for no cap.",
		section = GENERAL_SECTION,
		position = 4
	)
	@Range(
		min = 0,
		max = 512
	)
	default int maxHitsplats()
	{
		return HitsplatCustomizerPreset.DEFAULT.getMaxHitsplats();
	}

	@ConfigItem(
		keyName = OPACITY_KEY,
		name = "Opacity",
		description = "Overall hitsplat opacity. 1.0 is fully opaque.",
		section = GENERAL_SECTION,
		position = 5
	)
	default double opacity()
	{
		return 1.0;
	}

	@ConfigItem(
		keyName = ONLY_DISPLAY_MINE_KEY,
		name = "My hits only",
		description = "Only show hitsplats caused by you. Other native hitsplats are still suppressed while this plugin is active.",
		section = DISPLAY_SECTION,
		position = 1
	)
	default boolean onlyDisplayMine()
	{
		return HitsplatCustomizerPreset.DEFAULT.isOnlyDisplayMine();
	}

	@ConfigItem(
		keyName = PRIORITIZE_MINE_KEY,
		name = "Prioritize my hits",
		description = "When visible splats are limited, a new hit from you replaces the oldest hit from someone else first, then your oldest hit if needed.",
		section = DISPLAY_SECTION,
		position = 2
	)
	default boolean prioritizeMine()
	{
		return HitsplatCustomizerPreset.DEFAULT.isPrioritizeMine();
	}

	@ConfigItem(
		keyName = HIDE_ZERO_HITSPLATS_KEY,
		name = "Hide zero hitsplats",
		description = "Hide misses and other hitsplats with an amount of 0.",
		section = DISPLAY_SECTION,
		position = 3
	)
	default boolean hideZeroHitsplats()
	{
		return HitsplatCustomizerPreset.DEFAULT.isHideZeroHitsplats();
	}

	@ConfigItem(
		keyName = LAYOUT_SHAPE_KEY,
		name = "Shape",
		description = "Hexagonal uses six-sided rings. Diamond uses four-corner rings. Grid uses square rings. X uses diagonal arms.",
		section = DISPLAY_SECTION,
		position = 4
	)
	default HitsplatCustomizerLayoutShape layoutShape()
	{
		return HitsplatCustomizerPreset.DEFAULT.getLayoutShape();
	}

	@ConfigItem(
		keyName = LAYOUT_DIRECTION_KEY,
		name = "Direction",
		description = "Clockwise starts at top and moves right. Counterclockwise starts at top and moves left.",
		section = DISPLAY_SECTION,
		position = 5
	)
	default HitsplatCustomizerLayoutDirection layoutDirection()
	{
		return HitsplatCustomizerPreset.DEFAULT.getLayoutDirection();
	}

	@ConfigItem(
		keyName = LAYOUT_BEHAVIOR_KEY,
		name = "Behavior",
		description = "Incremental fills slots in order. Symmetrical alternates opposite slots. Random chooses an open slot from the current radius.",
		section = DISPLAY_SECTION,
		position = 6
	)
	default HitsplatCustomizerLayoutBehavior layoutBehavior()
	{
		return HitsplatCustomizerPreset.DEFAULT.getLayoutBehavior();
	}

	@ConfigItem(
		keyName = MIN_RADIUS_KEY,
		name = "Min radius",
		description = "First layout radius to use. 0 starts at the center; 1 skips the center and starts on the first ring.",
		section = DISPLAY_SECTION,
		position = 7
	)
	@Range(
		min = 0,
		max = 64
	)
	default int minRadius()
	{
		return HitsplatCustomizerPreset.DEFAULT.getMinRadius();
	}

	@ConfigItem(
		keyName = MAX_RADIUS_KEY,
		name = "Max radius",
		description = "Maximum number of layers from the center. 0 means no cap; 1 means center only; 2 means center plus the first ring.",
		section = DISPLAY_SECTION,
		position = 8
	)
	@Range(
		min = 0,
		max = 64
	)
	default int maxRadius()
	{
		return HitsplatCustomizerPreset.DEFAULT.getMaxRadius();
	}

	@ConfigItem(
		keyName = X_SPACING_KEY,
		name = "X spacing",
		description = "Extra horizontal pixels between adjacent hitsplat slots. Negative values pull slots closer together.",
		section = DISPLAY_SECTION,
		position = 9
	)
	@Range(
		min = -64,
		max = 64
	)
	default int xSpacing()
	{
		return HitsplatCustomizerPreset.DEFAULT.getXSpacing();
	}

	@ConfigItem(
		keyName = Y_SPACING_KEY,
		name = "Y spacing",
		description = "Extra vertical pixels between adjacent hitsplat slots. Negative values pull slots closer together.",
		section = DISPLAY_SECTION,
		position = 10
	)
	@Range(
		min = -64,
		max = 64
	)
	default int ySpacing()
	{
		return HitsplatCustomizerPreset.DEFAULT.getYSpacing();
	}

	@ConfigItem(
		keyName = GLOBAL_X_OFFSET_KEY,
		name = "Global X offset",
		description = "Horizontal pixel offset applied to every hitsplat.",
		section = DISPLAY_SECTION,
		position = 11
	)
	@Range(
		min = -256,
		max = 256
	)
	default int globalXOffset()
	{
		return HitsplatCustomizerPreset.DEFAULT.getGlobalXOffset();
	}

	@ConfigItem(
		keyName = GLOBAL_Y_OFFSET_KEY,
		name = "Global Y offset",
		description = "Vertical pixel offset applied to every hitsplat. Positive values move hitsplats upward.",
		section = DISPLAY_SECTION,
		position = 12
	)
	@Range(
		min = -256,
		max = 256
	)
	default int globalYOffset()
	{
		return HitsplatCustomizerPreset.DEFAULT.getGlobalYOffset();
	}

	@ConfigItem(
		keyName = LARGE_TARGET_SIZE_KEY,
		name = "Large target size",
		description = "Actor footprint size required for large-target offsets. 2 means targets at least 2x2 tiles.",
		section = DISPLAY_SECTION,
		position = 13
	)
	@Range(
		min = 1,
		max = 16
	)
	default int largeTargetSize()
	{
		return 2;
	}

	@ConfigItem(
		keyName = LARGE_TARGET_X_OFFSET_KEY,
		name = "Large target X offset",
		description = "Extra horizontal offset for actors at or above the large target size.",
		section = DISPLAY_SECTION,
		position = 14
	)
	@Range(
		min = -256,
		max = 256
	)
	default int largeTargetXOffset()
	{
		return 0;
	}

	@ConfigItem(
		keyName = LARGE_TARGET_Y_OFFSET_KEY,
		name = "Large target Y offset",
		description = "Extra vertical offset for actors at or above the large target size. Positive values move hitsplats upward.",
		section = DISPLAY_SECTION,
		position = 15
	)
	@Range(
		min = -256,
		max = 256
	)
	default int largeTargetYOffset()
	{
		return 0;
	}

	@ConfigItem(
		keyName = FADE_IN_DURATION_KEY,
		name = "Fade-in duration",
		description = "How long hitsplats take to appear. Total lifetime is fade-in + full opacity + fade-out; 560 ms is the conservative 1-tick target.",
		section = DISPLAY_SECTION,
		position = 16
	)
	@Units(Units.MILLISECONDS)
	@Range(
		min = 0,
		max = 60000
	)
	default int fadeInDuration()
	{
		return HitsplatCustomizerPreset.DEFAULT.getFadeInDuration();
	}

	@ConfigItem(
		keyName = FULL_OPACITY_DURATION_KEY,
		name = "Full opacity duration",
		description = "How long hitsplats stay fully visible after fading in. A 1120 ms total lifetime targets 2 ticks with the same conservative estimate.",
		section = DISPLAY_SECTION,
		position = 17
	)
	@Units(Units.MILLISECONDS)
	@Range(
		min = 0,
		max = 60000
	)
	default int fullOpacityDuration()
	{
		return HitsplatCustomizerPreset.DEFAULT.getFullOpacityDuration();
	}

	@ConfigItem(
		keyName = FADE_OUT_DURATION_KEY,
		name = "Fade-out duration",
		description = "How long hitsplats take to disappear after the full opacity duration.",
		section = DISPLAY_SECTION,
		position = 18
	)
	@Units(Units.MILLISECONDS)
	@Range(
		min = 0,
		max = 60000
	)
	default int fadeOutDuration()
	{
		return HitsplatCustomizerPreset.DEFAULT.getFadeOutDuration();
	}
}
