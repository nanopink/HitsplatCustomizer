package com.customizealot;

public enum CustomizeALotLayoutShape
{
	HEXAGONAL("Hexagonal"),
	DIAMOND("Diamond"),
	GRID("Grid"),
	X("X");

	private final String name;

	CustomizeALotLayoutShape(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
