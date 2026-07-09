package com.hitsplatcustomizer;

public enum HitsplatCustomizerLayoutDirection
{
	CLOCKWISE("Clockwise"),
	COUNTERCLOCKWISE("Counterclockwise");

	private final String name;

	HitsplatCustomizerLayoutDirection(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
