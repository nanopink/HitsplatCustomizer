package com.customizealot;

public enum CustomizeALotLayoutBehavior
{
	INCREMENTAL("Incremental"),
	SYMMETRIC("Symmetrical"),
	RANDOM("Random");

	private final String name;

	CustomizeALotLayoutBehavior(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
