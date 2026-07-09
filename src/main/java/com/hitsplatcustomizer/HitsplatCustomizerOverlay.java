package com.hitsplatcustomizer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class HitsplatCustomizerOverlay extends Overlay
{
	private static final BufferedImage ANCHOR_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private static final int SLOT_WIDTH = 30;
	private static final int SLOT_HEIGHT = 20;
	private static final int SLOT_EDGE_CUT = 7;
	private static final int MAX_TRANSFORM_DEPTH = 8;

	private static final int[][] STANDARD_HEALTH_SPRITES = {
		{30, SpriteID.StandardHealth30.FRONT, SpriteID.StandardHealth30.BACK},
		{40, SpriteID.StandardHealth40.FRONT, SpriteID.StandardHealth40.BACK},
		{50, SpriteID.StandardHealth50.FRONT, SpriteID.StandardHealth50.BACK},
		{60, SpriteID.StandardHealth60.FRONT, SpriteID.StandardHealth60.BACK},
		{70, SpriteID.StandardHealth70.FRONT, SpriteID.StandardHealth70.BACK},
		{80, SpriteID.StandardHealth80.FRONT, SpriteID.StandardHealth80.BACK},
		{90, SpriteID.StandardHealth90.FRONT, SpriteID.StandardHealth90.BACK},
		{100, SpriteID.StandardHealth100.FRONT, SpriteID.StandardHealth100.BACK},
		{120, SpriteID.StandardHealth120.FRONT, SpriteID.StandardHealth120.BACK},
		{140, SpriteID.StandardHealth140.FRONT, SpriteID.StandardHealth140.BACK},
		{160, SpriteID.StandardHealth160.FRONT, SpriteID.StandardHealth160.BACK}
	};

	private final Client client;
	private final HitsplatCustomizerPlugin plugin;
	private final HitsplatCustomizerConfig config;
	private final Map<Integer, HitsplatCustomizerHitmarkDefinition> hitmarkDefinitions = new HashMap<>();
	private final Map<Integer, HitsplatCustomizerSprite> sprites = new HashMap<>();

	@Inject
	HitsplatCustomizerOverlay(Client client, HitsplatCustomizerPlugin plugin, HitsplatCustomizerConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.UNDER_WIDGETS);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.getHitsplats().isEmpty() && plugin.getNativeUiSuppressedUntilGameCycle().isEmpty())
		{
			return null;
		}

		Graphics2D overlayGraphics = (Graphics2D) graphics.create();
		try
		{
			overlayGraphics.setFont(FontManager.getRunescapeSmallFont());
			overlayGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			overlayGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			int gameCycle = client.getGameCycle();
			Set<Actor> healthBarsDrawn = new HashSet<>();
			for (Map.Entry<Actor, CopyOnWriteArrayList<HitsplatCustomizerHitsplat>> entry : plugin.getHitsplats().entrySet())
			{
				if (renderActorHitsplats(overlayGraphics, entry.getKey(), entry.getValue(), gameCycle))
				{
					healthBarsDrawn.add(entry.getKey());
				}
			}

			for (Actor actor : plugin.getNativeUiSuppressedUntilGameCycle().keySet())
			{
				if (!healthBarsDrawn.contains(actor) && plugin.isNativeUiSuppressed(actor, gameCycle))
				{
					drawSuppressedHealthBar(overlayGraphics, actor);
				}
			}
		}
		finally
		{
			overlayGraphics.dispose();
		}

		return null;
	}

	private boolean renderActorHitsplats(Graphics2D graphics, Actor actor, List<HitsplatCustomizerHitsplat> hitsplats, int gameCycle)
	{
		if (plugin.shouldDisableHitsplatsForActor(actor))
		{
			return false;
		}

		List<HitsplatCustomizerHitsplat> visibleHitsplats = new ArrayList<>(hitsplats.size());
		for (HitsplatCustomizerHitsplat hitsplat : hitsplats)
		{
			if (hitsplat.isExpired(gameCycle)
				|| config.hideZeroHitsplats() && hitsplat.getAmount() == 0
				|| config.onlyDisplayMine() && !hitsplat.isMine())
			{
				continue;
			}

			visibleHitsplats.add(hitsplat);
		}

		if (visibleHitsplats.isEmpty())
		{
			return false;
		}

		drawSuppressedHealthBar(graphics, actor);

		Point anchor = actor.getCanvasImageLocation(ANCHOR_IMAGE, actor.getLogicalHeight() / 2);
		if (anchor == null)
		{
			return true;
		}

		visibleHitsplats.sort(Comparator.comparingLong(HitsplatCustomizerHitsplat::getSequence));
		int maxHitsplats = config.maxHitsplats();
		for (HitsplatCustomizerHitsplat hitsplat : visibleHitsplats)
		{
			if (maxHitsplats > 0 && hitsplat.getPosition() >= maxHitsplats)
			{
				continue;
			}

			if (!HitsplatCustomizerLayout.isPositionWithinRadiusLimit(hitsplat.getPosition(), config.minRadius(), config.maxRadius(), config.layoutShape()))
			{
				continue;
			}

			HitsplatCustomizerOffset offset = HitsplatCustomizerLayout.offsetFor(
				hitsplat.getPosition(),
				SLOT_WIDTH,
				SLOT_HEIGHT,
				SLOT_EDGE_CUT,
				config.layoutShape(),
				config.layoutDirection(),
				config.layoutBehavior(),
				config.xSpacing(),
				config.ySpacing(),
				config.minRadius());
			drawHitsplat(
				graphics,
				hitsplat,
				anchor.getX() + offset.getX() + xOffsetFor(actor),
				anchor.getY() + offset.getY() - yOffsetFor(actor),
				gameCycle);
		}

		return true;
	}

	private void drawHitsplat(Graphics2D graphics, HitsplatCustomizerHitsplat hitsplat, int centerX, int centerY, int gameCycle)
	{
		HitsplatCustomizerHitmarkDefinition definition = getDefinition(hitsplat.getHitsplatType());
		HitsplatCustomizerSprite first = getSprite(definition.getFirstSpriteId());
		HitsplatCustomizerSprite middle = getSprite(definition.getMiddleSpriteId());
		HitsplatCustomizerSprite second = getSprite(definition.getSecondSpriteId());
		HitsplatCustomizerSprite last = getSprite(definition.getLastSpriteId());

		String amountText = definition.formatAmount(hitsplat.getAmount());
		FontMetrics metrics = graphics.getFontMetrics();
		int textWidth = metrics.stringWidth(amountText);

		int firstWidth = width(first);
		int middleWidth = width(middle);
		int secondWidth = width(second);
		int lastWidth = width(last);
		int maxHeight = Math.max(SLOT_HEIGHT, Math.max(Math.max(height(first), height(middle)), Math.max(height(second), height(last))));

		int repeatCount = 0;
		if (middleWidth > 0)
		{
			repeatCount = second == null && last == null ? 1 : textWidth / middleWidth + 1;
		}

		int totalWidth = 0;
		int firstX = totalWidth;
		totalWidth += firstWidth;
		totalWidth += 2;

		int secondX = totalWidth;
		totalWidth += secondWidth;

		int middleX = totalWidth;
		int textX = totalWidth;
		if (middleWidth > 0)
		{
			int repeatedWidth = repeatCount * middleWidth;
			totalWidth += repeatedWidth;
			textX += (repeatedWidth - textWidth) / 2;
		}
		else
		{
			totalWidth += textWidth;
		}

		int lastX = totalWidth;
		totalWidth += lastWidth;

		int x = centerX - totalWidth / 2;
		int y = centerY - maxHeight / 2;
		int textBaseline = y + definition.getTextYOffset() + 15;
		if (first == null && middle == null && second == null && last == null)
		{
			textBaseline = y + (maxHeight - metrics.getHeight()) / 2 + metrics.getAscent();
		}

		Composite oldComposite = graphics.getComposite();
		float alpha = hitsplat.getAlpha(gameCycle) * opacityMultiplier();
		try
		{
			graphics.setComposite(AlphaComposite.SrcOver.derive(alpha));
			drawSprite(graphics, first, x + firstX, y);
			drawSprite(graphics, second, x + secondX, y);
			for (int i = 0; i < repeatCount; i++)
			{
				drawSprite(graphics, middle, x + middleX + i * middleWidth, y);
			}
			drawSprite(graphics, last, x + lastX, y);

			graphics.setColor(Color.BLACK);
			graphics.drawString(amountText, x + textX + 1, textBaseline + 1);
			graphics.setColor(new Color(definition.getTextColor() | 0xFF000000, true));
			graphics.drawString(amountText, x + textX, textBaseline);
		}
		finally
		{
			graphics.setComposite(oldComposite);
		}
	}

	private void drawSuppressedHealthBar(Graphics2D graphics, Actor actor)
	{
		int ratio = actor.getHealthRatio();
		int scale = actor.getHealthScale();
		if (ratio < 0 || scale <= 0)
		{
			return;
		}

		Point anchor = actor.getCanvasImageLocation(ANCHOR_IMAGE, actor.getLogicalHeight() + 15);
		if (anchor == null)
		{
			return;
		}

		int[] healthSprites = healthSpritesFor(scale);
		HitsplatCustomizerSprite front = getSprite(healthSprites[1]);
		HitsplatCustomizerSprite back = getSprite(healthSprites[2]);
		if (front == null || back == null)
		{
			drawFallbackHealthBar(graphics, anchor, ratio, scale, healthSprites[0]);
			return;
		}

		int x = anchor.getX() - back.getWidth() / 2;
		int y = anchor.getY() - back.getHeight() - 2;
		int fillWidth = ratio <= 0 ? 0 : Math.max(1, front.getWidth() * Math.min(ratio, scale) / scale);

		drawSprite(graphics, back, x, y);
		Shape oldClip = graphics.getClip();
		graphics.clipRect(x, y, fillWidth, back.getHeight());
		drawSprite(graphics, front, x, y);
		graphics.setClip(oldClip);
	}

	private static void drawFallbackHealthBar(Graphics2D graphics, Point anchor, int ratio, int scale, int width)
	{
		int height = 5;
		int x = anchor.getX() - width / 2;
		int y = anchor.getY() - height - 2;
		int fillWidth = ratio <= 0 ? 0 : Math.max(1, width * Math.min(ratio, scale) / scale);

		graphics.setColor(new Color(0x1A1A1A));
		graphics.fillRect(x - 1, y - 1, width + 2, height + 2);
		graphics.setColor(new Color(0x7A0000));
		graphics.fillRect(x, y, width, height);
		graphics.setColor(new Color(0x00A000));
		graphics.fillRect(x, y, fillWidth, height);
	}

	private HitsplatCustomizerHitmarkDefinition getDefinition(int hitsplatType)
	{
		HitsplatCustomizerHitmarkDefinition definition = loadDefinition(hitsplatType);
		for (int depth = 0; depth < MAX_TRANSFORM_DEPTH; depth++)
		{
			int transformedType = definition.getTransformedType(client);
			if (transformedType < 0 || transformedType == hitsplatType)
			{
				return definition;
			}

			hitsplatType = transformedType;
			definition = loadDefinition(hitsplatType);
		}

		return definition;
	}

	private HitsplatCustomizerHitmarkDefinition loadDefinition(int hitsplatType)
	{
		return hitmarkDefinitions.computeIfAbsent(hitsplatType, id -> HitsplatCustomizerHitmarkDefinition.load(client, id));
	}

	private HitsplatCustomizerSprite getSprite(int spriteId)
	{
		if (spriteId < 0)
		{
			return null;
		}

		return sprites.computeIfAbsent(spriteId, id -> HitsplatCustomizerSprite.load(client, id));
	}

	private static int[] healthSpritesFor(int healthScale)
	{
		for (int[] healthSprites : STANDARD_HEALTH_SPRITES)
		{
			if (healthScale <= healthSprites[0])
			{
				return healthSprites;
			}
		}

		return STANDARD_HEALTH_SPRITES[STANDARD_HEALTH_SPRITES.length - 1];
	}

	private static void drawSprite(Graphics2D graphics, HitsplatCustomizerSprite sprite, int x, int y)
	{
		if (sprite != null)
		{
			graphics.drawImage(sprite.getImage(), x, y, null);
		}
	}

	private static int width(HitsplatCustomizerSprite sprite)
	{
		return sprite == null ? 0 : sprite.getWidth();
	}

	private static int height(HitsplatCustomizerSprite sprite)
	{
		return sprite == null ? 0 : sprite.getHeight();
	}

	private float opacityMultiplier()
	{
		double opacity = config.opacity();
		if (Double.isNaN(opacity) || Double.isInfinite(opacity))
		{
			return 1.0f;
		}

		return (float) Math.max(0.0, Math.min(1.0, opacity));
	}

	private int xOffsetFor(Actor actor)
	{
		return config.globalXOffset() + (isLargeTarget(actor) ? config.largeTargetXOffset() : 0);
	}

	private int yOffsetFor(Actor actor)
	{
		return config.globalYOffset() + (isLargeTarget(actor) ? config.largeTargetYOffset() : 0);
	}

	private boolean isLargeTarget(Actor actor)
	{
		return actor.getFootprintSize() >= config.largeTargetSize();
	}
}
