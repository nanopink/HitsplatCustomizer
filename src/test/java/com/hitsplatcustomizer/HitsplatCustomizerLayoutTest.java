package com.hitsplatcustomizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HitsplatCustomizerLayoutTest
{
	@Test
	public void firstHexRingStartsTopAndMovesClockwiseWithoutGaps()
	{
		assertOffset(0, 0, 0);
		assertOffset(1, 0, -20);
		assertOffset(2, 23, -10);
		assertOffset(3, 23, 10);
		assertOffset(4, 0, 20);
		assertOffset(5, -23, 10);
		assertOffset(6, -23, -10);
	}

	@Test
	public void secondHexRingExpandsAfterFirstRing()
	{
		assertOffset(7, 0, -40);
		assertOffset(8, 23, -30);
		assertOffset(9, 46, -20);
	}

	@Test
	public void symmetricBehaviorPairsOppositeHexSlots()
	{
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 2, 0, 20);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 3, 23, -10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 4, -23, 10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 5, 23, 10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 6, -23, -10);
	}

	@Test
	public void symmetricBehaviorPairsOppositeHexSlotsOnLargerRings()
	{
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 7, 0, -40);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 8, 0, 40);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 9, 23, -30);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 10, -23, 30);
	}

	@Test
	public void counterclockwiseDirectionStartsTopAndMovesCounterclockwise()
	{
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 2, -23, -10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 3, -23, 10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 4, 0, 20);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 5, 23, 10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 6, 23, -10);
	}

	@Test
	public void counterclockwiseDirectionExtendsTheSamePatternOnLargerRings()
	{
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 7, 0, -40);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 8, -23, -30);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 9, -46, -20);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 10, -46, 0);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 11, -46, 20);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 12, -23, 30);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 13, 0, 40);
	}

	@Test
	public void spacingCanBeControlledPerAxis()
	{
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, 1, 0.0, 5.2, 0, -25);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, 2, 4.6, 0.0, 28, -10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, 2, 4.6, 5.2, 28, -12);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, 2, 4.2, 4.2, 27, -12);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, 1, -3.0, -4.0, 0, -16);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, 2, -3.0, -4.0, 20, -8);
	}

	@Test
	public void symmetricBehaviorMirrorsWithCounterclockwiseDirection()
	{
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 2, 0, 20);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 3, -23, -10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 4, 23, 10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 5, -23, 10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 6, 23, -10);
	}

	@Test
	public void diamondShapeUsesFourCornerRings()
	{
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 2, 23, 0);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 3, 0, 20);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 4, -23, 0);
	}

	@Test
	public void diamondShapeFillsLargerRingEdges()
	{
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 5, 0, -40);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 6, 23, -20);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 7, 46, 0);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 8, 23, 20);
	}

	@Test
	public void diamondShapeCanRunCounterclockwise()
	{
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 2, -23, 0);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 3, 0, 20);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 4, 23, 0);
	}

	@Test
	public void diamondShapeCanUseSymmetricBehavior()
	{
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 2, 0, 20);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 3, 23, 0);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 4, -23, 0);
	}

	@Test
	public void minRadiusSkipsInnerDiamondSlots()
	{
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 0, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 1, 1, 23, 0);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 2, 1, 0, 20);
		assertOffset(HitsplatCustomizerLayoutShape.DIAMOND, HitsplatCustomizerLayoutDirection.CLOCKWISE, 3, 1, -23, 0);
	}

	@Test
	public void minRadiusSkipsInnerHexSlots()
	{
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, 0, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, 1, 1, 23, -10);
		assertOffset(HitsplatCustomizerLayoutShape.HEXAGONAL, HitsplatCustomizerLayoutDirection.CLOCKWISE, 6, 1, 0, -40);
	}

	@Test
	public void gridShapeUsesSquareRings()
	{
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 0, 0, 0);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 2, 30, -20);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 3, 30, 0);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 4, 30, 20);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 5, 0, 20);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 6, -30, 20);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 7, -30, 0);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 8, -30, -20);
	}

	@Test
	public void gridShapeExpandsToLargerSquareRings()
	{
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 9, 0, -40);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 10, 30, -40);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 11, 60, -40);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, 12, 60, -20);
	}

	@Test
	public void gridShapeCanRunCounterclockwise()
	{
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 2, -30, -20);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 3, -30, 0);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 4, -30, 20);
	}

	@Test
	public void gridShapeCanUseSymmetricBehavior()
	{
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 1, 0, -20);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 2, 0, 20);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 3, 30, -20);
		assertOffset(HitsplatCustomizerLayoutShape.GRID, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 4, -30, 20);
	}

	@Test
	public void xShapeUsesDiagonalArms()
	{
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, 0, 0, 0);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, 1, 23, -20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, 2, 23, 20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, 3, -23, 20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, 4, -23, -20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, 5, 46, -40);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, 6, 46, 40);
	}

	@Test
	public void xShapeCanRunCounterclockwise()
	{
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 1, 23, -20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 2, -23, -20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 3, -23, 20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.COUNTERCLOCKWISE, 4, 23, 20);
	}

	@Test
	public void xShapeCanUseSymmetricBehavior()
	{
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 1, 23, -20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 2, -23, 20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 3, 23, 20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, HitsplatCustomizerLayoutBehavior.SYMMETRIC, 4, -23, -20);
	}

	@Test
	public void minRadiusSkipsInnerXSlots()
	{
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, 0, 1, 23, -20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, 1, 1, 23, 20);
		assertOffset(HitsplatCustomizerLayoutShape.X, HitsplatCustomizerLayoutDirection.CLOCKWISE, 4, 1, 46, -40);
	}

	@Test
	public void slotLimitUsesMaxRadius()
	{
		assertEquals(Integer.MAX_VALUE, HitsplatCustomizerLayout.slotLimit(0));
		assertEquals(1, HitsplatCustomizerLayout.slotLimit(1));
		assertEquals(7, HitsplatCustomizerLayout.slotLimit(2));
		assertEquals(19, HitsplatCustomizerLayout.slotLimit(3));
		assertTrue(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(0, 1));
		assertFalse(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(1, 1));
		assertTrue(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(6, 2));
		assertFalse(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(7, 2));
		assertFalse(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(-1, 1));
	}

	@Test
	public void diamondSlotLimitUsesFourSlotsPerRing()
	{
		assertEquals(Integer.MAX_VALUE, HitsplatCustomizerLayout.slotLimit(0, HitsplatCustomizerLayoutShape.DIAMOND));
		assertEquals(1, HitsplatCustomizerLayout.slotLimit(1, HitsplatCustomizerLayoutShape.DIAMOND));
		assertEquals(5, HitsplatCustomizerLayout.slotLimit(2, HitsplatCustomizerLayoutShape.DIAMOND));
		assertEquals(13, HitsplatCustomizerLayout.slotLimit(3, HitsplatCustomizerLayoutShape.DIAMOND));
		assertTrue(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(4, 2, HitsplatCustomizerLayoutShape.DIAMOND));
		assertFalse(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(5, 2, HitsplatCustomizerLayoutShape.DIAMOND));
	}

	@Test
	public void slotLimitCanSkipInnerRadii()
	{
		assertEquals(4, HitsplatCustomizerLayout.slotLimit(1, 2, HitsplatCustomizerLayoutShape.DIAMOND));
		assertEquals(12, HitsplatCustomizerLayout.slotLimit(1, 3, HitsplatCustomizerLayoutShape.DIAMOND));
		assertEquals(6, HitsplatCustomizerLayout.slotLimit(1, 2, HitsplatCustomizerLayoutShape.HEXAGONAL));
		assertEquals(8, HitsplatCustomizerLayout.slotLimit(1, 2, HitsplatCustomizerLayoutShape.GRID));
		assertEquals(4, HitsplatCustomizerLayout.slotLimit(1, 2, HitsplatCustomizerLayoutShape.X));
		assertEquals(0, HitsplatCustomizerLayout.slotLimit(2, 1, HitsplatCustomizerLayoutShape.DIAMOND));
		assertTrue(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(3, 1, 2, HitsplatCustomizerLayoutShape.DIAMOND));
		assertFalse(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(4, 1, 2, HitsplatCustomizerLayoutShape.DIAMOND));
	}

	@Test
	public void gridSlotLimitUsesEightSlotsPerRing()
	{
		assertEquals(Integer.MAX_VALUE, HitsplatCustomizerLayout.slotLimit(0, HitsplatCustomizerLayoutShape.GRID));
		assertEquals(1, HitsplatCustomizerLayout.slotLimit(1, HitsplatCustomizerLayoutShape.GRID));
		assertEquals(9, HitsplatCustomizerLayout.slotLimit(2, HitsplatCustomizerLayoutShape.GRID));
		assertEquals(25, HitsplatCustomizerLayout.slotLimit(3, HitsplatCustomizerLayoutShape.GRID));
		assertTrue(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(8, 2, HitsplatCustomizerLayoutShape.GRID));
		assertFalse(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(9, 2, HitsplatCustomizerLayoutShape.GRID));
	}

	@Test
	public void xSlotLimitUsesFourSlotsPerRing()
	{
		assertEquals(Integer.MAX_VALUE, HitsplatCustomizerLayout.slotLimit(0, HitsplatCustomizerLayoutShape.X));
		assertEquals(1, HitsplatCustomizerLayout.slotLimit(1, HitsplatCustomizerLayoutShape.X));
		assertEquals(5, HitsplatCustomizerLayout.slotLimit(2, HitsplatCustomizerLayoutShape.X));
		assertEquals(9, HitsplatCustomizerLayout.slotLimit(3, HitsplatCustomizerLayoutShape.X));
		assertTrue(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(4, 2, HitsplatCustomizerLayoutShape.X));
		assertFalse(HitsplatCustomizerLayout.isPositionWithinRadiusLimit(5, 2, HitsplatCustomizerLayoutShape.X));
	}

	@Test
	public void radiusForPositionUsesSkippedRadii()
	{
		assertEquals(1, HitsplatCustomizerLayout.radiusForPosition(0, 1, HitsplatCustomizerLayoutShape.DIAMOND));
		assertEquals(1, HitsplatCustomizerLayout.radiusForPosition(3, 1, HitsplatCustomizerLayoutShape.DIAMOND));
		assertEquals(2, HitsplatCustomizerLayout.radiusForPosition(4, 1, HitsplatCustomizerLayoutShape.DIAMOND));
		assertEquals(1, HitsplatCustomizerLayout.radiusForPosition(7, 0, HitsplatCustomizerLayoutShape.GRID));
		assertEquals(1, HitsplatCustomizerLayout.radiusForPosition(8, 0, HitsplatCustomizerLayoutShape.GRID));
		assertEquals(2, HitsplatCustomizerLayout.radiusForPosition(9, 0, HitsplatCustomizerLayoutShape.GRID));
		assertEquals(1, HitsplatCustomizerLayout.radiusForPosition(3, 0, HitsplatCustomizerLayoutShape.X));
		assertEquals(1, HitsplatCustomizerLayout.radiusForPosition(4, 0, HitsplatCustomizerLayoutShape.X));
		assertEquals(2, HitsplatCustomizerLayout.radiusForPosition(5, 0, HitsplatCustomizerLayoutShape.X));
		assertEquals(2, HitsplatCustomizerLayout.radiusForPosition(4, 1, HitsplatCustomizerLayoutShape.X));
	}

	private static void assertOffset(int position, int expectedX, int expectedY)
	{
		HitsplatCustomizerOffset offset = HitsplatCustomizerLayout.offsetFor(position, 30, 20, 7);
		assertEquals(expectedX, offset.getX());
		assertEquals(expectedY, offset.getY());
	}

	private static void assertOffset(
		HitsplatCustomizerLayoutShape layoutShape,
		HitsplatCustomizerLayoutDirection layoutDirection,
		int position,
		int expectedX,
		int expectedY)
	{
		HitsplatCustomizerOffset offset = HitsplatCustomizerLayout.offsetFor(position, 30, 20, 7, layoutShape, layoutDirection);
		assertEquals(expectedX, offset.getX());
		assertEquals(expectedY, offset.getY());
	}

	private static void assertOffset(
		HitsplatCustomizerLayoutShape layoutShape,
		HitsplatCustomizerLayoutDirection layoutDirection,
		HitsplatCustomizerLayoutBehavior layoutBehavior,
		int position,
		int expectedX,
		int expectedY)
	{
		HitsplatCustomizerOffset offset = HitsplatCustomizerLayout.offsetFor(position, 30, 20, 7, layoutShape, layoutDirection, layoutBehavior, 0.0, 0.0, 0);
		assertEquals(expectedX, offset.getX());
		assertEquals(expectedY, offset.getY());
	}

	private static void assertOffset(
		HitsplatCustomizerLayoutShape layoutShape,
		HitsplatCustomizerLayoutDirection layoutDirection,
		int position,
		int minRadius,
		int expectedX,
		int expectedY)
	{
		HitsplatCustomizerOffset offset = HitsplatCustomizerLayout.offsetFor(position, 30, 20, 7, layoutShape, layoutDirection, HitsplatCustomizerLayoutBehavior.INCREMENTAL, 0.0, 0.0, minRadius);
		assertEquals(expectedX, offset.getX());
		assertEquals(expectedY, offset.getY());
	}

	private static void assertOffset(
		HitsplatCustomizerLayoutShape layoutShape,
		HitsplatCustomizerLayoutDirection layoutDirection,
		int position,
		double xSpacing,
		double ySpacing,
		int expectedX,
		int expectedY)
	{
		HitsplatCustomizerOffset offset = HitsplatCustomizerLayout.offsetFor(position, 30, 20, 7, layoutShape, layoutDirection, xSpacing, ySpacing);
		assertEquals(expectedX, offset.getX());
		assertEquals(expectedY, offset.getY());
	}
}
