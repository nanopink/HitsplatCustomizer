package com.customizealot;

public enum CustomizeALotHealthScaleMode
{
	FIXED("Fixed"),
	THRESHOLD("Threshold"),
	DYNAMIC("Dynamic");

	private final String displayName;

	CustomizeALotHealthScaleMode(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
