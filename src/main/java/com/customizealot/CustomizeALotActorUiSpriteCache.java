package com.customizealot;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.SpritePixels;

@Singleton
final class CustomizeALotActorUiSpriteCache
{
	private static final int CACHE_FAILURE_RETRY_CYCLES = 10;

	private final IntSupplier gameCycleSupplier;
	private final IntFunction<SpriteSource[]> groupLoader;
	private final Map<Integer, SpriteGroup> groups = new HashMap<>();

	@Inject
	CustomizeALotActorUiSpriteCache(Client client)
	{
		this(
			() -> safeGameCycle(client),
			archiveId -> loadGroup(client, archiveId));
	}

	CustomizeALotActorUiSpriteCache(
		IntSupplier gameCycleSupplier,
		IntFunction<SpriteSource[]> groupLoader)
	{
		this.gameCycleSupplier = gameCycleSupplier;
		this.groupLoader = groupLoader;
	}

	CustomizeALotSprite get(int archiveId, int spriteIndex)
	{
		if (archiveId < 0 || spriteIndex < 0)
		{
			return null;
		}

		int gameCycle = gameCycleSupplier.getAsInt();
		SpriteGroup group = groups.get(archiveId);
		if (group == null)
		{
			group = new SpriteGroup(groupLoader.apply(archiveId), retryOnGameCycle(gameCycle));
			groups.put(archiveId, group);
		}
		else if (group.shouldRetryGroup(gameCycle))
		{
			group.replaceSources(groupLoader.apply(archiveId), retryOnGameCycle(gameCycle));
		}

		return group.get(
			spriteIndex,
			gameCycle,
			() -> groupLoader.apply(archiveId));
	}

	void clear()
	{
		groups.clear();
	}

	private static SpriteSource[] loadGroup(Client client, int archiveId)
	{
		try
		{
			SpritePixels[] pixels = client.getSprites(client.getIndexSprites(), archiveId, 0);
			if (pixels == null || pixels.length == 0)
			{
				return null;
			}

			SpriteSource[] sources = new SpriteSource[pixels.length];
			for (int i = 0; i < pixels.length; i++)
			{
				if (pixels[i] != null)
				{
					sources[i] = new SpriteSource(pixels[i]);
				}
			}
			return sources;
		}
		catch (RuntimeException ex)
		{
			return null;
		}
	}

	private static int retryOnGameCycle(int gameCycle)
	{
		return gameCycle + CACHE_FAILURE_RETRY_CYCLES;
	}

	private static int safeGameCycle(Client client)
	{
		try
		{
			return client.getGameCycle();
		}
		catch (RuntimeException ex)
		{
			return 0;
		}
	}

	static final class SpriteSource
	{
		private final Supplier<CustomizeALotSprite> loader;

		SpriteSource(SpritePixels spritePixels)
		{
			this(() -> convert(spritePixels));
		}

		SpriteSource(Supplier<CustomizeALotSprite> loader)
		{
			this.loader = loader;
		}

		CustomizeALotSprite load()
		{
			try
			{
				return loader.get();
			}
			catch (RuntimeException ex)
			{
				return null;
			}
		}

		private static CustomizeALotSprite convert(SpritePixels spritePixels)
		{
			BufferedImage image = spritePixels.toBufferedImage();
			return image == null
				? null
				: new CustomizeALotSprite(
					image,
					spritePixels.getMaxWidth(),
					spritePixels.getMaxHeight(),
					spritePixels.getOffsetX(),
					spritePixels.getOffsetY());
		}
	}

	private static final class SpriteGroup
	{
		private final Map<Integer, CustomizeALotSprite> convertedSprites = new HashMap<>();
		private final Map<Integer, Integer> failedSpriteRetries = new HashMap<>();
		private SpriteSource[] sources;
		private int retryGroupOnGameCycle;

		private SpriteGroup(SpriteSource[] sources, int retryGroupOnGameCycle)
		{
			this.sources = sources;
			this.retryGroupOnGameCycle = retryGroupOnGameCycle;
		}

		private boolean shouldRetryGroup(int gameCycle)
		{
			return sources == null && gameCycle >= retryGroupOnGameCycle;
		}

		private void replaceSources(SpriteSource[] replacement, int nextRetryGameCycle)
		{
			sources = replacement;
			retryGroupOnGameCycle = nextRetryGameCycle;
			if (replacement == null)
			{
				failedSpriteRetries.replaceAll((index, ignored) -> nextRetryGameCycle);
			}
			else
			{
				failedSpriteRetries.clear();
				releaseConvertedSources();
			}
		}

		private CustomizeALotSprite get(
			int spriteIndex,
			int gameCycle,
			Supplier<SpriteSource[]> groupReloader)
		{
			CustomizeALotSprite converted = convertedSprites.get(spriteIndex);
			if (converted != null)
			{
				return converted;
			}

			Integer retryOnGameCycle = failedSpriteRetries.get(spriteIndex);
			if (retryOnGameCycle != null)
			{
				if (gameCycle < retryOnGameCycle)
				{
					return null;
				}

				SpriteSource[] replacement = groupReloader.get();
				if (replacement != null)
				{
					sources = replacement;
					failedSpriteRetries.clear();
					releaseConvertedSources();
				}
				else
				{
					failedSpriteRetries.replaceAll(
						(index, ignored) -> retryOnGameCycle(gameCycle));
				}
			}

			converted = convert(spriteIndex);
			if (converted == null)
			{
				failedSpriteRetries.put(spriteIndex, retryOnGameCycle(gameCycle));
				return null;
			}

			failedSpriteRetries.remove(spriteIndex);
			// The converted image is now authoritative; release the cache-backed pixels.
			sources[spriteIndex] = null;
			convertedSprites.put(spriteIndex, converted);
			return converted;
		}

		private void releaseConvertedSources()
		{
			if (sources == null)
			{
				return;
			}

			for (Integer spriteIndex : convertedSprites.keySet())
			{
				if (spriteIndex >= 0 && spriteIndex < sources.length)
				{
					sources[spriteIndex] = null;
				}
			}
		}

		private CustomizeALotSprite convert(int spriteIndex)
		{
			if (sources == null || spriteIndex >= sources.length)
			{
				return null;
			}

			SpriteSource source = sources[spriteIndex];
			if (source == null)
			{
				return null;
			}
			return source.load();
		}
	}
}
