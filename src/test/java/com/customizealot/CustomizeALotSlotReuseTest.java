package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class CustomizeALotSlotReuseTest
{
	@Test
	public void reuseWindowRoundsUpToWholeGameCycles()
	{
		assertEquals(28, CustomizeALotPlugin.reuseWindowCycles(550));
		assertEquals(28, CustomizeALotPlugin.reuseWindowCycles(560));
		assertEquals(29, CustomizeALotPlugin.reuseWindowCycles(561));
		assertEquals(1, CustomizeALotPlugin.reuseWindowCycles(0));
	}

	@Test
	public void hitBecomesReusableAtExactBoundary()
	{
		CustomizeALotHitsplat old = hitsplat(4, 100, 0, false);

		assertNull(CustomizeALotPlugin.findTimedReuseVictim(
			Arrays.asList(old), 127, 28, false, false));
		assertSame(old, CustomizeALotPlugin.findTimedReuseVictim(
			Arrays.asList(old), 128, 28, false, false));
		assertEquals(4, old.getPosition());
	}

	@Test
	public void sameWindowHitsDoNotReplaceEachOther()
	{
		List<CustomizeALotHitsplat> hitsplats = new ArrayList<>();
		CustomizeALotHitsplat old = hitsplat(0, 100, 0, false);
		hitsplats.add(old);

		CustomizeALotHitsplat victim = CustomizeALotPlugin.findTimedReuseVictim(
			hitsplats, 128, 28, false, false);
		assertSame(old, victim);
		hitsplats.remove(victim);
		hitsplats.add(hitsplat(0, 128, 1, false));

		assertNull(CustomizeALotPlugin.findTimedReuseVictim(
			hitsplats, 128, 28, false, false));
	}

	@Test
	public void oldestVisibleAgeWinsThenSequenceBreaksTies()
	{
		CustomizeALotHitsplat firstSequence = hitsplat(0, 100, 0, false);
		CustomizeALotHitsplat olderDisplay = hitsplat(1, 90, 1, false);
		CustomizeALotHitsplat laterSequence = hitsplat(2, 100, 2, false);

		assertSame(olderDisplay, CustomizeALotPlugin.findTimedReuseVictim(
			Arrays.asList(firstSequence, olderDisplay, laterSequence), 130, 28, false, false));
		assertSame(firstSequence, CustomizeALotPlugin.findTimedReuseVictim(
			Arrays.asList(laterSequence, firstSequence), 130, 28, false, false));
	}

	@Test
	public void prioritizedMineChoosesEligibleNonMineFirst()
	{
		CustomizeALotHitsplat olderMine = hitsplat(0, 80, 0, true);
		CustomizeALotHitsplat nonMine = hitsplat(1, 90, 1, false);
		List<CustomizeALotHitsplat> hitsplats = Arrays.asList(olderMine, nonMine);

		assertSame(nonMine, CustomizeALotPlugin.findTimedReuseVictim(
			hitsplats, 130, 28, true, true));
		assertSame(olderMine, CustomizeALotPlugin.findTimedReuseVictim(
			hitsplats, 130, 28, true, false));
		assertSame(olderMine, CustomizeALotPlugin.findTimedReuseVictim(
			hitsplats, 130, 28, false, true));
	}

	@Test
	public void delayedAppearanceStartsReuseAgeLaterThanEvent()
	{
		assertEquals(140, CustomizeALotPlugin.reuseAgeStartsOnGameCycle(100, 140));
		assertEquals(140, CustomizeALotPlugin.reuseAgeStartsOnGameCycle(140, 100));
	}

	private static CustomizeALotHitsplat hitsplat(
		int position,
		int reuseAgeStartsOnGameCycle,
		long sequence,
		boolean mine)
	{
		return new CustomizeALotHitsplat(
			0,
			10,
			position,
			reuseAgeStartsOnGameCycle,
			reuseAgeStartsOnGameCycle,
			reuseAgeStartsOnGameCycle,
			reuseAgeStartsOnGameCycle + 100,
			reuseAgeStartsOnGameCycle + 200,
			sequence,
			mine);
	}
}
