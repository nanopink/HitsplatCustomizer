package com.customizealot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CustomizeALotActorUiVisibilityTrackerTest
{
	@Test
	public void requiresAcceptanceDuringTheCurrentGameCycle()
	{
		assertTrue(CustomizeALotActorUiVisibilityTracker.acceptedOn(100, 100));
		assertFalse(CustomizeALotActorUiVisibilityTracker.acceptedOn(99, 100));
		assertFalse(CustomizeALotActorUiVisibilityTracker.acceptedOn(Integer.MIN_VALUE, 100));
	}

	@Test
	public void suppressesOnlyActorUiWhileEnabled()
	{
		assertTrue(CustomizeALotActorUiVisibilityTracker.shouldSuppressActorUi(true, true, true));
		assertFalse(CustomizeALotActorUiVisibilityTracker.shouldSuppressActorUi(false, true, true));
		assertFalse(CustomizeALotActorUiVisibilityTracker.shouldSuppressActorUi(true, false, true));
		assertFalse(CustomizeALotActorUiVisibilityTracker.shouldSuppressActorUi(true, true, false));
	}
}
