package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.client.config.FontType;
import org.junit.Test;

public class CustomizeALotOverheadChatRendererTest
{
	@Test
	public void formattingIsRemovedWithoutLosingEscapedAngleBrackets()
	{
		assertEquals("<3 > 2",
			CustomizeALotOverheadChatRenderer.displayText("<lt>3 <gt> 2"));
		assertEquals("<tag>",
			CustomizeALotOverheadChatRenderer.displayText("<col=ff0000><lt>tag<gt></col>"));
		assertEquals("<col=ff0000>",
			CustomizeALotOverheadChatRenderer.displayText("<lt>col=ff0000<gt>"));
		assertEquals("Café ★ & 'quotes'",
			CustomizeALotOverheadChatRenderer.displayText("Café ★ & 'quotes'"));
	}

	@Test
	public void capturesEveryConfiguredStyleOnceForTheFrame()
	{
		Color textColor = new Color(10, 20, 30, 40);
		Color shadowColor = new Color(50, 60, 70, 80);
		FontType font = new FontType()
			.withFamily("Dialog")
			.withSize(19)
			.withBold(true)
			.withItalic(true);
		CustomizeALotConfig config = new CustomizeALotConfig()
		{
			@Override
			public boolean showNpcOverheadChat()
			{
				return false;
			}

			@Override
			public FontType overheadChatFont()
			{
				return font;
			}

			@Override
			public Color overheadChatColor()
			{
				return textColor;
			}

			@Override
			public boolean overheadChatShadow()
			{
				return false;
			}

			@Override
			public Color overheadChatShadowColor()
			{
				return shadowColor;
			}

			@Override
			public CustomizeALotOverheadChatEffect overheadChatEffect()
			{
				return CustomizeALotOverheadChatEffect.SHAKE;
			}

			@Override
			public int overheadChatXOffset()
			{
				return -7;
			}

			@Override
			public int overheadChatYOffset()
			{
				return 9;
			}
		};

		CustomizeALotOverheadChatRenderer.Style style =
			CustomizeALotOverheadChatRenderer.captureStyle(config);
		assertTrue(style.shouldRender(false));
		assertFalse(style.shouldRender(true));
		assertEquals(19, style.getFont().getSize());
		assertTrue(style.getFont().isItalic());
		assertSame(textColor, style.getColor());
		assertFalse(style.hasShadow());
		assertSame(shadowColor, style.getShadowColor());
		assertEquals(CustomizeALotOverheadChatEffect.SHAKE, style.getFallbackEffect());
		assertEquals(-7, style.getXOffset());
		assertEquals(9, style.getYOffset());
	}

	@Test
	public void relationshipColorsUseGroupIronThenFriendThenClanPrecedence()
	{
		Color fallback = new Color(1, 2, 3, 4);
		Color friend = new Color(0xFFA5FF40, true);
		Color clan = new Color(0xFF40CFFF, true);
		Color groupIron = new Color(0xFFFF4040, true);
		CustomizeALotConfig config = new CustomizeALotConfig()
		{
			@Override
			public Color overheadChatColor()
			{
				return fallback;
			}

			@Override
			public boolean overheadChatRelationshipColors()
			{
				return true;
			}

			@Override
			public Color overheadChatFriendColor()
			{
				return friend;
			}

			@Override
			public Color overheadChatClanColor()
			{
				return clan;
			}

			@Override
			public Color overheadChatGroupIronColor()
			{
				return groupIron;
			}
		};
		CustomizeALotOverheadChatRenderer.Style style =
			CustomizeALotOverheadChatRenderer.captureStyle(config);

		assertSame(groupIron,
			CustomizeALotOverheadChatRenderer.relationshipColor(style, true, true, true));
		assertSame(friend,
			CustomizeALotOverheadChatRenderer.relationshipColor(style, false, true, true));
		assertSame(clan,
			CustomizeALotOverheadChatRenderer.relationshipColor(style, false, false, true));
		assertSame(fallback,
			CustomizeALotOverheadChatRenderer.relationshipColor(style, false, false, false));

		CustomizeALotOverheadChatRenderer.Style disabled =
			CustomizeALotOverheadChatRenderer.captureStyle(new CustomizeALotConfig()
			{
				@Override
				public Color overheadChatColor()
				{
					return fallback;
				}

				@Override
				public boolean overheadChatRelationshipColors()
				{
					return false;
				}
			});
		assertSame(fallback,
			CustomizeALotOverheadChatRenderer.relationshipColor(disabled, true, true, true));
	}

	@Test
	public void rgbaTextAndShadowReachThePixelRenderer()
	{
		Color textColor = new Color(220, 10, 20, 128);
		CustomizeALotConfig textConfig = new CustomizeALotConfig()
		{
			@Override
			public Color overheadChatColor()
			{
				return textColor;
			}

			@Override
			public boolean overheadChatShadow()
			{
				return false;
			}
		};
		BufferedImage textImage = drawStaticText(
			CustomizeALotOverheadChatRenderer.captureStyle(textConfig),
			textColor);
		int textPixel = mostOpaquePixel(textImage);
		assertTrue((textPixel >>> 24) > 0 && (textPixel >>> 24) <= 128);
		assertTrue((textPixel >> 16 & 0xFF) > (textPixel & 0xFF));

		Color shadowColor = new Color(5, 30, 210, 96);
		CustomizeALotConfig shadowConfig = new CustomizeALotConfig()
		{
			@Override
			public Color overheadChatColor()
			{
				return new Color(0, 0, 0, 0);
			}

			@Override
			public Color overheadChatShadowColor()
			{
				return shadowColor;
			}
		};
		CustomizeALotOverheadChatRenderer.Style shadowStyle =
			CustomizeALotOverheadChatRenderer.captureStyle(shadowConfig);
		BufferedImage shadowImage = drawStaticText(shadowStyle, shadowStyle.getColor());
		int shadowPixel = mostOpaquePixel(shadowImage);
		assertTrue((shadowPixel >>> 24) > 0 && (shadowPixel >>> 24) <= 96);
		assertTrue((shadowPixel & 0xFF) > (shadowPixel >> 16 & 0xFF));
	}

	@Test
	public void stretchedPatternColorsReachTheMovingGlyphRenderer()
	{
		CustomizeALotLocalChatEffectTracker tracker =
			new CustomizeALotLocalChatEffectTracker();
		Object local = new Object();
		tracker.recordOutgoing("pattern15:wave:MM", 0, false, 100);
		tracker.recordOverhead(local, local, "MM", 101);
		Color colors = tracker.colorFor(local, "MM", Color.YELLOW, 101);
		assertTrue(colors instanceof CustomizeALotLocalChatEffectTracker.PerGlyphColor);

		CustomizeALotConfig config = new CustomizeALotConfig()
		{
			@Override
			public boolean overheadChatShadow()
			{
				return false;
			}
		};
		CustomizeALotOverheadChatRenderer.Style style =
			CustomizeALotOverheadChatRenderer.captureStyle(config);
		BufferedImage image = drawText(
			style,
			colors,
			"MM",
			CustomizeALotOverheadChatEffect.WAVE);
		assertTrue(containsRgb(image, 0xE40303));
		assertTrue(containsRgb(image, 0x24408E));
	}

	@Test
	public void placesTextAboveEarlierActorUiBeforeApplyingUserOffset()
	{
		assertEquals(
			80,
			CustomizeALotOverheadChatRenderer.baselineY(
				80,
				CustomizeALotHealthBarRenderer.NO_OCCUPIED_TOP,
				3,
				0));
		assertEquals(95, CustomizeALotOverheadChatRenderer.baselineY(120, 100, 3, 0));
		assertEquals(100, CustomizeALotOverheadChatRenderer.baselineY(120, 100, 3, -5));
	}

	@Test
	public void animatedTextReservesItsFullMovementAboveActorUi()
	{
		assertEquals(
			90,
			CustomizeALotOverheadChatRenderer.baselineY(
				120,
				100,
				3,
				0,
				CustomizeALotOverheadChatEffect.WAVE));
		assertEquals(
			88,
			CustomizeALotOverheadChatRenderer.baselineY(
				120,
				100,
				3,
				0,
				CustomizeALotOverheadChatEffect.SHAKE));
	}

	@Test
	public void shiftsOverlappingChatAboveExistingText()
	{
		int baseline = CustomizeALotOverheadChatRenderer.collisionFreeBaseline(
			10,
			100,
			50,
			10,
			3,
			Arrays.asList(new Rectangle(0, 88, 80, 16)));

		assertEquals(82, baseline);
		assertEquals(
			100,
			CustomizeALotOverheadChatRenderer.collisionFreeBaseline(
				100,
				100,
				20,
				10,
				3,
				Arrays.asList(new Rectangle(0, 88, 20, 16))));
	}

	@Test
	public void ordersCollisionBoundsAndHandlesAnUnsortedCallerDefensively()
	{
		List<Rectangle> ordered = new ArrayList<>();
		CustomizeALotOverheadChatRenderer.addOccupiedBounds(
			ordered,
			new Rectangle(0, 72, 80, 16));
		CustomizeALotOverheadChatRenderer.addOccupiedBounds(
			ordered,
			new Rectangle(0, 100, 80, 16));
		CustomizeALotOverheadChatRenderer.addOccupiedBounds(
			ordered,
			new Rectangle(0, 88, 80, 16));
		assertEquals(100, ordered.get(0).y);
		assertEquals(88, ordered.get(1).y);
		assertEquals(72, ordered.get(2).y);

		assertEquals(
			66,
			CustomizeALotOverheadChatRenderer.collisionFreeBaseline(
				10,
				100,
				50,
				10,
				3,
				Arrays.asList(
					new Rectangle(0, 72, 80, 16),
					new Rectangle(0, 88, 80, 16))));
	}

	@Test
	public void reservesTheFullAnimatedEnvelopeForCollisionChecks()
	{
		assertEquals(
			new Rectangle(5, 85, 61, 24),
			CustomizeALotOverheadChatRenderer.effectBounds(
				10,
				100,
				50,
				10,
				3,
				CustomizeALotOverheadChatEffect.WAVE_2));
		assertEquals(
			new Rectangle(-15, 90, 101, 14),
			CustomizeALotOverheadChatRenderer.effectBounds(
				10,
				100,
				50,
				10,
				3,
				CustomizeALotOverheadChatEffect.SCROLL));

		int baseline = CustomizeALotOverheadChatRenderer.collisionFreeBaseline(
			10,
			100,
			50,
			10,
			3,
			CustomizeALotOverheadChatEffect.WAVE,
			Arrays.asList(new Rectangle(0, 88, 80, 16)));
		assertEquals(77, baseline);
	}

	@Test
	public void animatedOffsetsAreDeterministicAndBounded()
	{
		int waveX = CustomizeALotOverheadChatRenderer.effectXOffset(
			CustomizeALotOverheadChatEffect.WAVE_2,
			4,
			1234);
		int waveY = CustomizeALotOverheadChatRenderer.effectYOffset(
			CustomizeALotOverheadChatEffect.WAVE_2,
			4,
			1234,
			100);

		assertEquals(
			waveX,
			CustomizeALotOverheadChatRenderer.effectXOffset(
				CustomizeALotOverheadChatEffect.WAVE_2,
				4,
				1234));
		assertEquals(
			waveY,
			CustomizeALotOverheadChatRenderer.effectYOffset(
				CustomizeALotOverheadChatEffect.WAVE_2,
				4,
				1234,
				100));
		assertTrue(Math.abs(waveX) <= 5);
		assertTrue(Math.abs(waveY) <= 5);
		assertEquals(7, CustomizeALotOverheadChatRenderer.shakeAmplitude(100));
		assertEquals(0, CustomizeALotOverheadChatRenderer.shakeAmplitude(44));
	}

	@Test
	public void animatedPhaseDoesNotJumpAtTheFormerModuloBoundary()
	{
		int before = CustomizeALotOverheadChatRenderer.effectYOffset(
			CustomizeALotOverheadChatEffect.WAVE,
			0,
			9999,
			100);
		int after = CustomizeALotOverheadChatRenderer.effectYOffset(
			CustomizeALotOverheadChatEffect.WAVE,
			0,
			10000,
			100);

		assertTrue(Math.abs(after - before) <= 2);
	}

	@Test
	public void clippedEffectsTraverseTheirWindowsOverTheMessageLifetime()
	{
		assertEquals(170, CustomizeALotOverheadChatRenderer.scrollTextX(100, 40, 100));
		assertEquals(30, CustomizeALotOverheadChatRenderer.scrollTextX(100, 40, 0));
		assertEquals(-20, CustomizeALotOverheadChatRenderer.slideYOffset(100));
		assertEquals(0, CustomizeALotOverheadChatRenderer.slideYOffset(80));
		assertEquals(0, CustomizeALotOverheadChatRenderer.slideYOffset(20));
		assertEquals(20, CustomizeALotOverheadChatRenderer.slideYOffset(0));
	}

	@Test
	public void playerEffectsUseTheFullOneHundredFiftyCycleLifetime()
	{
		assertEquals(
			170,
			CustomizeALotOverheadChatRenderer.scrollTextX(100, 40, 150, 150));
		assertEquals(
			124,
			CustomizeALotOverheadChatRenderer.scrollTextX(100, 40, 100, 150));
		assertEquals(
			30,
			CustomizeALotOverheadChatRenderer.scrollTextX(100, 40, 0, 150));
		assertEquals(
			-20,
			CustomizeALotOverheadChatRenderer.slideYOffset(150, 150));
		assertEquals(
			0,
			CustomizeALotOverheadChatRenderer.slideYOffset(100, 150));
		assertEquals(
			20,
			CustomizeALotOverheadChatRenderer.slideYOffset(0, 150));
		assertEquals(
			7,
			CustomizeALotOverheadChatRenderer.shakeAmplitude(150, 150));
		assertEquals(
			0,
			CustomizeALotOverheadChatRenderer.shakeAmplitude(94, 150));
	}

	@Test
	public void effectsHaveReadableLabels()
	{
		assertEquals("Static", CustomizeALotOverheadChatEffect.STATIC.toString());
		assertEquals("Wave", CustomizeALotOverheadChatEffect.WAVE.toString());
		assertEquals("Wave 2", CustomizeALotOverheadChatEffect.WAVE_2.toString());
		assertEquals("Shake", CustomizeALotOverheadChatEffect.SHAKE.toString());
		assertEquals("Scroll", CustomizeALotOverheadChatEffect.SCROLL.toString());
		assertEquals("Slide", CustomizeALotOverheadChatEffect.SLIDE.toString());
	}

	private static BufferedImage drawStaticText(
		CustomizeALotOverheadChatRenderer.Style style,
		Color textColor)
	{
		return drawText(
			style,
			textColor,
			"M",
			CustomizeALotOverheadChatEffect.STATIC);
	}

	private static BufferedImage drawText(
		CustomizeALotOverheadChatRenderer.Style style,
		Color textColor,
		String text,
		CustomizeALotOverheadChatEffect effect)
	{
		BufferedImage image = new BufferedImage(80, 40, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			graphics.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			graphics.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
			graphics.setFont(style.getFont());
			FontMetrics metrics = graphics.getFontMetrics();
			GlyphVector glyphs = CustomizeALotOverheadChatRenderer.createAnimatedGlyphs(
				graphics,
				text,
				effect,
				123,
				100,
				150);
			CustomizeALotOverheadChatRenderer.drawStyledEffect(
				graphics,
				text,
				8,
				28,
				metrics.stringWidth(text),
				metrics,
				effect,
				glyphs,
				100,
				100,
				style,
				textColor);
		}
		finally
		{
			graphics.dispose();
		}
		return image;
	}

	private static boolean containsRgb(BufferedImage image, int expectedRgb)
	{
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				int pixel = image.getRGB(x, y);
				if ((pixel >>> 24) != 0 && (pixel & 0x00FFFFFF) == expectedRgb)
				{
					return true;
				}
			}
		}
		return false;
	}

	private static int mostOpaquePixel(BufferedImage image)
	{
		int mostOpaque = 0;
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				int pixel = image.getRGB(x, y);
				if ((pixel >>> 24) > (mostOpaque >>> 24))
				{
					mostOpaque = pixel;
				}
			}
		}
		return mostOpaque;
	}
}
