package com.hitsplatcustomizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HitsplatCustomizerPresetTest
{
	@Test
	public void builtInPresetsUseSharedSpacingAndRadiusDefaults()
	{
		assertSharedSpacingDefaults(HitsplatCustomizerPreset.RUINED_HEIRS_ONE_TICK);
		assertSharedDefaults(HitsplatCustomizerPreset.STANDARD);
	}

	@Test
	public void defaultPresetIsRunescape()
	{
		assertEquals(HitsplatCustomizerPreset.RUNESCAPE, HitsplatCustomizerPreset.DEFAULT);
	}

	@Test
	public void runescapePresetUsesSkippedCenterDiamond()
	{
		HitsplatCustomizerPreset preset = HitsplatCustomizerPreset.RUNESCAPE;

		assertEquals("RuneScape", preset.toString());
		assertEquals(0, preset.getMaxHitsplats());
		assertEquals(HitsplatCustomizerLayoutShape.DIAMOND, preset.getLayoutShape());
		assertEquals(HitsplatCustomizerLayoutDirection.CLOCKWISE, preset.getLayoutDirection());
		assertEquals(HitsplatCustomizerLayoutBehavior.INCREMENTAL, preset.getLayoutBehavior());
		assertEquals(1, preset.getMinRadius());
		assertEquals(2, preset.getMaxRadius());
		assertEquals(-8, preset.getXSpacing());
		assertEquals(-10, preset.getYSpacing());
		assertEquals(0, preset.getGlobalXOffset());
		assertEquals(5, preset.getGlobalYOffset());
		assertEquals(0, preset.getFadeInDuration());
		assertEquals(1000, preset.getFullOpacityDuration());
		assertEquals(0, preset.getFadeOutDuration());
		assertTrue(preset.isPrioritizeMine());
		assertFalse(preset.isHideZeroHitsplats());
	}

	@Test
	public void ruinedHeirsPresetUsesTwoRadiusCap()
	{
		HitsplatCustomizerPreset preset = HitsplatCustomizerPreset.RUINED_HEIRS_ONE_TICK;

		assertEquals(2, preset.getMaxRadius());
	}

	@Test
	public void presetsShowZeroHitsplats()
	{
		for (HitsplatCustomizerPreset preset : HitsplatCustomizerPreset.values())
		{
			assertFalse(preset.isHideZeroHitsplats());
		}
	}

	@Test
	public void chaosPresetUsesRandomUncappedLongLifetime()
	{
		HitsplatCustomizerPreset chaos = HitsplatCustomizerPreset.CHAOS;

		assertEquals("Chaos", chaos.toString());
		assertEquals(HitsplatCustomizerLayoutShape.HEXAGONAL, chaos.getLayoutShape());
		assertEquals(HitsplatCustomizerLayoutDirection.CLOCKWISE, chaos.getLayoutDirection());
		assertEquals(HitsplatCustomizerLayoutBehavior.RANDOM, chaos.getLayoutBehavior());
		assertEquals(0, chaos.getMinRadius());
		assertEquals(0, chaos.getMaxRadius());
		assertEquals(-2, chaos.getXSpacing());
		assertEquals(2, chaos.getYSpacing());
		assertEquals(0, chaos.getGlobalXOffset());
		assertEquals(0, chaos.getGlobalYOffset());
		assertEquals(30, chaos.getFadeInDuration());
		assertEquals(1000, chaos.getFullOpacityDuration());
		assertEquals(120, chaos.getFadeOutDuration());
	}

	@Test
	public void standardPresetIsTwoTickHexagon()
	{
		HitsplatCustomizerPreset preset = HitsplatCustomizerPreset.STANDARD;

		assertEquals("Hexagon 2 ticks", preset.toString());
		assertEquals(HitsplatCustomizerLayoutShape.HEXAGONAL, preset.getLayoutShape());
		assertEquals(HitsplatCustomizerLayoutBehavior.INCREMENTAL, preset.getLayoutBehavior());
	}

	private static void assertSharedDefaults(HitsplatCustomizerPreset preset)
	{
		assertEquals(0, preset.getMinRadius());
		assertEquals(3, preset.getMaxRadius());
		assertSharedSpacingDefaults(preset);
	}

	private static void assertSharedSpacingDefaults(HitsplatCustomizerPreset preset)
	{
		assertEquals(-2, preset.getXSpacing());
		assertEquals(2, preset.getYSpacing());
		assertEquals(0, preset.getGlobalXOffset());
		assertEquals(0, preset.getGlobalYOffset());
	}
}
