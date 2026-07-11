package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CustomizeALotPresetTest
{
	@Test
	public void defaultPresetIsRuneScape()
	{
		assertEquals(CustomizeALotPreset.RUNESCAPE, CustomizeALotPreset.DEFAULT);
	}

	@Test
	public void customPresetRetainsOriginalDisplayValues()
	{
		assertDisplayValues(
			CustomizeALotPreset.CUSTOM,
			"Custom",
			CustomizeALotLayoutShape.HEXAGONAL,
			CustomizeALotLayoutBehavior.SYMMETRIC,
			0,
			3,
			-2,
			2,
			30,
			400,
			150);
	}

	@Test
	public void ruinedHeirsPresetRetainsOriginalDisplayValues()
	{
		assertDisplayValues(
			CustomizeALotPreset.RUINED_HEIRS_ONE_TICK,
			"Ruined Heir's 1 tick",
			CustomizeALotLayoutShape.HEXAGONAL,
			CustomizeALotLayoutBehavior.SYMMETRIC,
			0,
			2,
			-2,
			2,
			30,
			400,
			150);
	}

	@Test
	public void runescapePresetRetainsOriginalDisplayValues()
	{
		assertDisplayValues(
			CustomizeALotPreset.RUNESCAPE,
			"RuneScape",
			CustomizeALotLayoutShape.DIAMOND,
			CustomizeALotLayoutBehavior.INCREMENTAL,
			1,
			2,
			-8,
			-10,
			0,
			1000,
			0);
	}

	@Test
	public void standardPresetRetainsOriginalDisplayValues()
	{
		assertDisplayValues(
			CustomizeALotPreset.STANDARD,
			"Hexagon 2 ticks",
			CustomizeALotLayoutShape.HEXAGONAL,
			CustomizeALotLayoutBehavior.INCREMENTAL,
			0,
			3,
			-2,
			2,
			60,
			700,
			300);
	}

	@Test
	public void chaosPresetKeepsImprovedOuterRandomLayout()
	{
		assertDisplayValues(
			CustomizeALotPreset.CHAOS,
			"Chaos",
			CustomizeALotLayoutShape.HEXAGONAL,
			CustomizeALotLayoutBehavior.RANDOM,
			2,
			0,
			-2,
			2,
			30,
			1000,
			120);
	}

	private static void assertDisplayValues(
		CustomizeALotPreset preset,
		String name,
		CustomizeALotLayoutShape shape,
		CustomizeALotLayoutBehavior behavior,
		int minRadius,
		int maxRadius,
		int xSpacing,
		int ySpacing,
		int fadeIn,
		int fullOpacity,
		int fadeOut)
	{
		assertEquals(name, preset.toString());
		assertFalse(preset.isOnlyDisplayMine());
		assertTrue(preset.isPrioritizeMine());
		assertFalse(preset.isHideZeroHitsplats());
		assertEquals(shape, preset.getLayoutShape());
		assertEquals(CustomizeALotLayoutDirection.CLOCKWISE, preset.getLayoutDirection());
		assertEquals(behavior, preset.getLayoutBehavior());
		assertEquals(100, preset.getHitsplatScalePercent());
		assertEquals(minRadius, preset.getMinRadius());
		assertEquals(maxRadius, preset.getMaxRadius());
		assertEquals(xSpacing, preset.getXSpacing());
		assertEquals(ySpacing, preset.getYSpacing());
		assertEquals(fadeIn, preset.getFadeInDuration());
		assertEquals(fullOpacity, preset.getFullOpacityDuration());
		assertEquals(fadeOut, preset.getFadeOutDuration());
	}
}
