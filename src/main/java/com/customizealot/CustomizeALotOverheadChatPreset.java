package com.customizealot;

import java.awt.Color;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.runelite.client.config.FontType;

public enum CustomizeALotOverheadChatPreset
{
	CUSTOM("Custom", runeScapeSettings()),
	RUNESCAPE("RuneScape", runeScapeSettings()),
	RUINED_HEIR("Ruined Heir", ruinedHeirSettings());

	static final CustomizeALotOverheadChatPreset DEFAULT = RUINED_HEIR;

	private final String displayName;
	private final Map<String, Object> settings;

	CustomizeALotOverheadChatPreset(String displayName, Map<String, Object> settings)
	{
		this.displayName = displayName;
		this.settings = settings;
	}

	Map<String, Object> getSettings()
	{
		return settings;
	}

	private static Map<String, Object> runeScapeSettings()
	{
		Map<String, Object> values = new LinkedHashMap<>();
		values.put(CustomizeALotConfig.SHOW_NPC_OVERHEAD_CHAT_KEY, true);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_FONT_KEY, FontType.BOLD);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_COLOR_KEY, Color.YELLOW);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_RELATIONSHIP_COLORS_KEY, false);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_FRIEND_COLOR_KEY, Color.YELLOW);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_CLAN_COLOR_KEY, Color.YELLOW);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_GROUP_IRON_COLOR_KEY, Color.YELLOW);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_EFFECT_KEY,
			CustomizeALotOverheadChatEffect.STATIC);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_SHADOW_KEY, true);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_SHADOW_COLOR_KEY, Color.BLACK);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_X_OFFSET_KEY, 0);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_Y_OFFSET_KEY, 0);
		return Collections.unmodifiableMap(values);
	}

	private static Map<String, Object> ruinedHeirSettings()
	{
		Map<String, Object> values = new LinkedHashMap<>(previousRuinedHeirSettings());
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_SHADOW_COLOR_KEY,
			new Color(0x80171717, true));
		return Collections.unmodifiableMap(values);
	}

	static Map<String, Object> previousRuinedHeirSettings()
	{
		Map<String, Object> values = new LinkedHashMap<>(legacyRuinedHeirSettings());
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_RELATIONSHIP_COLORS_KEY, true);
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_FRIEND_COLOR_KEY,
			new Color(0xFFA5FF40, true));
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_CLAN_COLOR_KEY,
			new Color(0xFF40CFFF, true));
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_GROUP_IRON_COLOR_KEY,
			new Color(0xFFFF4040, true));
		return Collections.unmodifiableMap(values);
	}

	static Map<String, Object> legacyRuinedHeirSettings()
	{
		Map<String, Object> values = new LinkedHashMap<>(runeScapeSettings());
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_COLOR_KEY,
			new Color(0xFF, 0xFF, 0x3F, 0xFF));
		values.put(CustomizeALotConfig.OVERHEAD_CHAT_SHADOW_COLOR_KEY,
			new Color(0x17, 0x17, 0x17, 0xFF));
		return Collections.unmodifiableMap(values);
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
