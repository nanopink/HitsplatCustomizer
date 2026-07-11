package com.customizealot;

public enum CustomizeALotHealthBarGradientCoordinates
{
	SEGMENT("Relative to each segment"),
	FULL_BAR("Absolute across full bar");

	private final String displayName;

	CustomizeALotHealthBarGradientCoordinates(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
