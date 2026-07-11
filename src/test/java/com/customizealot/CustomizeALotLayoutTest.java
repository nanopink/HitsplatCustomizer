package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CustomizeALotLayoutTest
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
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 2, 0, 20);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 3, 23, -10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 4, -23, 10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 5, 23, 10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 6, -23, -10);
	}

	@Test
	public void symmetricBehaviorPairsOppositeHexSlotsOnLargerRings()
	{
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 7, 0, -40);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 8, 0, 40);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 9, 23, -30);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 10, -23, 30);
	}

	@Test
	public void counterclockwiseDirectionStartsTopAndMovesCounterclockwise()
	{
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 2, -23, -10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 3, -23, 10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 4, 0, 20);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 5, 23, 10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 6, 23, -10);
	}

	@Test
	public void counterclockwiseDirectionExtendsTheSamePatternOnLargerRings()
	{
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 7, 0, -40);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 8, -23, -30);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 9, -46, -20);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 10, -46, 0);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 11, -46, 20);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 12, -23, 30);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 13, 0, 40);
	}

	@Test
	public void spacingCanBeControlledPerAxis()
	{
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, 1, 0.0, 5.2, 0, -25);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, 2, 4.6, 0.0, 28, -10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, 2, 4.6, 5.2, 28, -12);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, 2, 4.2, 4.2, 27, -12);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, 1, -3.0, -4.0, 0, -16);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, 2, -3.0, -4.0, 20, -8);
	}

	@Test
	public void symmetricBehaviorMirrorsWithCounterclockwiseDirection()
	{
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 2, 0, 20);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 3, -23, -10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 4, 23, 10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 5, -23, 10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 6, 23, -10);
	}

	@Test
	public void diamondShapeUsesFourCornerRings()
	{
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 2, 23, 0);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 3, 0, 20);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 4, -23, 0);
	}

	@Test
	public void diamondShapeFillsLargerRingEdges()
	{
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 5, 0, -40);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 6, 23, -20);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 7, 46, 0);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 8, 23, 20);
	}

	@Test
	public void diamondShapeCanRunCounterclockwise()
	{
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 2, -23, 0);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 3, 0, 20);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 4, 23, 0);
	}

	@Test
	public void diamondShapeCanUseSymmetricBehavior()
	{
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 2, 0, 20);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 3, 23, 0);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 4, -23, 0);
	}

	@Test
	public void minRadiusSkipsInnerDiamondSlots()
	{
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 0, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 1, 1, 23, 0);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 2, 1, 0, 20);
		assertOffset(CustomizeALotLayoutShape.DIAMOND, CustomizeALotLayoutDirection.CLOCKWISE, 3, 1, -23, 0);
	}

	@Test
	public void minRadiusSkipsInnerHexSlots()
	{
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, 0, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, 1, 1, 23, -10);
		assertOffset(CustomizeALotLayoutShape.HEXAGONAL, CustomizeALotLayoutDirection.CLOCKWISE, 6, 1, 0, -40);
	}

	@Test
	public void gridShapeUsesSquareRings()
	{
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 0, 0, 0);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 2, 30, -20);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 3, 30, 0);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 4, 30, 20);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 5, 0, 20);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 6, -30, 20);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 7, -30, 0);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 8, -30, -20);
	}

	@Test
	public void gridShapeExpandsToLargerSquareRings()
	{
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 9, 0, -40);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 10, 30, -40);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 11, 60, -40);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, 12, 60, -20);
	}

	@Test
	public void gridShapeCanRunCounterclockwise()
	{
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 2, -30, -20);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 3, -30, 0);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 4, -30, 20);
	}

	@Test
	public void gridShapeCanUseSymmetricBehavior()
	{
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 1, 0, -20);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 2, 0, 20);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 3, 30, -20);
		assertOffset(CustomizeALotLayoutShape.GRID, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 4, -30, 20);
	}

	@Test
	public void xShapeUsesDiagonalArms()
	{
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, 0, 0, 0);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, 1, 23, -20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, 2, 23, 20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, 3, -23, 20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, 4, -23, -20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, 5, 46, -40);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, 6, 46, 40);
	}

	@Test
	public void xShapeCanRunCounterclockwise()
	{
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 1, 23, -20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 2, -23, -20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 3, -23, 20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.COUNTERCLOCKWISE, 4, 23, 20);
	}

	@Test
	public void xShapeCanUseSymmetricBehavior()
	{
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 1, 23, -20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 2, -23, 20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 3, 23, 20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, CustomizeALotLayoutBehavior.SYMMETRIC, 4, -23, -20);
	}

	@Test
	public void minRadiusSkipsInnerXSlots()
	{
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, 0, 1, 23, -20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, 1, 1, 23, 20);
		assertOffset(CustomizeALotLayoutShape.X, CustomizeALotLayoutDirection.CLOCKWISE, 4, 1, 46, -40);
	}

	@Test
	public void slotLimitUsesMaxRadius()
	{
		assertEquals(Integer.MAX_VALUE, CustomizeALotLayout.slotLimit(0));
		assertEquals(1, CustomizeALotLayout.slotLimit(1));
		assertEquals(7, CustomizeALotLayout.slotLimit(2));
		assertEquals(19, CustomizeALotLayout.slotLimit(3));
		assertTrue(CustomizeALotLayout.isPositionWithinRadiusLimit(0, 1));
		assertFalse(CustomizeALotLayout.isPositionWithinRadiusLimit(1, 1));
		assertTrue(CustomizeALotLayout.isPositionWithinRadiusLimit(6, 2));
		assertFalse(CustomizeALotLayout.isPositionWithinRadiusLimit(7, 2));
		assertFalse(CustomizeALotLayout.isPositionWithinRadiusLimit(-1, 1));
	}

	@Test
	public void diamondSlotLimitUsesFourSlotsPerRing()
	{
		assertEquals(Integer.MAX_VALUE, CustomizeALotLayout.slotLimit(0, CustomizeALotLayoutShape.DIAMOND));
		assertEquals(1, CustomizeALotLayout.slotLimit(1, CustomizeALotLayoutShape.DIAMOND));
		assertEquals(5, CustomizeALotLayout.slotLimit(2, CustomizeALotLayoutShape.DIAMOND));
		assertEquals(13, CustomizeALotLayout.slotLimit(3, CustomizeALotLayoutShape.DIAMOND));
		assertTrue(CustomizeALotLayout.isPositionWithinRadiusLimit(4, 2, CustomizeALotLayoutShape.DIAMOND));
		assertFalse(CustomizeALotLayout.isPositionWithinRadiusLimit(5, 2, CustomizeALotLayoutShape.DIAMOND));
	}

	@Test
	public void slotLimitCanSkipInnerRadii()
	{
		assertEquals(4, CustomizeALotLayout.slotLimit(1, 2, CustomizeALotLayoutShape.DIAMOND));
		assertEquals(12, CustomizeALotLayout.slotLimit(1, 3, CustomizeALotLayoutShape.DIAMOND));
		assertEquals(6, CustomizeALotLayout.slotLimit(1, 2, CustomizeALotLayoutShape.HEXAGONAL));
		assertEquals(8, CustomizeALotLayout.slotLimit(1, 2, CustomizeALotLayoutShape.GRID));
		assertEquals(4, CustomizeALotLayout.slotLimit(1, 2, CustomizeALotLayoutShape.X));
		assertEquals(0, CustomizeALotLayout.slotLimit(2, 1, CustomizeALotLayoutShape.DIAMOND));
		assertTrue(CustomizeALotLayout.isPositionWithinRadiusLimit(3, 1, 2, CustomizeALotLayoutShape.DIAMOND));
		assertFalse(CustomizeALotLayout.isPositionWithinRadiusLimit(4, 1, 2, CustomizeALotLayoutShape.DIAMOND));
	}

	@Test
	public void gridSlotLimitUsesEightSlotsPerRing()
	{
		assertEquals(Integer.MAX_VALUE, CustomizeALotLayout.slotLimit(0, CustomizeALotLayoutShape.GRID));
		assertEquals(1, CustomizeALotLayout.slotLimit(1, CustomizeALotLayoutShape.GRID));
		assertEquals(9, CustomizeALotLayout.slotLimit(2, CustomizeALotLayoutShape.GRID));
		assertEquals(25, CustomizeALotLayout.slotLimit(3, CustomizeALotLayoutShape.GRID));
		assertTrue(CustomizeALotLayout.isPositionWithinRadiusLimit(8, 2, CustomizeALotLayoutShape.GRID));
		assertFalse(CustomizeALotLayout.isPositionWithinRadiusLimit(9, 2, CustomizeALotLayoutShape.GRID));
	}

	@Test
	public void xSlotLimitUsesFourSlotsPerRing()
	{
		assertEquals(Integer.MAX_VALUE, CustomizeALotLayout.slotLimit(0, CustomizeALotLayoutShape.X));
		assertEquals(1, CustomizeALotLayout.slotLimit(1, CustomizeALotLayoutShape.X));
		assertEquals(5, CustomizeALotLayout.slotLimit(2, CustomizeALotLayoutShape.X));
		assertEquals(9, CustomizeALotLayout.slotLimit(3, CustomizeALotLayoutShape.X));
		assertTrue(CustomizeALotLayout.isPositionWithinRadiusLimit(4, 2, CustomizeALotLayoutShape.X));
		assertFalse(CustomizeALotLayout.isPositionWithinRadiusLimit(5, 2, CustomizeALotLayoutShape.X));
	}

	@Test
	public void radiusForPositionUsesSkippedRadii()
	{
		assertEquals(1, CustomizeALotLayout.radiusForPosition(0, 1, CustomizeALotLayoutShape.DIAMOND));
		assertEquals(1, CustomizeALotLayout.radiusForPosition(3, 1, CustomizeALotLayoutShape.DIAMOND));
		assertEquals(2, CustomizeALotLayout.radiusForPosition(4, 1, CustomizeALotLayoutShape.DIAMOND));
		assertEquals(1, CustomizeALotLayout.radiusForPosition(7, 0, CustomizeALotLayoutShape.GRID));
		assertEquals(1, CustomizeALotLayout.radiusForPosition(8, 0, CustomizeALotLayoutShape.GRID));
		assertEquals(2, CustomizeALotLayout.radiusForPosition(9, 0, CustomizeALotLayoutShape.GRID));
		assertEquals(1, CustomizeALotLayout.radiusForPosition(3, 0, CustomizeALotLayoutShape.X));
		assertEquals(1, CustomizeALotLayout.radiusForPosition(4, 0, CustomizeALotLayoutShape.X));
		assertEquals(2, CustomizeALotLayout.radiusForPosition(5, 0, CustomizeALotLayoutShape.X));
		assertEquals(2, CustomizeALotLayout.radiusForPosition(4, 1, CustomizeALotLayoutShape.X));
	}

	private static void assertOffset(int position, int expectedX, int expectedY)
	{
		CustomizeALotOffset offset = CustomizeALotLayout.offsetFor(position, 30, 20, 7);
		assertEquals(expectedX, offset.getX());
		assertEquals(expectedY, offset.getY());
	}

	private static void assertOffset(
		CustomizeALotLayoutShape layoutShape,
		CustomizeALotLayoutDirection layoutDirection,
		int position,
		int expectedX,
		int expectedY)
	{
		CustomizeALotOffset offset = CustomizeALotLayout.offsetFor(position, 30, 20, 7, layoutShape, layoutDirection);
		assertEquals(expectedX, offset.getX());
		assertEquals(expectedY, offset.getY());
	}

	private static void assertOffset(
		CustomizeALotLayoutShape layoutShape,
		CustomizeALotLayoutDirection layoutDirection,
		CustomizeALotLayoutBehavior layoutBehavior,
		int position,
		int expectedX,
		int expectedY)
	{
		CustomizeALotOffset offset = CustomizeALotLayout.offsetFor(position, 30, 20, 7, layoutShape, layoutDirection, layoutBehavior, 0.0, 0.0, 0);
		assertEquals(expectedX, offset.getX());
		assertEquals(expectedY, offset.getY());
	}

	private static void assertOffset(
		CustomizeALotLayoutShape layoutShape,
		CustomizeALotLayoutDirection layoutDirection,
		int position,
		int minRadius,
		int expectedX,
		int expectedY)
	{
		CustomizeALotOffset offset = CustomizeALotLayout.offsetFor(position, 30, 20, 7, layoutShape, layoutDirection, CustomizeALotLayoutBehavior.INCREMENTAL, 0.0, 0.0, minRadius);
		assertEquals(expectedX, offset.getX());
		assertEquals(expectedY, offset.getY());
	}

	private static void assertOffset(
		CustomizeALotLayoutShape layoutShape,
		CustomizeALotLayoutDirection layoutDirection,
		int position,
		double xSpacing,
		double ySpacing,
		int expectedX,
		int expectedY)
	{
		CustomizeALotOffset offset = CustomizeALotLayout.offsetFor(position, 30, 20, 7, layoutShape, layoutDirection, xSpacing, ySpacing);
		assertEquals(expectedX, offset.getX());
		assertEquals(expectedY, offset.getY());
	}
}
