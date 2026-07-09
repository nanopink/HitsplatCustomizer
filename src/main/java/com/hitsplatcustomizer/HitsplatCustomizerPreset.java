package com.hitsplatcustomizer;

public enum HitsplatCustomizerPreset
{
	CUSTOM(
		"Custom",
		false,
		false,
		false,
		0,
		false,
		true,
		false,
		HitsplatCustomizerLayoutShape.HEXAGONAL,
		HitsplatCustomizerLayoutDirection.CLOCKWISE,
		HitsplatCustomizerLayoutBehavior.SYMMETRIC,
		0,
		3,
		-2,
		2,
		0,
		0,
		30,
		400,
		150),
	RUINED_HEIRS_ONE_TICK(
		"Ruined Heir's 1 tick",
		false,
		false,
		false,
		0,
		false,
		true,
		false,
		HitsplatCustomizerLayoutShape.HEXAGONAL,
		HitsplatCustomizerLayoutDirection.CLOCKWISE,
		HitsplatCustomizerLayoutBehavior.SYMMETRIC,
		0,
		2,
		-2,
		2,
		0,
		0,
		30,
		400,
		150),
	CHAOS(
		"Chaos",
		false,
		false,
		false,
		0,
		false,
		true,
		false,
		HitsplatCustomizerLayoutShape.HEXAGONAL,
		HitsplatCustomizerLayoutDirection.CLOCKWISE,
		HitsplatCustomizerLayoutBehavior.RANDOM,
		0,
		0,
		-2,
		2,
		0,
		0,
		30,
		1000,
		120),
	RUNESCAPE(
		"RuneScape",
		false,
		false,
		false,
		0,
		false,
		true,
		false,
		HitsplatCustomizerLayoutShape.DIAMOND,
		HitsplatCustomizerLayoutDirection.CLOCKWISE,
		HitsplatCustomizerLayoutBehavior.INCREMENTAL,
		1,
		2,
		-8,
		-10,
		0,
		5,
		0,
		1000,
		0),
	STANDARD(
		"Hexagon 2 ticks",
		false,
		false,
		false,
		0,
		false,
		true,
		false,
		HitsplatCustomizerLayoutShape.HEXAGONAL,
		HitsplatCustomizerLayoutDirection.CLOCKWISE,
		HitsplatCustomizerLayoutBehavior.INCREMENTAL,
		0,
		3,
		-2,
		2,
		0,
		0,
		60,
		700,
		300);

	static final HitsplatCustomizerPreset DEFAULT = RUNESCAPE;

	private final String name;
	private final boolean disableEnemyHitsplats;
	private final boolean disableAllyHitsplats;
	private final boolean disableMyHitsplats;
	private final int maxHitsplats;
	private final boolean onlyDisplayMine;
	private final boolean prioritizeMine;
	private final boolean hideZeroHitsplats;
	private final HitsplatCustomizerLayoutShape layoutShape;
	private final HitsplatCustomizerLayoutDirection layoutDirection;
	private final HitsplatCustomizerLayoutBehavior layoutBehavior;
	private final int minRadius;
	private final int maxRadius;
	private final int xSpacing;
	private final int ySpacing;
	private final int globalXOffset;
	private final int globalYOffset;
	private final int fadeInDuration;
	private final int fullOpacityDuration;
	private final int fadeOutDuration;

	HitsplatCustomizerPreset(
		String name,
		boolean disableEnemyHitsplats,
		boolean disableAllyHitsplats,
		boolean disableMyHitsplats,
		int maxHitsplats,
		boolean onlyDisplayMine,
		boolean prioritizeMine,
		boolean hideZeroHitsplats,
		HitsplatCustomizerLayoutShape layoutShape,
		HitsplatCustomizerLayoutDirection layoutDirection,
		HitsplatCustomizerLayoutBehavior layoutBehavior,
		int minRadius,
		int maxRadius,
		int xSpacing,
		int ySpacing,
		int globalXOffset,
		int globalYOffset,
		int fadeInDuration,
		int fullOpacityDuration,
		int fadeOutDuration)
	{
		this.name = name;
		this.disableEnemyHitsplats = disableEnemyHitsplats;
		this.disableAllyHitsplats = disableAllyHitsplats;
		this.disableMyHitsplats = disableMyHitsplats;
		this.maxHitsplats = maxHitsplats;
		this.onlyDisplayMine = onlyDisplayMine;
		this.prioritizeMine = prioritizeMine;
		this.hideZeroHitsplats = hideZeroHitsplats;
		this.layoutShape = layoutShape;
		this.layoutDirection = layoutDirection;
		this.layoutBehavior = layoutBehavior;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.xSpacing = xSpacing;
		this.ySpacing = ySpacing;
		this.globalXOffset = globalXOffset;
		this.globalYOffset = globalYOffset;
		this.fadeInDuration = fadeInDuration;
		this.fullOpacityDuration = fullOpacityDuration;
		this.fadeOutDuration = fadeOutDuration;
	}

	boolean isDisableEnemyHitsplats()
	{
		return disableEnemyHitsplats;
	}

	boolean isDisableAllyHitsplats()
	{
		return disableAllyHitsplats;
	}

	boolean isDisableMyHitsplats()
	{
		return disableMyHitsplats;
	}

	int getMaxHitsplats()
	{
		return maxHitsplats;
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

	HitsplatCustomizerLayoutShape getLayoutShape()
	{
		return layoutShape;
	}

	HitsplatCustomizerLayoutDirection getLayoutDirection()
	{
		return layoutDirection;
	}

	HitsplatCustomizerLayoutBehavior getLayoutBehavior()
	{
		return layoutBehavior;
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

	int getGlobalXOffset()
	{
		return globalXOffset;
	}

	int getGlobalYOffset()
	{
		return globalYOffset;
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
