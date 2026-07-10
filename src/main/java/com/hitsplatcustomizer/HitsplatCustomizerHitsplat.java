package com.hitsplatcustomizer;

final class HitsplatCustomizerHitsplat
{
	private final int hitsplatType;
	private final int amount;
	private final int position;
	private final int appearsOnGameCycle;
	private final int fullOpacityStartsOnGameCycle;
	private final int fadeOutStartsOnGameCycle;
	private final int expiresOnGameCycle;
	private final long sequence;
	private final boolean mine;
	private final boolean fake;

	HitsplatCustomizerHitsplat(
		int hitsplatType,
		int amount,
		int position,
		int appearsOnGameCycle,
		int fullOpacityStartsOnGameCycle,
		int fadeOutStartsOnGameCycle,
		int expiresOnGameCycle,
		long sequence,
		boolean mine)
	{
		this(
			hitsplatType,
			amount,
			position,
			appearsOnGameCycle,
			fullOpacityStartsOnGameCycle,
			fadeOutStartsOnGameCycle,
			expiresOnGameCycle,
			sequence,
			mine,
			false);
	}

	HitsplatCustomizerHitsplat(
		int hitsplatType,
		int amount,
		int position,
		int appearsOnGameCycle,
		int fullOpacityStartsOnGameCycle,
		int fadeOutStartsOnGameCycle,
		int expiresOnGameCycle,
		long sequence,
		boolean mine,
		boolean fake)
	{
		this.hitsplatType = hitsplatType;
		this.amount = amount;
		this.position = position;
		this.appearsOnGameCycle = appearsOnGameCycle;
		this.fullOpacityStartsOnGameCycle = fullOpacityStartsOnGameCycle;
		this.fadeOutStartsOnGameCycle = fadeOutStartsOnGameCycle;
		this.expiresOnGameCycle = expiresOnGameCycle;
		this.sequence = sequence;
		this.mine = mine;
		this.fake = fake;
	}

	int getHitsplatType()
	{
		return hitsplatType;
	}

	int getAmount()
	{
		return amount;
	}

	int getPosition()
	{
		return position;
	}

	int getAppearsOnGameCycle()
	{
		return appearsOnGameCycle;
	}

	long getSequence()
	{
		return sequence;
	}

	boolean isMine()
	{
		return mine;
	}

	boolean isFake()
	{
		return fake;
	}

	HitsplatCustomizerHitsplat asMine(int mineHitsplatType)
	{
		return new HitsplatCustomizerHitsplat(
			mineHitsplatType,
			amount,
			position,
			appearsOnGameCycle,
			fullOpacityStartsOnGameCycle,
			fadeOutStartsOnGameCycle,
			expiresOnGameCycle,
			sequence,
			true,
			false);
	}

	boolean isExpired(int gameCycle)
	{
		return expiresOnGameCycle < gameCycle;
	}

	float getAlpha(int gameCycle)
	{
		if (gameCycle < appearsOnGameCycle)
		{
			return 0.0f;
		}

		if (gameCycle < fullOpacityStartsOnGameCycle)
		{
			return progressBetween(gameCycle, appearsOnGameCycle, fullOpacityStartsOnGameCycle);
		}

		if (gameCycle < fadeOutStartsOnGameCycle)
		{
			return 1.0f;
		}

		if (expiresOnGameCycle <= fadeOutStartsOnGameCycle)
		{
			return 0.0f;
		}

		return 1.0f - progressBetween(gameCycle, fadeOutStartsOnGameCycle, expiresOnGameCycle);
	}

	private static float progressBetween(int gameCycle, int startsOnGameCycle, int endsOnGameCycle)
	{
		int cycles = endsOnGameCycle - startsOnGameCycle;
		if (cycles <= 0)
		{
			return 1.0f;
		}

		float progress = (gameCycle - startsOnGameCycle) / (float) cycles;
		return Math.max(0.0f, Math.min(1.0f, progress));
	}
}
