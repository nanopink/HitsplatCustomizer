package com.customizealot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CustomizeALotTargetDetectionTest
{
	@Test
	public void footprintModeUsesOnlyTheConfiguredFootprintThreshold()
	{
		assertFalse(CustomizeALotPlugin.detectsBossLikeTarget(
			CustomizeALotTargetDetection.FOOTPRINT,
			1,
			200,
			2,
			40));
		assertTrue(CustomizeALotPlugin.detectsBossLikeTarget(
			CustomizeALotTargetDetection.FOOTPRINT,
			2,
			1,
			2,
			40));
	}

	@Test
	public void publicHealthScaleModeClampsLegacyThresholdsBelowOneHundred()
	{
		assertFalse(CustomizeALotPlugin.detectsBossLikeTarget(
			CustomizeALotTargetDetection.HEALTH_SCALE,
			8,
			99,
			2,
			40));
		assertTrue(CustomizeALotPlugin.detectsBossLikeTarget(
			CustomizeALotTargetDetection.HEALTH_SCALE,
			1,
			100,
			2,
			40));
	}

	@Test
	public void eitherModeCatchesLargeFootprintsAndOneTileHighScaleTargets()
	{
		assertTrue(CustomizeALotPlugin.detectsBossLikeTarget(
			CustomizeALotTargetDetection.EITHER,
			2,
			30,
			2,
			40));
		assertTrue(CustomizeALotPlugin.detectsBossLikeTarget(
			CustomizeALotTargetDetection.EITHER,
			1,
			100,
			2,
			40));
		assertFalse(CustomizeALotPlugin.detectsBossLikeTarget(
			CustomizeALotTargetDetection.EITHER,
			1,
			30,
			2,
			40));
	}

	@Test
	public void nullModeFallsBackToEitherAndThresholdsAreClamped()
	{
		assertTrue(CustomizeALotPlugin.detectsBossLikeTarget(
			null,
			1,
			1,
			0,
			0));
	}

	@Test
	public void classificationSurvivesMissingHealthButNotAnNpcTransform()
	{
		assertTrue(CustomizeALotPlugin.canReuseTargetClassification(
			2, 100, 42,
			2, -1, 42));
		assertFalse(CustomizeALotPlugin.canReuseTargetClassification(
			2, 100, 42,
			1, -1, 42));
		assertFalse(CustomizeALotPlugin.canReuseTargetClassification(
			2, 100, 42,
			2, -1, 43));
		assertFalse(CustomizeALotPlugin.canReuseTargetClassification(
			2, 100, 42,
			2, 30, 42));
	}
}
