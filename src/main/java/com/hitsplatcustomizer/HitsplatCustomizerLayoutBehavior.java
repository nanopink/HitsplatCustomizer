package com.hitsplatcustomizer;

public enum HitsplatCustomizerLayoutBehavior
{
	INCREMENTAL("Incremental"),
	SYMMETRIC("Symmetrical"),
	RANDOM("Random");

	private final String name;

	HitsplatCustomizerLayoutBehavior(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
