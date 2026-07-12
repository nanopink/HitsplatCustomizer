package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import net.runelite.client.config.FontType;
import org.junit.Test;

public class CustomizeALotPresetWorkflowTest
{
	@Test
	public void selectingNamedPresetCopiesEveryControlledDisplayValue()
	{
		Map<String, Object> settings = new LinkedHashMap<>();

		CustomizeALotPlugin.applyPreset(CustomizeALotPreset.RUNESCAPE, settings::put);

		assertEquals(15, settings.size());
		assertEquals(false, settings.get(CustomizeALotConfig.ONLY_DISPLAY_MINE_KEY));
		assertEquals(true, settings.get(CustomizeALotConfig.PRIORITIZE_MINE_KEY));
		assertEquals(false, settings.get(CustomizeALotConfig.HIDE_ZERO_HITSPLATS_KEY));
		assertEquals("DIAMOND", settings.get(CustomizeALotConfig.LAYOUT_SHAPE_KEY));
		assertEquals("CLOCKWISE", settings.get(CustomizeALotConfig.LAYOUT_DIRECTION_KEY));
		assertEquals("INCREMENTAL", settings.get(CustomizeALotConfig.LAYOUT_BEHAVIOR_KEY));
		assertEquals(100, settings.get(CustomizeALotConfig.HITSPLAT_SCALE_PERCENT_KEY));
		assertEquals(1, settings.get(CustomizeALotConfig.MIN_RADIUS_KEY));
		assertEquals(2, settings.get(CustomizeALotConfig.MAX_RADIUS_KEY));
		assertEquals(-8, settings.get(CustomizeALotConfig.X_SPACING_KEY));
		assertEquals(-10, settings.get(CustomizeALotConfig.Y_SPACING_KEY));
		assertEquals(FontType.SMALL, settings.get(CustomizeALotConfig.HITSPLAT_FONT_KEY));
		assertEquals(0, settings.get(CustomizeALotConfig.FADE_IN_DURATION_KEY));
		assertEquals(1000, settings.get(CustomizeALotConfig.FULL_OPACITY_DURATION_KEY));
		assertEquals(0, settings.get(CustomizeALotConfig.FADE_OUT_DURATION_KEY));
	}

	@Test
	public void editingControlledDisplayValueSelectsCustom()
	{
		assertTrue(CustomizeALotPlugin.shouldSwitchToCustom(
			false,
			CustomizeALotConfig.ONLY_DISPLAY_MINE_KEY,
			CustomizeALotPreset.RUNESCAPE));
		assertTrue(CustomizeALotPlugin.shouldSwitchToCustom(
			false,
			CustomizeALotConfig.FADE_OUT_DURATION_KEY,
			CustomizeALotPreset.CHAOS));
		assertTrue(CustomizeALotPlugin.shouldSwitchToCustom(
			false,
			CustomizeALotConfig.HITSPLAT_FONT_KEY,
			CustomizeALotPreset.RUNESCAPE));
		assertTrue(CustomizeALotPlugin.shouldSwitchToCustom(
			false,
			CustomizeALotConfig.HITSPLAT_SCALE_PERCENT_KEY,
			CustomizeALotPreset.RUNESCAPE));

		assertFalse(CustomizeALotPlugin.shouldSwitchToCustom(
			true,
			CustomizeALotConfig.ONLY_DISPLAY_MINE_KEY,
			CustomizeALotPreset.RUNESCAPE));
		assertFalse(CustomizeALotPlugin.shouldSwitchToCustom(
			false,
			CustomizeALotConfig.ONLY_DISPLAY_MINE_KEY,
			CustomizeALotPreset.CUSTOM));
		assertFalse(CustomizeALotPlugin.shouldSwitchToCustom(
			false,
			CustomizeALotConfig.MAX_HITSPLATS_KEY,
			CustomizeALotPreset.RUNESCAPE));
		assertFalse(CustomizeALotPlugin.shouldSwitchToCustom(
			false,
			CustomizeALotConfig.HITSPLAT_REUSE_INTERVAL_KEY,
			CustomizeALotPreset.RUNESCAPE));
		assertFalse(CustomizeALotPlugin.shouldSwitchToCustom(
			false,
			CustomizeALotConfig.OPACITY_PERCENT_KEY,
			CustomizeALotPreset.RUNESCAPE));
		assertFalse(CustomizeALotPlugin.shouldSwitchToCustom(
			false,
			CustomizeALotConfig.LARGE_TARGET_DETECTION_KEY,
			CustomizeALotPreset.RUNESCAPE));
	}

	@Test
	public void presetValueParserAcceptsStoredNameAndDisplayLabel()
	{
		assertEquals(
			CustomizeALotPreset.RUINED_HEIRS_ONE_TICK,
			CustomizeALotPlugin.presetFromValue("RUINED_HEIRS_ONE_TICK"));
		assertEquals(
			CustomizeALotPreset.RUINED_HEIRS_ONE_TICK,
			CustomizeALotPlugin.presetFromValue("Ruined Heir's 1 tick"));
		assertNull(CustomizeALotPlugin.presetFromValue("not-a-preset"));
		assertNull(CustomizeALotPlugin.presetFromValue(null));
	}

	@Test
	public void pendingCustomSelectionIsCancelledByProfileOrPresetChanges()
	{
		assertTrue(CustomizeALotPlugin.shouldApplyPendingCustomSelection(
			4,
			4,
			false,
			CustomizeALotPreset.RUNESCAPE));
		assertFalse(CustomizeALotPlugin.shouldApplyPendingCustomSelection(
			4,
			5,
			false,
			CustomizeALotPreset.RUNESCAPE));
		assertFalse(CustomizeALotPlugin.shouldApplyPendingCustomSelection(
			4,
			4,
			true,
			CustomizeALotPreset.RUNESCAPE));
		assertFalse(CustomizeALotPlugin.shouldApplyPendingCustomSelection(
			4,
			4,
			false,
			CustomizeALotPreset.CUSTOM));
	}

	@Test
	public void workflowMigrationPreservesMismatchedStoredValuesAsCustom()
	{
		CustomizeALotConfig matching = new CustomizeALotConfig()
		{
		};
		CustomizeALotConfig mismatched = new CustomizeALotConfig()
		{
			@Override
			public int xSpacing()
			{
				return 17;
			}
		};

		assertTrue(CustomizeALotPlugin.presetMatchesConfig(
			CustomizeALotPreset.RUNESCAPE,
			matching));
		assertFalse(CustomizeALotPlugin.presetMatchesConfig(
			CustomizeALotPreset.RUNESCAPE,
			mismatched));
	}

	@Test
	public void presetMatchIncludesTheCopiedHitsplatScale()
	{
		CustomizeALotConfig scaled = new CustomizeALotConfig()
		{
			@Override
			public int hitsplatScalePercent()
			{
				return 125;
			}
		};

		assertFalse(CustomizeALotPlugin.presetMatchesConfig(
			CustomizeALotPreset.DEFAULT,
			scaled));
	}

	@Test
	public void workflowMigrationRecognizesThePreviousChaosPreset()
	{
		CustomizeALotConfig oldChaos = new CustomizeALotConfig()
		{
			@Override
			public CustomizeALotLayoutShape layoutShape()
			{
				return CustomizeALotLayoutShape.HEXAGONAL;
			}

			@Override
			public CustomizeALotLayoutBehavior layoutBehavior()
			{
				return CustomizeALotLayoutBehavior.RANDOM;
			}

			@Override
			public int minRadius()
			{
				return 0;
			}

			@Override
			public int maxRadius()
			{
				return 0;
			}

			@Override
			public int xSpacing()
			{
				return -2;
			}

			@Override
			public int ySpacing()
			{
				return 2;
			}

			@Override
			public int fadeInDuration()
			{
				return 30;
			}

			@Override
			public int fullOpacityDuration()
			{
				return 1000;
			}

			@Override
			public int fadeOutDuration()
			{
				return 120;
			}
		};

		assertFalse(CustomizeALotPlugin.presetMatchesConfig(
			CustomizeALotPreset.CHAOS,
			oldChaos));
		assertTrue(CustomizeALotPlugin.legacyPresetMatchesConfig(
			CustomizeALotPreset.CHAOS,
			oldChaos));
	}
}
