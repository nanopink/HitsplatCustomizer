package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class CustomizeALotActorUiSpriteCacheTest
{
	private static final int ARCHIVE_ID = 440;

	@Test
	public void convertsOnlyTheRequestedSpriteAndReusesTheLoadedGroup()
	{
		AtomicInteger gameCycle = new AtomicInteger(100);
		AtomicInteger groupLoads = new AtomicInteger();
		AtomicInteger firstConversions = new AtomicInteger();
		AtomicInteger secondConversions = new AtomicInteger();
		CustomizeALotActorUiSpriteCache.SpriteSource first = source(firstConversions, 2, 3, 4, 5, 1, 2);
		CustomizeALotActorUiSpriteCache.SpriteSource second = source(secondConversions, 6, 7, 8, 9, 3, 4);
		CustomizeALotActorUiSpriteCache cache = new CustomizeALotActorUiSpriteCache(
			gameCycle::get,
			archiveId ->
			{
				groupLoads.incrementAndGet();
				return new CustomizeALotActorUiSpriteCache.SpriteSource[]{first, second};
			});

		CustomizeALotSprite loadedSecond = cache.get(ARCHIVE_ID, 1);

		assertNotNull(loadedSecond);
		assertEquals(6, loadedSecond.getWidth());
		assertEquals(7, loadedSecond.getHeight());
		assertEquals(8, loadedSecond.getMaxWidth());
		assertEquals(9, loadedSecond.getMaxHeight());
		assertEquals(3, loadedSecond.getOffsetX());
		assertEquals(4, loadedSecond.getOffsetY());
		assertEquals(0, firstConversions.get());
		assertEquals(1, secondConversions.get());

		assertSame(loadedSecond, cache.get(ARCHIVE_ID, 1));
		assertNotNull(cache.get(ARCHIVE_ID, 0));
		assertEquals(1, groupLoads.get());
		assertEquals(1, firstConversions.get());
		assertEquals(1, secondConversions.get());
	}

	@Test
	public void missingSpriteEntryReloadsTheGroupAfterTheRetryWindow()
	{
		AtomicInteger gameCycle = new AtomicInteger();
		AtomicInteger groupLoads = new AtomicInteger();
		AtomicInteger conversions = new AtomicInteger();
		CustomizeALotActorUiSpriteCache.SpriteSource recovered = source(
			conversions, 3, 4, 5, 6, 1, 2);
		CustomizeALotActorUiSpriteCache cache = new CustomizeALotActorUiSpriteCache(
			gameCycle::get,
			archiveId -> groupLoads.getAndIncrement() == 0
				? new CustomizeALotActorUiSpriteCache.SpriteSource[]{null}
				: new CustomizeALotActorUiSpriteCache.SpriteSource[]{recovered});

		assertNull(cache.get(ARCHIVE_ID, 0));
		gameCycle.set(9);
		assertNull(cache.get(ARCHIVE_ID, 0));
		gameCycle.set(10);
		CustomizeALotSprite loaded = cache.get(ARCHIVE_ID, 0);

		assertNotNull(loaded);
		assertEquals(3, loaded.getWidth());
		assertEquals(2, groupLoads.get());
		assertEquals(1, conversions.get());
	}

	@Test
	public void failedWholeGroupLoadIsRetriedWithoutDoubleLoadingAtTheBoundary()
	{
		AtomicInteger gameCycle = new AtomicInteger();
		AtomicInteger groupLoads = new AtomicInteger();
		AtomicInteger conversions = new AtomicInteger();
		CustomizeALotActorUiSpriteCache.SpriteSource recovered = source(
			conversions, 1, 1, 1, 1, 0, 0);
		CustomizeALotActorUiSpriteCache cache = new CustomizeALotActorUiSpriteCache(
			gameCycle::get,
			archiveId -> groupLoads.getAndIncrement() == 0
				? null
				: new CustomizeALotActorUiSpriteCache.SpriteSource[]{recovered});

		assertNull(cache.get(ARCHIVE_ID, 0));
		gameCycle.set(9);
		assertNull(cache.get(ARCHIVE_ID, 0));
		gameCycle.set(10);
		assertNotNull(cache.get(ARCHIVE_ID, 0));

		assertEquals(2, groupLoads.get());
		assertEquals(1, conversions.get());
	}

	private static CustomizeALotActorUiSpriteCache.SpriteSource source(
		AtomicInteger conversions,
		int width,
		int height,
		int maxWidth,
		int maxHeight,
		int offsetX,
		int offsetY)
	{
		return new CustomizeALotActorUiSpriteCache.SpriteSource(() ->
		{
			conversions.incrementAndGet();
			return new CustomizeALotSprite(
				new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB),
				maxWidth,
				maxHeight,
				offsetX,
				offsetY);
		});
	}
}
