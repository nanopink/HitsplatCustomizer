package com.hitsplatcustomizer;

import static net.runelite.api.HitsplatID.DAMAGE_ME;
import static net.runelite.api.HitsplatID.DAMAGE_OTHER;
import static net.runelite.api.HitsplatID.HEAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.runelite.api.Hitsplat;
import org.junit.Test;

public class HitsplatCustomizerMineFilterTest
{
	@Test
	public void treatsStandardMineHitsplatsAsMine()
	{
		assertTrue(HitsplatCustomizerPlugin.shouldTreatAsMine(hitsplat(DAMAGE_ME)));
	}

	@Test
	public void treatsStandardOtherHitsplatsAsNotMine()
	{
		assertFalse(HitsplatCustomizerPlugin.shouldTreatAsMine(hitsplat(DAMAGE_OTHER)));
	}

	@Test
	public void treatsUnassignedHitsplatsAsNotMine()
	{
		assertFalse(HitsplatCustomizerPlugin.shouldTreatAsMine(hitsplat(HEAL)));
	}

	@Test
	public void convertsHitpointsExperienceToSyntheticDamage()
	{
		assertEquals(1, HitsplatCustomizerPlugin.damageFromHitpointsExperience(1));
		assertEquals(2, HitsplatCustomizerPlugin.damageFromHitpointsExperience(3));
		assertEquals(21, HitsplatCustomizerPlugin.damageFromHitpointsExperience(28));
		assertEquals(43, HitsplatCustomizerPlugin.damageFromHitpointsExperience(57));
		assertEquals(-1, HitsplatCustomizerPlugin.damageFromHitpointsExperience(0));
	}

	@Test
	public void convertsLegacyOpacityToPercent()
	{
		assertEquals(100, HitsplatCustomizerPlugin.opacityPercent("1.0"));
		assertEquals(75, HitsplatCustomizerPlugin.opacityPercent("0.75"));
		assertEquals(33, HitsplatCustomizerPlugin.opacityPercent("0.333"));
		assertEquals(75, HitsplatCustomizerPlugin.opacityPercent("75"));
		assertEquals(0, HitsplatCustomizerPlugin.opacityPercent("-1"));
		assertEquals(100, HitsplatCustomizerPlugin.opacityPercent("200"));
		assertEquals(100, HitsplatCustomizerPlugin.opacityPercent("not a number"));
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
