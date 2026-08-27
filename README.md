# heatmap-compose 🟩

[![CI](https://github.com/Meko123456/heatmap-compose/actions/workflows/ci.yml/badge.svg)](https://github.com/Meko123456/heatmap-compose/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.meko123456/heatmap)](https://central.sonatype.com/artifact/io.github.meko123456/heatmap)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

GitHub-style contribution heatmap for Jetpack Compose.

<p align="center">
  <img src="docs/sample.png" width="320" alt="Sample app: default greens, 26 weeks, custom orange palette, sparse data" />
</p>

## What you get

| API | For |
|---|---|
| `ContributionHeatmap` | Compose UI — a single-Canvas composable |
| `HeatmapBitmap` | Non-Compose surfaces — Glance widgets, notifications, share images |
| `HeatmapLevel` | The pure intensity math (0–4 levels), if you render yourself |
| `HeatmapLayout` | The pure grid arithmetic — hit-test taps, count drawn days, size columns to a width |

Layout mirrors github.com exactly: one column per week, Sunday-first rows,
newest week on the right. By default the busiest day is the darkest green;
pass `maxCount` for a fixed scale instead.

## Usage

```kotlin
ContributionHeatmap(
    counts = countsByDay,                     // Map<Long, Int>: epochDay -> count
    endDay = LocalDate.now().toEpochDay(),
    weeks = 20,                               // columns
    levelColors = GithubGreens,               // any 4 colors (GithubLightGreens for light surfaces)
    emptyColor = DefaultEmptyColor,
    maxCount = 4,                             // optional: absolute scale instead of "busiest day = darkest"
    onDayClick = { epochDay -> openDay(epochDay) },   // optional: makes the grid tappable
    contentDescription = "Activity: 12 of the last 140 days",   // optional: TalkBack label
)
```

Need the geometry yourself (custom overlays, tests, a date picker fallback)?

```kotlin
val weeks = HeatmapLayout.weeksThatFit(widthPx, minStepPx = 24.dp.toPx())
val day: Long? = HeatmapLayout.dayAt(x, y, widthPx, endDay, weeks)   // null off-grid / undrawn
val shown = HeatmapLayout.daysShown(endDay, weeks)
```

For a widget or notification:

```kotlin
val bitmap = HeatmapBitmap.render(counts = countsByDay, endDay = today, weeks = 26)
```

- **Zero dependencies** beyond `compose-ui` + `compose-foundation` — no material3
- `minSdk 26`

## Installation

Available on [Maven Central](https://central.sonatype.com/artifact/io.github.meko123456/heatmap):

```kotlin
implementation("io.github.meko123456:heatmap:0.2.0")
```

## Changelog

- **0.2.0** — `onDayClick` and `contentDescription` on `ContributionHeatmap`; `maxCount` for an
  absolute scale on both renderers; public `HeatmapLayout` grid math; `GithubLightGreens` palette.
  No breaking changes.
- **0.1.0** — initial release: `ContributionHeatmap`, `HeatmapBitmap`, `HeatmapLevel`.

## Sample

The [`:sample`](sample/) module demos default palette, custom colors, week
counts, and sparse data — `./gradlew :sample:installDebug`.

Extracted from [HabitStreaks](https://github.com/Meko123456/HabitStreaks),
where it renders in-app heatmaps and two home-screen widgets.

## License

[MIT](LICENSE)
