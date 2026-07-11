package com.customizealot;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Renderable;
import net.runelite.client.callback.RenderCallback;
import net.runelite.client.callback.RenderCallbackManager;

/**
 * Records actors whose native 2D pass was accepted by every earlier render
 * listener, then suppresses that entire pass. Registering this callback last
 * lets replacement UI respect actor hiders without reproducing their policy.
 */
@Singleton
final class CustomizeALotActorUiVisibilityTracker
{
	private final Client client;
	private final RenderCallbackManager renderCallbackManager;
	private final Map<Actor, Integer> acceptedOnGameCycle = new ConcurrentHashMap<>();
	private final RenderCallback observer = new RenderCallback()
	{
		@Override
		public boolean addEntity(Renderable renderable, boolean ui)
		{
			if (shouldSuppressActorUi(enabled, ui, renderable instanceof Actor))
			{
				acceptedOnGameCycle.put((Actor) renderable, currentGameCycleValue());
				return false;
			}

			return true;
		}
	};

	private volatile boolean enabled;
	private volatile int cachedGameCycle = Integer.MIN_VALUE;
	private volatile Integer cachedGameCycleValue = Integer.MIN_VALUE;

	@Inject
	CustomizeALotActorUiVisibilityTracker(
		Client client,
		RenderCallbackManager renderCallbackManager)
	{
		this.client = client;
		this.renderCallbackManager = renderCallbackManager;
	}

	void enable()
	{
		if (!enabled)
		{
			renderCallbackManager.register(observer);
			enabled = true;
		}
	}

	void moveToEnd()
	{
		if (!enabled)
		{
			return;
		}

		renderCallbackManager.unregister(observer);
		renderCallbackManager.register(observer);
	}

	void disable()
	{
		if (enabled)
		{
			enabled = false;
			renderCallbackManager.unregister(observer);
		}
		clear();
	}

	void copyAcceptedInto(Collection<Actor> destination, int gameCycle)
	{
		if (destination == null)
		{
			return;
		}

		for (Map.Entry<Actor, Integer> entry : acceptedOnGameCycle.entrySet())
		{
			if (acceptedOn(entry.getValue(), gameCycle))
			{
				destination.add(entry.getKey());
			}
		}
	}

	void remove(Actor actor)
	{
		if (actor != null)
		{
			acceptedOnGameCycle.remove(actor);
		}
	}

	void discardBefore(int oldestGameCycle)
	{
		acceptedOnGameCycle.entrySet().removeIf(entry -> entry.getValue() < oldestGameCycle);
	}

	void clear()
	{
		acceptedOnGameCycle.clear();
		cachedGameCycleValue = Integer.MIN_VALUE;
		cachedGameCycle = Integer.MIN_VALUE;
	}

	private Integer currentGameCycleValue()
	{
		int gameCycle = client.getGameCycle();
		if (cachedGameCycle == gameCycle)
		{
			return cachedGameCycleValue;
		}

		synchronized (this)
		{
			if (cachedGameCycle != gameCycle)
			{
				cachedGameCycleValue = gameCycle;
				cachedGameCycle = gameCycle;
			}
			return cachedGameCycleValue;
		}
	}

	static boolean acceptedOn(int observedGameCycle, int gameCycle)
	{
		return observedGameCycle == gameCycle;
	}

	static boolean shouldSuppressActorUi(boolean enabled, boolean ui, boolean actor)
	{
		return enabled && ui && actor;
	}
}
