package com.customizealot;

import javax.inject.Inject;
import net.runelite.client.config.FontType;

/**
 * Centralizes hitsplat settings without making the preset selector authoritative.
 * A named preset copies values once; the copied configuration is always rendered.
 */
final class CustomizeALotSettings
{
	private final CustomizeALotConfig config;

	@Inject
	CustomizeALotSettings(CustomizeALotConfig config)
	{
		this.config = config;
	}

	boolean onlyDisplayMine()
	{
		return config.onlyDisplayMine();
	}

	boolean prioritizeMine()
	{
		return config.prioritizeMine();
	}

	boolean hideZeroHitsplats()
	{
		return config.hideZeroHitsplats();
	}

	CustomizeALotLayoutShape layoutShape()
	{
		return config.layoutShape();
	}

	CustomizeALotLayoutDirection layoutDirection()
	{
		return config.layoutDirection();
	}

	CustomizeALotLayoutBehavior layoutBehavior()
	{
		return config.layoutBehavior();
	}

	int minRadius()
	{
		return config.minRadius();
	}

	int maxRadius()
	{
		return config.maxRadius();
	}

	int xSpacing()
	{
		return config.xSpacing();
	}

	int ySpacing()
	{
		return config.ySpacing();
	}

	FontType hitsplatFont()
	{
		return config.hitsplatFont();
	}

	int hitsplatScalePercent()
	{
		return config.hitsplatScalePercent();
	}

	int fadeInDuration()
	{
		return config.fadeInDuration();
	}

	int fullOpacityDuration()
	{
		return config.fullOpacityDuration();
	}

	int fadeOutDuration()
	{
		return config.fadeOutDuration();
	}
}
