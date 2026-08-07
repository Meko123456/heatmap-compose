# heatmap-compose 🟩

GitHub-style contribution heatmap for Jetpack Compose.

A single dependency that gives you the familiar green-squares calendar:

- **`ContributionHeatmap`** — a Compose Canvas composable: one column per week,
  Sunday-first rows, newest week on the right, exactly like github.com
- **`HeatmapBitmap`** — the same grid rendered to an `android.graphics.Bitmap`,
  for surfaces that can't host Compose: Glance widgets, notifications, shares
- **`HeatmapLevel`** — the pure intensity-bucketing math (0–4 levels, busiest
  day always darkest), fully unit-tested

Extracted from [HabitStreaks](https://github.com/Meko123456/HabitStreaks),
where it renders both in-app heatmaps and two home-screen widgets.

## Usage

```kotlin
ContributionHeatmap(
    counts = mapOf(LocalDate.now().toEpochDay() to 5),  // epochDay -> count
    endDay = LocalDate.now().toEpochDay(),
    weeks = 20,
)
```

Colors, week count, and empty-cell color are parameters; defaults match
GitHub's dark-theme palette.

## Status

🚧 Extraction in progress — see [issues](../../issues) for the roadmap to
Maven Central.

## License

[MIT](LICENSE)
