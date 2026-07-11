package com.customizealot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComboBox;
import net.runelite.client.config.FontType;
import org.junit.Test;

public class CustomizeALotSectionPresetWorkflowTest
{
	@Test
	public void everySectionDefaultsToItsRuneScapePreset()
	{
		CustomizeALotConfig config = new CustomizeALotConfig()
		{
		};

		assertSame(CustomizeALotPreset.RUNESCAPE, config.preset());
		assertSame(CustomizeALotHealthBarPreset.RUNESCAPE, config.healthBarPreset());
		assertSame(CustomizeALotOverheadChatPreset.RUNESCAPE, config.overheadChatPreset());
		assertSame(CustomizeALotHeadIconPreset.RUNESCAPE, config.headIconPreset());
	}

	@Test
	public void healthPresetCopiesEveryControlledSettingOnce()
	{
		Map<String, Object> values = new LinkedHashMap<>();
		CustomizeALotPlugin.applyHealthBarPreset(
			CustomizeALotHealthBarPreset.RUINED_HEIR,
			values::put);

		assertEquals(31, values.size());
		assertSame(CustomizeALotHealthBarStyle.SOLID,
			values.get(CustomizeALotConfig.HEALTH_BAR_STYLE_KEY));
		assertEquals(49.0,
			((Number) values.get(CustomizeALotConfig.HEALTH_BAR_SOLID_WIDTH_KEY)).doubleValue(),
			0.0);
		assertEquals(5.0,
			((Number) values.get(CustomizeALotConfig.HEALTH_BAR_HEIGHT_KEY)).doubleValue(),
			0.0);
		assertEquals(true, values.get(CustomizeALotConfig.HEALTH_BAR_SEGMENTS_ENABLED_KEY));
		assertEquals(new Color(0x3F000000, true),
			values.get(CustomizeALotConfig.HEALTH_BAR_SEGMENT_COLOR_KEY));
		assertEquals(0.5,
			((Number) values.get(CustomizeALotConfig.HEALTH_BAR_SEGMENT_THICKNESS_KEY)).doubleValue(),
			0.0);
		assertEquals(Color.RED,
			values.get(CustomizeALotConfig.HEALTH_BAR_DAMAGE_TRAIL_COLOR_KEY));
		assertEquals(0.5,
			((Number) values.get(CustomizeALotConfig.HEALTH_BAR_BORDER_THICKNESS_KEY)).doubleValue(),
			0.0);
		assertEquals(0.5,
			((Number) values.get(CustomizeALotConfig.HEALTH_BAR_CORNER_RADIUS_KEY)).doubleValue(),
			0.0);
		assertSame(CustomizeALotHealthBarGradientCoordinates.FULL_BAR,
			values.get(CustomizeALotConfig.HEALTH_BAR_FRONT_GRADIENT_COORDINATES_KEY));
		assertSame(CustomizeALotHealthBarGradientCoordinates.FULL_BAR,
			values.get(CustomizeALotConfig.HEALTH_BAR_BACK_GRADIENT_COORDINATES_KEY));
		assertFalse(values.containsKey(CustomizeALotConfig.HEALTH_BARS_ENABLED_KEY));
		assertFalse(values.containsKey(CustomizeALotConfig.HEALTH_BAR_GRADIENT_COORDINATES_KEY));
		assertFalse(values.containsKey(CustomizeALotConfig.HEALTH_BAR_PUBLIC_UNITS_PER_SEGMENT_KEY));
	}

	@Test
	public void chatAndHeadIconPresetsCopyOnlyTheirSectionSettings()
	{
		Map<String, Object> chat = new LinkedHashMap<>();
		Map<String, Object> icons = new LinkedHashMap<>();
		CustomizeALotPlugin.applyOverheadChatPreset(
			CustomizeALotOverheadChatPreset.RUNESCAPE,
			chat::put);
		CustomizeALotPlugin.applyHeadIconPreset(
			CustomizeALotHeadIconPreset.RUNESCAPE,
			icons::put);

		assertEquals(8, chat.size());
		assertSame(FontType.BOLD, chat.get(CustomizeALotConfig.OVERHEAD_CHAT_FONT_KEY));
		assertEquals(true, chat.get(CustomizeALotConfig.SHOW_NPC_OVERHEAD_CHAT_KEY));
		assertFalse(chat.containsKey(CustomizeALotConfig.OVERHEAD_CHAT_ENABLED_KEY));

		assertEquals(8, icons.size());
		assertEquals(100, icons.get(CustomizeALotConfig.HEAD_ICON_SCALE_PERCENT_KEY));
		assertEquals(2, icons.get(CustomizeALotConfig.HEAD_ICON_SPACING_KEY));
		assertFalse(icons.containsKey(CustomizeALotConfig.HEAD_ICONS_ENABLED_KEY));
	}

	@Test
	public void ruinedHeirChatPresetKeepsRuneScapeBehaviorWithItsOwnColors()
	{
		Map<String, Object> values = new LinkedHashMap<>();
		CustomizeALotPlugin.applyOverheadChatPreset(
			CustomizeALotOverheadChatPreset.RUINED_HEIR,
			values::put);

		assertEquals(8, values.size());
		assertSame(FontType.BOLD, values.get(CustomizeALotConfig.OVERHEAD_CHAT_FONT_KEY));
		assertSame(CustomizeALotOverheadChatEffect.STATIC,
			values.get(CustomizeALotConfig.OVERHEAD_CHAT_EFFECT_KEY));
		assertEquals(new Color(0xFF, 0xFF, 0x3F, 0xFF),
			values.get(CustomizeALotConfig.OVERHEAD_CHAT_COLOR_KEY));
		assertEquals(new Color(0x17, 0x17, 0x17, 0xFF),
			values.get(CustomizeALotConfig.OVERHEAD_CHAT_SHADOW_COLOR_KEY));
	}

	@Test
	public void runeScapePresetsMatchFreshConfigurationDefaults()
	{
		CustomizeALotConfig config = new CustomizeALotConfig()
		{
		};

		assertTrue(CustomizeALotPlugin.healthBarPresetMatchesConfig(
			CustomizeALotHealthBarPreset.RUNESCAPE, config));
		assertTrue(CustomizeALotPlugin.overheadChatPresetMatchesConfig(
			CustomizeALotOverheadChatPreset.RUNESCAPE, config));
		assertTrue(CustomizeALotPlugin.headIconPresetMatchesConfig(
			CustomizeALotHeadIconPreset.RUNESCAPE, config));
	}

	@Test
	public void ruinedHeirChatPresetMatchesItsAppliedConfiguration()
	{
		CustomizeALotConfig config = new CustomizeALotConfig()
		{
			@Override
			public CustomizeALotOverheadChatPreset overheadChatPreset()
			{
				return CustomizeALotOverheadChatPreset.RUINED_HEIR;
			}

			@Override
			public Color overheadChatColor()
			{
				return new Color(0xFF, 0xFF, 0x3F, 0xFF);
			}

			@Override
			public Color overheadChatShadowColor()
			{
				return new Color(0x17, 0x17, 0x17, 0xFF);
			}
		};

		assertTrue(CustomizeALotPlugin.overheadChatPresetMatchesConfig(
			CustomizeALotOverheadChatPreset.RUINED_HEIR, config));
		assertFalse(CustomizeALotPlugin.overheadChatPresetMatchesConfig(
			CustomizeALotOverheadChatPreset.RUNESCAPE, config));
	}

	@Test
	public void mismatchedExistingValuesAreRecognizedAsCustomWithoutOverwrite()
	{
		CustomizeALotConfig config = new CustomizeALotConfig()
		{
			@Override
			public double healthBarSolidWidth()
			{
				return 73.5;
			}

			@Override
			public int overheadChatXOffset()
			{
				return 11;
			}

			@Override
			public int headIconSpacing()
			{
				return 7;
			}
		};

		assertFalse(CustomizeALotPlugin.healthBarPresetMatchesConfig(
			CustomizeALotHealthBarPreset.RUNESCAPE, config));
		assertFalse(CustomizeALotPlugin.overheadChatPresetMatchesConfig(
			CustomizeALotOverheadChatPreset.RUNESCAPE, config));
		assertFalse(CustomizeALotPlugin.headIconPresetMatchesConfig(
			CustomizeALotHeadIconPreset.RUNESCAPE, config));
	}

	@Test
	public void legacyHealthValuesFlowIntoNewKeysAndAreRecognizedAsCustom()
	{
		CustomizeALotConfig legacy = new CustomizeALotConfig()
		{
			@Override
			public CustomizeALotHealthBarGradientCoordinates healthBarGradientCoordinates()
			{
				return CustomizeALotHealthBarGradientCoordinates.FULL_BAR;
			}

			@Override
			public int healthBarPublicUnitsPerSegment()
			{
				return 8;
			}
		};

		assertSame(CustomizeALotHealthBarGradientCoordinates.FULL_BAR,
			legacy.healthBarFrontGradientCoordinates());
		assertSame(CustomizeALotHealthBarGradientCoordinates.FULL_BAR,
			legacy.healthBarBackGradientCoordinates());
		assertEquals(8, legacy.healthBarHitpointsPerSegment());
		assertFalse(CustomizeALotPlugin.healthBarPresetMatchesConfig(
			CustomizeALotHealthBarPreset.RUNESCAPE, legacy));
	}

	@Test
	public void controlledEditsSelectOnlyTheirOwnCustomPreset()
	{
		assertTrue(CustomizeALotPlugin.shouldSwitchHealthBarToCustom(
			false,
			CustomizeALotConfig.HEALTH_BAR_FRONT_COLOR_KEY,
			CustomizeALotHealthBarPreset.RUNESCAPE));
		assertTrue(CustomizeALotPlugin.shouldSwitchOverheadChatToCustom(
			false,
			CustomizeALotConfig.OVERHEAD_CHAT_FONT_KEY,
			CustomizeALotOverheadChatPreset.RUNESCAPE));
		assertTrue(CustomizeALotPlugin.shouldSwitchHeadIconToCustom(
			false,
			CustomizeALotConfig.HEAD_ICON_SPACING_KEY,
			CustomizeALotHeadIconPreset.RUNESCAPE));

		assertFalse(CustomizeALotPlugin.shouldSwitchHealthBarToCustom(
			false,
			CustomizeALotConfig.HEALTH_BARS_ENABLED_KEY,
			CustomizeALotHealthBarPreset.RUNESCAPE));
		assertFalse(CustomizeALotPlugin.shouldSwitchOverheadChatToCustom(
			false,
			CustomizeALotConfig.OVERHEAD_CHAT_ENABLED_KEY,
			CustomizeALotOverheadChatPreset.RUNESCAPE));
		assertFalse(CustomizeALotPlugin.shouldSwitchHeadIconToCustom(
			false,
			CustomizeALotConfig.HEAD_ICONS_ENABLED_KEY,
			CustomizeALotHeadIconPreset.RUNESCAPE));
		assertFalse(CustomizeALotPlugin.shouldSwitchHeadIconToCustom(
			true,
			CustomizeALotConfig.HEAD_ICON_SPACING_KEY,
			CustomizeALotHeadIconPreset.RUNESCAPE));
	}

	@Test
	public void sectionPresetParsersAcceptStoredAndDisplayNames()
	{
		assertSame(CustomizeALotHealthBarPreset.RUINED_HEIR,
			CustomizeALotPlugin.healthBarPresetFromValue("RUINED_HEIR"));
		assertSame(CustomizeALotHealthBarPreset.RUINED_HEIR,
			CustomizeALotPlugin.healthBarPresetFromValue("Ruined Heir"));
		assertSame(CustomizeALotOverheadChatPreset.RUNESCAPE,
			CustomizeALotPlugin.overheadChatPresetFromValue("RuneScape"));
		assertSame(CustomizeALotOverheadChatPreset.RUINED_HEIR,
			CustomizeALotPlugin.overheadChatPresetFromValue("RUINED_HEIR"));
		assertSame(CustomizeALotOverheadChatPreset.RUINED_HEIR,
			CustomizeALotPlugin.overheadChatPresetFromValue("Ruined Heir"));
		assertSame(CustomizeALotHeadIconPreset.CUSTOM,
			CustomizeALotPlugin.headIconPresetFromValue("CUSTOM"));
	}

	@Test
	public void configRefreshDetectionRecognizesEverySectionPresetSelector()
	{
		assertTrue(CustomizeALotPlugin.containsPresetSelector(
			new JComboBox<>(CustomizeALotHealthBarPreset.values())));
		assertTrue(CustomizeALotPlugin.containsPresetSelector(
			new JComboBox<>(CustomizeALotOverheadChatPreset.values())));
		assertTrue(CustomizeALotPlugin.containsPresetSelector(
			new JComboBox<>(CustomizeALotHeadIconPreset.values())));
	}

	@Test
	public void pendingCustomSelectionRequiresTheSameNamedSectionPreset()
	{
		assertTrue(CustomizeALotPlugin.shouldApplyPendingCustomSelectionForNamedPreset(
			8, 8, false, true));
		assertFalse(CustomizeALotPlugin.shouldApplyPendingCustomSelectionForNamedPreset(
			8, 9, false, true));
		assertFalse(CustomizeALotPlugin.shouldApplyPendingCustomSelectionForNamedPreset(
			8, 8, true, true));
		assertFalse(CustomizeALotPlugin.shouldApplyPendingCustomSelectionForNamedPreset(
			8, 8, false, false));
	}
}
