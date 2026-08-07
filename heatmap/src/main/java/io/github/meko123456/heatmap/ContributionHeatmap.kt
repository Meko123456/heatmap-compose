package io.github.meko123456.heatmap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import java.time.DayOfWeek
import java.time.LocalDate

/** GitHub dark-theme contribution greens, index = level 1..4. */
public val GithubGreens: List<Color> = listOf(
    Color(0xFF0E4429),
    Color(0xFF006D32),
    Color(0xFF26A641),
    Color(0xFF39D353),
)

/** Default color for days with no contributions; readable on light and dark. */
public val DefaultEmptyColor: Color = Color(0x1F888888)

/**
 * GitHub-style contribution heatmap drawn on a single Canvas.
 *
 * Layout mirrors github.com: one column per week, rows Sunday..Saturday,
 * newest week in the rightmost column ending at [endDay]. Cells after
 * [endDay] are not drawn.
 *
 * @param counts contributions per day, keyed by [LocalDate.toEpochDay]
 * @param endDay last day to render (usually today's epoch day)
 * @param modifier standard Compose modifier; the composable fills the given
 *   width and derives its height from [weeks] to keep cells square
 * @param weeks number of week columns (must be positive)
 * @param levelColors colors for intensity levels 1..4, defaults to [GithubGreens]
 * @param emptyColor color for zero-contribution days
 */
@Composable
public fun ContributionHeatmap(
    counts: Map<Long, Int>,
    endDay: Long,
    modifier: Modifier = Modifier,
    weeks: Int = 20,
    levelColors: List<Color> = GithubGreens,
    emptyColor: Color = DefaultEmptyColor,
) {
    require(weeks > 0) { "weeks must be positive" }
    require(levelColors.size == HeatmapLevel.MAX_LEVEL) {
        "levelColors must have exactly ${HeatmapLevel.MAX_LEVEL} entries"
    }
    val maxCount = counts.values.maxOrNull() ?: 0
    val endRow = LocalDate.ofEpochDay(endDay).dayOfWeek.let { dow ->
        if (dow == DayOfWeek.SUNDAY) 0 else dow.value
    }
    val firstDay = endDay - endRow - (weeks - 1) * 7L

    Canvas(modifier = modifier.fillMaxWidth().aspectRatio(weeks / 7f)) {
        val step = size.width / weeks
        val cell = step * 0.82f
        val corner = CornerRadius(cell * 0.18f)
        for (col in 0 until weeks) {
            for (row in 0 until 7) {
                val day = firstDay + col * 7L + row
                if (day > endDay) continue
                val level = HeatmapLevel.levelFor(counts[day] ?: 0, maxCount)
                drawRoundRect(
                    color = if (level == 0) emptyColor else levelColors[level - 1],
                    topLeft = Offset(col * step, row * step),
                    size = Size(cell, cell),
                    cornerRadius = corner,
                )
            }
        }
    }
}
