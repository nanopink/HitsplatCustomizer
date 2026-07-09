# Hitsplat Customizer

Take control of hitsplats. Show more damage and healing splats with configurable layouts, filters, spacing, and timing.

Hitsplat Customizer suppresses the native actor hitsplat UI while it has active splats for that actor, then redraws the hitsplats itself using RuneScape's hitmark sprite definitions, text templates, and text colors. Each splat keeps its assigned slot until it expires.

Settings include:

- Presets, including `Ruined Heir's 1 tick`, `Chaos`, `RuneScape`, and `Hexagon 2 ticks`. Editing a preset-controlled setting marks the preset as custom.
- General settings are grouped separately and are not changed by presets.
- Category-wide disable toggles for NPC targets, other player targets, and your own player.
- Maximum visible hitsplats per actor, with `0` meaning no cap.
- Overall opacity, with `1.0` meaning fully opaque.
- Showing only hitsplats caused by you.
- Prioritizing your hitsplats when visible splats are limited.
- Optional hiding for zero-value hitsplats.
- Hexagonal, diamond, grid, or X shapes, with clockwise or counterclockwise direction.
- Incremental, symmetrical, or random placement behavior. Random chooses an open slot from the current radius layer.
- Min and max radius controls for choosing where the layout starts and how far it expands. Min radius `1` skips the center slot; max radius `0` means no radius cap, `1` means the center slot only, and `2` means the center plus the first ring.
- X and Y spacing controls for spreading adjacent hitsplats farther apart or pulling them closer together in whole-pixel increments, including negative values.
- Global X and Y offset controls for moving every hitsplat relative to the actor anchor, plus large-target offsets gated by actor footprint size. Positive Y offset moves hitsplats upward.
- Fade-in, full opacity, and fade-out durations. Total lifetime is their sum; 560 ms is the conservative one-tick target, giving roughly 40 ms of margin against a typical 600 ms server tick.
