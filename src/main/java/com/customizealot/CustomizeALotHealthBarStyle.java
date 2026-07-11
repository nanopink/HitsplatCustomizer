package com.customizealot;

public enum CustomizeALotHealthBarStyle
{
	NATIVE("RuneScape"),
	SOLID("Custom");

	private final String displayName;

	CustomizeALotHealthBarStyle(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
