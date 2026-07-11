package com.customizealot;

public enum CustomizeALotHealthBarGradient
{
	SOLID("Solid"),
	HORIZONTAL("Horizontal"),
	VERTICAL("Vertical"),
	HEALTH_BASED("Health based");

	private final String displayName;

	CustomizeALotHealthBarGradient(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
