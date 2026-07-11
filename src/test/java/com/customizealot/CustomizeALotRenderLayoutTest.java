package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Test;

public class CustomizeALotRenderLayoutTest
{
	@Test
	public void centersTheActualOffsetSpriteBounds()
	{
		CustomizeALotSprite sprite = sprite(10, 8, 3, 4);
		Graphics2D graphics = graphics();
		try
		{
			CustomizeALotRenderLayout layout = CustomizeALotRenderLayout.create(
				sprite,
				null,
				null,
				null,
				"",
				graphics.getFontMetrics(),
				0);

			assertEquals(10, layout.getWidth());
			assertEquals(8, layout.getHeight());
			assertEquals(-5, layout.getFirstX() + sprite.getOffsetX());
			assertEquals(-4, layout.getSpriteY() + sprite.getOffsetY());
		}
		finally
		{
			graphics.dispose();
		}
	}

	@Test
	public void wideHitmarksExpandTheMeasuredCollisionSlot()
	{
		CustomizeALotSprite sprite = sprite(48, 9, 0, 0);
		Graphics2D graphics = graphics();
		try
		{
			CustomizeALotRenderLayout layout = CustomizeALotRenderLayout.create(
				sprite,
				null,
				null,
				null,
				"",
				graphics.getFontMetrics(),
				0);

			assertEquals(48, layout.getWidth());
			assertEquals(9, layout.getHeight());
			assertTrue(layout.getWidth() > 30);
		}
		finally
		{
			graphics.dispose();
		}
	}

	@Test
	public void differentPieceOffsetsDoNotCreateOverlapOrGaps()
	{
		CustomizeALotSprite first = sprite(10, 8, 5, 0);
		CustomizeALotSprite second = sprite(10, 8, 0, 0);
		Graphics2D graphics = graphics();
		try
		{
			CustomizeALotRenderLayout layout = CustomizeALotRenderLayout.create(
				first,
				null,
				second,
				null,
				"",
				graphics.getFontMetrics(),
				0);

			int firstVisualRight = layout.getFirstX() + first.getOffsetX() + first.getWidth();
			int secondVisualLeft = layout.getSecondX() + second.getOffsetX();
			assertEquals(firstVisualRight + 2, secondVisualLeft);
			assertEquals(22, layout.getWidth());
		}
		finally
		{
			graphics.dispose();
		}
	}

	@Test
	public void textAndItsShadowAreIncludedInBounds()
	{
		Graphics2D graphics = graphics();
		try
		{
			FontMetrics metrics = graphics.getFontMetrics();
			String text = "123456789";
			CustomizeALotRenderLayout layout = CustomizeALotRenderLayout.create(
				null,
				null,
				null,
				null,
				text,
				metrics,
				0);

			assertEquals(metrics.stringWidth(text) + 1, layout.getWidth());
			assertEquals(metrics.getAscent() + metrics.getDescent() + 1, layout.getHeight());
		}
		finally
		{
			graphics.dispose();
		}
	}

	@Test
	public void textOnlyDefinitionsKeepTheirVerticalOffset()
	{
		Graphics2D graphics = graphics();
		try
		{
			CustomizeALotRenderLayout base = CustomizeALotRenderLayout.create(
				null, null, null, null, "10", graphics.getFontMetrics(), 0);
			CustomizeALotRenderLayout shifted = CustomizeALotRenderLayout.create(
				null, null, null, null, "10", graphics.getFontMetrics(), 4);

			assertEquals(base.getTextBaseline() + 4, shifted.getTextBaseline());
			assertTrue(shifted.getHeight() > base.getHeight());
		}
		finally
		{
			graphics.dispose();
		}
	}

	@Test
	public void globalScaleResizesSpriteBoundsOffsetsAndPieceSpacingTogether()
	{
		CustomizeALotSprite first = sprite(10, 8, 5, 4);
		CustomizeALotSprite second = sprite(10, 8, 0, 0);
		Graphics2D graphics = graphics();
		try
		{
			CustomizeALotRenderLayout layout = CustomizeALotRenderLayout.create(
				first,
				null,
				second,
				null,
				"",
				graphics.getFontMetrics(),
				0,
				200);

			int firstVisualLeft = layout.getFirstX()
				+ CustomizeALotRenderLayout.scaleCoordinate(first.getOffsetX(), 200);
			int firstVisualRight = firstVisualLeft
				+ CustomizeALotRenderLayout.width(first, 200);
			int secondVisualLeft = layout.getSecondX()
				+ CustomizeALotRenderLayout.scaleCoordinate(second.getOffsetX(), 200);
			assertEquals(firstVisualRight + 4, secondVisualLeft);
			assertEquals(44, layout.getWidth());
			assertEquals(24, layout.getHeight());
		}
		finally
		{
			graphics.dispose();
		}
	}

	@Test
	public void globalScaleUsesScaledFontMetricsAndClampsUnsafeValues()
	{
		Graphics2D graphics = graphics();
		try
		{
			Font scaledFont = CustomizeALotOverlay.scaleFont(graphics.getFont(), 150);
			FontMetrics metrics = graphics.getFontMetrics(scaledFont);
			CustomizeALotRenderLayout layout = CustomizeALotRenderLayout.create(
				null, null, null, null, "123", metrics, 0, 150);

			assertEquals(18.0f, scaledFont.getSize2D(), 0.01f);
			assertEquals(metrics.stringWidth("123") + 2, layout.getWidth());
			assertEquals(5, CustomizeALotRenderLayout.scaleDimension(10, 1));
			assertEquals(20, CustomizeALotRenderLayout.scaleDimension(10, 999));
			assertEquals(-3, CustomizeALotRenderLayout.scaleCoordinate(-2, 150));
		}
		finally
		{
			graphics.dispose();
		}
	}

	private static Graphics2D graphics()
	{
		Graphics2D graphics = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB).createGraphics();
		graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		return graphics;
	}

	private static CustomizeALotSprite sprite(
		int width,
		int height,
		int offsetX,
		int offsetY)
	{
		return new CustomizeALotSprite(
			new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB),
			offsetX,
			offsetY);
	}
}
