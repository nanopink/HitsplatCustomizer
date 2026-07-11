package com.customizealot;

public enum CustomizeALotTargetDetection
{
	FOOTPRINT("Footprint"),
	HEALTH_SCALE("Public health scale"),
	EITHER("Either");

	private final String displayName;

	CustomizeALotTargetDetection(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
