package com.customizealot;

import static net.runelite.api.HitsplatID.DAMAGE_ME;
import static net.runelite.api.HitsplatID.DAMAGE_OTHER;
import static net.runelite.api.HitsplatID.HEAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.runelite.api.Hitsplat;
import org.junit.Test;

public class CustomizeALotMineFilterTest
{
	@Test
	public void treatsStandardMineHitsplatsAsMine()
	{
		assertTrue(CustomizeALotPlugin.shouldTreatAsMine(hitsplat(DAMAGE_ME)));
	}

	@Test
	public void treatsStandardOtherHitsplatsAsNotMine()
	{
		assertFalse(CustomizeALotPlugin.shouldTreatAsMine(hitsplat(DAMAGE_OTHER)));
	}

	@Test
	public void treatsUnassignedHitsplatsAsNotMine()
	{
		assertFalse(CustomizeALotPlugin.shouldTreatAsMine(hitsplat(HEAL)));
	}

	@Test
	public void convertsLegacyOpacityToPercent()
	{
		assertEquals(100, CustomizeALotPlugin.opacityPercent("1.0"));
		assertEquals(75, CustomizeALotPlugin.opacityPercent("0.75"));
		assertEquals(33, CustomizeALotPlugin.opacityPercent("0.333"));
		assertEquals(75, CustomizeALotPlugin.opacityPercent("75"));
		assertEquals(0, CustomizeALotPlugin.opacityPercent("-1"));
		assertEquals(100, CustomizeALotPlugin.opacityPercent("200"));
		assertEquals(100, CustomizeALotPlugin.opacityPercent("not a number"));
	}

	private static Hitsplat hitsplat(int hitsplatType)
	{
		return new Hitsplat()
		{
			@Override
			public int getHitsplatType()
			{
				return hitsplatType;
			}

			@Override
			public int getAmount()
			{
				return 1;
			}

			@Override
			public int getDisappearsOnGameCycle()
			{
				return 0;
			}
		};
	}
}
