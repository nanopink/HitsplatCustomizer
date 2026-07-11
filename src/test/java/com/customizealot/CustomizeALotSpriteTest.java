package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Test;

public class CustomizeALotSpriteTest
{
	@Test
	public void constructorPreservesImageDimensionsAndBothOffsets()
	{
		BufferedImage image = new BufferedImage(4, 3, BufferedImage.TYPE_INT_ARGB);
		CustomizeALotSprite sprite = new CustomizeALotSprite(image, 2, 5);

		assertSame(image, sprite.getImage());
		assertEquals(4, sprite.getWidth());
		assertEquals(3, sprite.getHeight());
		assertEquals(4, sprite.getMaxWidth());
		assertEquals(3, sprite.getMaxHeight());
		assertEquals(2, sprite.getOffsetX());
		assertEquals(5, sprite.getOffsetY());
	}

	@Test
	public void constructorPreservesLargerSpriteCanvasDimensions()
	{
		BufferedImage image = new BufferedImage(4, 3, BufferedImage.TYPE_INT_ARGB);
		CustomizeALotSprite sprite = new CustomizeALotSprite(image, 8, 9, 2, 5);

		assertEquals(4, sprite.getWidth());
		assertEquals(3, sprite.getHeight());
		assertEquals(8, sprite.getMaxWidth());
		assertEquals(9, sprite.getMaxHeight());
	}

	@Test
	public void drawSpriteAppliesHorizontalAndVerticalOffsets()
	{
		BufferedImage source = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		source.setRGB(0, 0, 0xFFFF0000);
		CustomizeALotSprite sprite = new CustomizeALotSprite(source, 2, 3);
		BufferedImage destination = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = destination.createGraphics();
		try
		{
			CustomizeALotOverlay.drawSprite(graphics, sprite, 4, 5);
		}
		finally
		{
			graphics.dispose();
		}

		assertEquals(0, destination.getRGB(4, 5));
		assertEquals(0xFFFF0000, destination.getRGB(6, 8));
	}

	@Test
	public void xbrMasterIsCreatedOnlyForUpscalingAndThenReused()
	{
		BufferedImage source = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
		CustomizeALotSprite sprite = new CustomizeALotSprite(source, 0, 0);

		assertSame(
			source,
			sprite.getImageForScaling(CustomizeALotSpriteScalingMode.XBR, 2, 2));
		BufferedImage first = sprite.getImageForScaling(CustomizeALotSpriteScalingMode.XBR, 3, 3);
		BufferedImage second = sprite.getImageForScaling(CustomizeALotSpriteScalingMode.XBR, 4, 4);

		assertEquals(4, first.getWidth());
		assertEquals(4, first.getHeight());
		assertSame(first, second);
		assertSame(
			source,
			sprite.getImageForScaling(CustomizeALotSpriteScalingMode.BICUBIC, 4, 4));
	}

	@Test
	public void scaledHitsplatDrawingScalesBothImageAndOffsets()
	{
		BufferedImage source = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		source.setRGB(0, 0, 0xFFFF0000);
		CustomizeALotSprite sprite = new CustomizeALotSprite(source, 2, 3);
		BufferedImage destination = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = destination.createGraphics();
		try
		{
			CustomizeALotOverlay.drawScaledSprite(
				graphics,
				sprite,
				4,
				5,
				200,
				CustomizeALotSpriteScalingMode.XBR);
		}
		finally
		{
			graphics.dispose();
		}

		assertEquals(0, destination.getRGB(7, 10));
		assertEquals(0xFFFF0000, destination.getRGB(8, 11));
		assertEquals(0xFFFF0000, destination.getRGB(9, 12));
		assertEquals(0, destination.getRGB(10, 13));
	}
}
