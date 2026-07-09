package com.hitsplatcustomizer;

import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.api.SpritePixels;

final class HitsplatCustomizerSprite
{
	private final BufferedImage image;
	private final int width;
	private final int height;
	private final int offsetX;

	private HitsplatCustomizerSprite(BufferedImage image, int width, int height, int offsetX)
	{
		this.image = image;
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
	}

	static HitsplatCustomizerSprite load(Client client, int spriteId)
	{
		if (spriteId < 0)
		{
			return null;
		}

		SpritePixels[] sprites = client.getSprites(client.getIndexSprites(), spriteId, 0);
		if (sprites == null || sprites.length == 0 || sprites[0] == null)
		{
			return null;
		}

		SpritePixels sprite = sprites[0];
		return new HitsplatCustomizerSprite(
			sprite.toBufferedImage(),
			sprite.getWidth(),
			sprite.getHeight(),
			sprite.getOffsetX());
	}

	BufferedImage getImage()
	{
		return image;
	}

	int getWidth()
	{
		return width;
	}

	int getHeight()
	{
		return height;
	}

	int getOffsetX()
	{
		return offsetX;
	}
}
