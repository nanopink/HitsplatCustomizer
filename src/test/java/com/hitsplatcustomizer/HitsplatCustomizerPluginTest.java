package com.hitsplatcustomizer;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class HitsplatCustomizerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(HitsplatCustomizerPlugin.class);
		RuneLite.main(args);
	}
}
