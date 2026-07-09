package com.hitsplatcustomizer;

public enum HitsplatCustomizerLayoutShape
{
	HEXAGONAL("Hexagonal"),
	DIAMOND("Diamond"),
	GRID("Grid"),
	X("X");

	private final String name;

	HitsplatCustomizerLayoutShape(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
