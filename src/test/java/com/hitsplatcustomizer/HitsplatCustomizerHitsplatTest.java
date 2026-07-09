package com.hitsplatcustomizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HitsplatCustomizerHitsplatTest
{
	@Test
	public void fadesInThenHoldsThenFadesOut()
	{
		HitsplatCustomizerHitsplat hitsplat = new HitsplatCustomizerHitsplat(0, 10, 0, 100, 120, 160, 180, 0, true);

		assertEquals(0.0f, hitsplat.getAlpha(100), 0.001f);
		assertEquals(0.5f, hitsplat.getAlpha(110), 0.001f);
		assertEquals(1.0f, hitsplat.getAlpha(120), 0.001f);
		assertEquals(1.0f, hitsplat.getAlpha(150), 0.001f);
		assertEquals(0.5f, hitsplat.getAlpha(170), 0.001f);
		assertEquals(0.0f, hitsplat.getAlpha(180), 0.001f);
	}
}
