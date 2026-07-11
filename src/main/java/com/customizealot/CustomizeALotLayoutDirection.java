package com.customizealot;

public enum CustomizeALotLayoutDirection
{
	CLOCKWISE("Clockwise"),
	COUNTERCLOCKWISE("Counterclockwise");

	private final String name;

	CustomizeALotLayoutDirection(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
