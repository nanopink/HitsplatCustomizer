package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import net.runelite.client.config.FontType;
import org.junit.Test;

public class CustomizeALotSettingsTest
{
	@Test
	public void defaultGeneralSettingsAllowReplacementHits()
	{
		CustomizeALotConfig config = new CustomizeALotConfig()
		{
		};

		assertFalse(config.disableEnemyHitsplats());
		assertFalse(config.disableAllyHitsplats());
		assertFalse(config.disableMyHitsplats());
		assertEquals(0, config.maxHitsplats());
		assertFalse(config.reuseOldHitsplatSlots());
		assertEquals(550, config.hitsplatReuseInterval());
		assertEquals(100, config.opacityPercent());
		assertSame(CustomizeALotTargetDetection.EITHER, config.largeTargetDetection());
		assertEquals(2, config.largeTargetSize());
		assertEquals(100, config.largeTargetHealthScale());
		assertEquals(100, config.hitsplatScalePercent());
	}

	@Test
	public void namedPresetDoesNotOverrideStoredDisplayValues()
	{
		MutableConfig config = new MutableConfig();
		config.preset = CustomizeALotPreset.RUNESCAPE;
		config.onlyMine = true;
		config.maxRadius = 64;
		CustomizeALotSettings settings = new CustomizeALotSettings(config);

		assertTrue(settings.onlyDisplayMine());
		assertSame(FontType.SMALL, settings.hitsplatFont());
		assertEquals(64, settings.maxRadius());
		assertTrue(config.onlyMine);
		assertEquals(64, config.maxRadius);
	}

	@Test
	public void customUsesStoredDisplayValues()
	{
		MutableConfig config = new MutableConfig();
		config.preset = CustomizeALotPreset.CUSTOM;
		config.onlyMine = true;
		config.maxRadius = 64;
		CustomizeALotSettings settings = new CustomizeALotSettings(config);

		assertTrue(settings.onlyDisplayMine());
		assertEquals(64, settings.maxRadius());
	}

	private static final class MutableConfig implements CustomizeALotConfig
	{
		private CustomizeALotPreset preset;
		private boolean onlyMine;
		private int maxRadius;

		@Override
		public CustomizeALotPreset preset()
		{
			return preset;
		}

		@Override
		public boolean onlyDisplayMine()
		{
			return onlyMine;
		}

		@Override
		public int maxRadius()
		{
			return maxRadius;
		}
	}
}
