package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import net.runelite.client.plugins.PluginDescriptor;
import org.junit.Test;

public class CustomizeALotConfigGroupMigrationTest
{
	@Test
	public void publicRenamePreservesTheLegacyEnablementKey()
	{
		PluginDescriptor descriptor = CustomizeALotPlugin.class.getAnnotation(PluginDescriptor.class);

		assertEquals("Customize a Lot", descriptor.name());
		assertEquals("hitsplatcustomizerplugin", descriptor.configName());
	}

	@Test
	public void copiesLegacyValuesOverSeededNewDefaults()
	{
		Map<String, String> values = new LinkedHashMap<>();
		values.put("hitsplat-customizer.preset", "CHAOS");
		values.put("hitsplat-customizer.opacityPercent", "73");
		values.put("customize-a-lot.preset", "RUNESCAPE");
		values.put("customize-a-lot.opacityPercent", "100");

		CustomizeALotPlugin.copyConfigurationGroup(
			Arrays.asList(
				"hitsplat-customizer.preset",
				"hitsplat-customizer.opacityPercent"),
			"hitsplat-customizer",
			"customize-a-lot",
			(group, key) -> values.get(group + "." + key),
			(key, value) -> values.put("customize-a-lot." + key, value));

		assertEquals("CHAOS", values.get("customize-a-lot.preset"));
		assertEquals("73", values.get("customize-a-lot.opacityPercent"));
	}

	@Test
	public void ignoresKeysOutsideTheLegacyGroupAndMissingValues()
	{
		Map<String, String> values = new LinkedHashMap<>();
		values.put("hitsplat-customizer.valid", "copied");

		CustomizeALotPlugin.copyConfigurationGroup(
			Arrays.asList(
				null,
				"hitsplat-customizer",
				"hitsplat-customizer.",
				"other-group.foreign",
				"hitsplat-customizer.missing",
				"hitsplat-customizer.valid"),
			"hitsplat-customizer",
			"customize-a-lot",
			(group, key) -> values.get(group + "." + key),
			(key, value) -> values.put("customize-a-lot." + key, value));

		assertEquals("copied", values.get("customize-a-lot.valid"));
		assertFalse(values.containsKey("customize-a-lot.missing"));
		assertFalse(values.containsKey("customize-a-lot.foreign"));
	}

	@Test
	public void noOpsForInvalidOrSameGroupRequests()
	{
		Map<String, String> written = new LinkedHashMap<>();

		CustomizeALotPlugin.copyConfigurationGroup(
			null,
			"hitsplat-customizer",
			"customize-a-lot",
			(group, key) -> "value",
			written::put);
		CustomizeALotPlugin.copyConfigurationGroup(
			Arrays.asList("customize-a-lot.preset"),
			"customize-a-lot",
			"customize-a-lot",
			(group, key) -> "value",
			written::put);

		assertEquals(0, written.size());
	}
}
