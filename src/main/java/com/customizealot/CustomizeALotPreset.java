package com.customizealot;

public enum CustomizeALotPreset
{
	CUSTOM(
		"Custom",
		false,
		true,
		false,
		CustomizeALotLayoutShape.HEXAGONAL,
		CustomizeALotLayoutDirection.CLOCKWISE,
		CustomizeALotLayoutBehavior.SYMMETRIC,
		100,
		0,
		3,
		-2,
		2,
		30,
		400,
		150),
	RUINED_HEIRS_ONE_TICK(
		"Ruined Heir's 1 tick",
		false,
		true,
		false,
		CustomizeALotLayoutShape.HEXAGONAL,
		CustomizeALotLayoutDirection.CLOCKWISE,
		CustomizeALotLayoutBehavior.SYMMETRIC,
		100,
		0,
		2,
		-2,
		2,
		30,
		400,
		150),
	CHAOS(
		"Chaos",
		false,
		true,
		false,
		CustomizeALotLayoutShape.HEXAGONAL,
		CustomizeALotLayoutDirection.CLOCKWISE,
		CustomizeALotLayoutBehavior.RANDOM,
		100,
		2,
		0,
		-2,
		2,
		30,
		1000,
		120),
	RUNESCAPE(
		"RuneScape",
		false,
		true,
		false,
		CustomizeALotLayoutShape.DIAMOND,
		CustomizeALotLayoutDirection.CLOCKWISE,
		CustomizeALotLayoutBehavior.INCREMENTAL,
		100,
		1,
		2,
		-8,
		-10,
		0,
		1000,
		0),
	STANDARD(
		"Hexagon 2 ticks",
		false,
		true,
		false,
		CustomizeALotLayoutShape.HEXAGONAL,
		CustomizeALotLayoutDirection.CLOCKWISE,
		CustomizeALotLayoutBehavior.INCREMENTAL,
		100,
		0,
		3,
		-2,
		2,
		60,
		700,
		300);

	static final CustomizeALotPreset DEFAULT = RUNESCAPE;

	private final String name;
	private final boolean onlyDisplayMine;
	private final boolean prioritizeMine;
	private final boolean hideZeroHitsplats;
	private final CustomizeALotLayoutShape layoutShape;
	private final CustomizeALotLayoutDirection layoutDirection;
	private final CustomizeALotLayoutBehavior layoutBehavior;
	private final int hitsplatScalePercent;
	private final int minRadius;
	private final int maxRadius;
	private final int xSpacing;
	private final int ySpacing;
	private final int fadeInDuration;
	private final int fullOpacityDuration;
	private final int fadeOutDuration;

	CustomizeALotPreset(
		String name,
		boolean onlyDisplayMine,
		boolean prioritizeMine,
		boolean hideZeroHitsplats,
		CustomizeALotLayoutShape layoutShape,
		CustomizeALotLayoutDirection layoutDirection,
		CustomizeALotLayoutBehavior layoutBehavior,
		int hitsplatScalePercent,
		int minRadius,
		int maxRadius,
		int xSpacing,
		int ySpacing,
		int fadeInDuration,
		int fullOpacityDuration,
		int fadeOutDuration)
	{
		this.name = name;
		this.onlyDisplayMine = onlyDisplayMine;
		this.prioritizeMine = prioritizeMine;
		this.hideZeroHitsplats = hideZeroHitsplats;
		this.layoutShape = layoutShape;
		this.layoutDirection = layoutDirection;
		this.layoutBehavior = layoutBehavior;
		this.hitsplatScalePercent = hitsplatScalePercent;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.xSpacing = xSpacing;
		this.ySpacing = ySpacing;
		this.fadeInDuration = fadeInDuration;
		this.fullOpacityDuration = fullOpacityDuration;
		this.fadeOutDuration = fadeOutDuration;
	}

	boolean isOnlyDisplayMine()
	{
		return onlyDisplayMine;
	}

	boolean isPrioritizeMine()
	{
		return prioritizeMine;
	}

	boolean isHideZeroHitsplats()
	{
		return hideZeroHitsplats;
	}

	CustomizeALotLayoutShape getLayoutShape()
	{
		return layoutShape;
	}

	CustomizeALotLayoutDirection getLayoutDirection()
	{
		return layoutDirection;
	}

	CustomizeALotLayoutBehavior getLayoutBehavior()
	{
		return layoutBehavior;
	}

	int getHitsplatScalePercent()
	{
		return hitsplatScalePercent;
	}

	int getMinRadius()
	{
		return minRadius;
	}

	int getMaxRadius()
	{
		return maxRadius;
	}

	int getXSpacing()
	{
		return xSpacing;
	}

	int getYSpacing()
	{
		return ySpacing;
	}

	int getFadeInDuration()
	{
		return fadeInDuration;
	}

	int getFullOpacityDuration()
	{
		return fullOpacityDuration;
	}

	int getFadeOutDuration()
	{
		return fadeOutDuration;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
