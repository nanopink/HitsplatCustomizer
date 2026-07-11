package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.RenderingHints;
import net.runelite.client.config.FontType;
import org.junit.Test;

public class CustomizeALotActorUiConfigTest
{
	private final CustomizeALotConfig config = new CustomizeALotConfig()
	{
	};

	@Test
	public void healthBarDefaultsUseNativeStyleAndScale()
	{
		assertTrue(config.healthBarsEnabled());
		assertEquals(CustomizeALotHealthBarStyle.NATIVE, config.healthBarStyle());
		assertEquals(CustomizeALotHealthScaleMode.FIXED, config.healthBarScaleMode());
		assertEquals(100, config.healthBarScalePercent());
		assertEquals(100, config.healthBarScaleThreshold());
		assertEquals(150, config.healthBarLargeScalePercent());
		assertEquals(30.0, config.healthBarSolidWidth(), 0.0);
		assertEquals(5.0, config.healthBarHeight(), 0.0);
		assertEquals(0, config.healthBarXOffset());
		assertEquals(0, config.healthBarYOffset());
		assertEquals(
			CustomizeALotHealthBarFillDirection.LEFT_TO_RIGHT,
			config.healthBarFillDirection());
		assertEquals(
			CustomizeALotHealthBarGradientCoordinates.SEGMENT,
			config.healthBarGradientCoordinates());
		assertEquals(
			CustomizeALotHealthBarGradientCoordinates.SEGMENT,
			config.healthBarFrontGradientCoordinates());
		assertEquals(
			CustomizeALotHealthBarGradientCoordinates.SEGMENT,
			config.healthBarBackGradientCoordinates());
		assertEquals(CustomizeALotHealthBarGradient.SOLID, config.healthBarFrontGradient());
		assertEquals(new Color(45, 190, 88), config.healthBarFrontColor());
		assertEquals(new Color(34, 158, 72), config.healthBarFrontSecondaryColor());
		assertEquals(new Color(118, 190, 60), config.healthBarPoisonedFrontColor());
		assertEquals(CustomizeALotHealthBarGradient.SOLID, config.healthBarBackGradient());
		assertEquals(new Color(184, 60, 60), config.healthBarBackColor());
		assertEquals(new Color(151, 45, 45), config.healthBarBackSecondaryColor());
		assertTrue(config.healthBarDamageTrailEnabled());
		assertEquals(new Color(245, 185, 66, 210), config.healthBarDamageTrailColor());
		assertEquals(400, config.healthBarDamageTrailHold());
		assertEquals(600, config.healthBarDamageTrailDrain());
		assertFalse(config.healthBarSegmentsEnabled());
		assertEquals(10, config.healthBarPublicUnitsPerSegment());
		assertEquals(
			CustomizeALotHealthBarSegmentValueMode.EXACT_HP_WITH_PUBLIC_FALLBACK,
			config.healthBarSegmentValueMode());
		assertEquals(10, config.healthBarHitpointsPerSegment());
		assertEquals(new Color(0, 0, 0, 160), config.healthBarSegmentColor());
		assertEquals(1.0, config.healthBarSegmentThickness(), 0.0);
		assertEquals(Color.BLACK, config.healthBarBorderColor());
		assertEquals(1.0, config.healthBarBorderThickness(), 0.0);
		assertEquals(0.0, config.healthBarCornerRadius(), 0.0);
		assertEquals(CustomizeALotSpriteScalingMode.XBR, config.spriteScalingMode());
	}

	@Test
	public void overheadChatReplacementIsVisibleByDefault()
	{
		assertTrue(config.overheadChatEnabled());
		assertTrue(config.showNpcOverheadChat());
		assertSame(FontType.BOLD, config.overheadChatFont());
		assertEquals(Color.YELLOW, config.overheadChatColor());
		assertEquals(CustomizeALotOverheadChatEffect.STATIC, config.overheadChatEffect());
		assertTrue(config.overheadChatShadow());
		assertEquals(Color.BLACK, config.overheadChatShadowColor());
		assertEquals(0, config.overheadChatXOffset());
		assertEquals(0, config.overheadChatYOffset());
	}

	@Test
	public void headIconsAreEnabledByDefault()
	{
		assertTrue(config.headIconsEnabled());
		assertTrue(config.showPrayerIcons());
		assertTrue(config.showSkullIcons());
		assertTrue(config.showNpcIcons());
		assertTrue(config.showHintArrows());
		assertEquals(100, config.headIconScalePercent());
		assertEquals(0, config.headIconXOffset());
		assertEquals(0, config.headIconYOffset());
		assertEquals(2, config.headIconSpacing());
	}

	@Test
	public void healthBarStylesHaveReadableLabels()
	{
		assertEquals("RuneScape", CustomizeALotHealthBarStyle.NATIVE.toString());
		assertEquals("Custom", CustomizeALotHealthBarStyle.SOLID.toString());
	}

	@Test
	public void customHealthBarModesHaveReadableLabels()
	{
		assertEquals("Solid", CustomizeALotHealthBarGradient.SOLID.toString());
		assertEquals("Horizontal", CustomizeALotHealthBarGradient.HORIZONTAL.toString());
		assertEquals("Vertical", CustomizeALotHealthBarGradient.VERTICAL.toString());
		assertEquals("Health based", CustomizeALotHealthBarGradient.HEALTH_BASED.toString());
		assertEquals(
			"Relative to each segment",
			CustomizeALotHealthBarGradientCoordinates.SEGMENT.toString());
		assertEquals(
			"Absolute across full bar",
			CustomizeALotHealthBarGradientCoordinates.FULL_BAR.toString());
		assertEquals(
			"Exact HP; public fallback",
			CustomizeALotHealthBarSegmentValueMode.EXACT_HP_WITH_PUBLIC_FALLBACK.toString());
		assertEquals(
			"Exact HP only",
			CustomizeALotHealthBarSegmentValueMode.EXACT_HP_ONLY.toString());
		assertEquals(
			"Public scale units",
			CustomizeALotHealthBarSegmentValueMode.PUBLIC_SCALE.toString());
		assertEquals("Left to right", CustomizeALotHealthBarFillDirection.LEFT_TO_RIGHT.toString());
		assertEquals("Right to left", CustomizeALotHealthBarFillDirection.RIGHT_TO_LEFT.toString());
		assertEquals("Top to bottom", CustomizeALotHealthBarFillDirection.TOP_TO_BOTTOM.toString());
		assertEquals("Bottom to top", CustomizeALotHealthBarFillDirection.BOTTOM_TO_TOP.toString());
	}

	@Test
	public void legacyHealthValuesFeedTheNewSplitSettings()
	{
		CustomizeALotConfig legacy = new CustomizeALotConfig()
		{
			@Override
			public CustomizeALotHealthBarGradientCoordinates healthBarGradientCoordinates()
			{
				return CustomizeALotHealthBarGradientCoordinates.FULL_BAR;
			}

			@Override
			public int healthBarPublicUnitsPerSegment()
			{
				return 25;
			}
		};

		assertEquals(
			CustomizeALotHealthBarGradientCoordinates.FULL_BAR,
			legacy.healthBarFrontGradientCoordinates());
		assertEquals(
			CustomizeALotHealthBarGradientCoordinates.FULL_BAR,
			legacy.healthBarBackGradientCoordinates());
		assertEquals(25, legacy.healthBarHitpointsPerSegment());
	}

	@Test
	public void spriteScalingModesHaveReadableLabels()
	{
		assertEquals("Nearest", CustomizeALotSpriteScalingMode.NEAREST.toString());
		assertEquals("Bilinear", CustomizeALotSpriteScalingMode.BILINEAR.toString());
		assertEquals("Bicubic", CustomizeALotSpriteScalingMode.BICUBIC.toString());
		assertEquals("xBR", CustomizeALotSpriteScalingMode.XBR.toString());
		assertSame(
			RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
			CustomizeALotSpriteScalingMode.NEAREST.getInterpolationHint());
		assertSame(
			RenderingHints.VALUE_INTERPOLATION_BILINEAR,
			CustomizeALotSpriteScalingMode.BILINEAR.getInterpolationHint());
		assertSame(
			RenderingHints.VALUE_INTERPOLATION_BICUBIC,
			CustomizeALotSpriteScalingMode.BICUBIC.getInterpolationHint());
		assertSame(
			RenderingHints.VALUE_INTERPOLATION_BICUBIC,
			CustomizeALotSpriteScalingMode.XBR.getInterpolationHint());
	}

}
