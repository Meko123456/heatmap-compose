package io.github.meko123456.heatmap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import java.time.LocalDate

/** GitHub dark-theme contribution greens, index = level 1..4. */
public val GithubGreens: List<Color> = listOf(
    Color(0xFF0E4429),
    Color(0xFF006D32),
    Color(0xFF26A641),
    Color(0xFF39D353),
)

/** GitHub light-theme contribution greens, index = level 1..4; use on light surfaces. */
public val GithubLightGreens: List<Color> = listOf(
    Color(0xFF9BE9A8),
    Color(0xFF40C463),
    Color(0xFF30A14E),
    Color(0xFF216E39),
)

/** Default color for days with no contributions; readable on light and dark. */
public val DefaultEmptyColor: Color = Color(0x1F888888)

/**
 * GitHub-style contribution heatmap drawn on a single Canvas.
 *
 * Layout mirrors github.com: one column per week, rows Sunday..Saturday,
 * newest week in the rightmost column ending at [endDay]. Cells after
 * [endDay] are not drawn. The arithmetic is public as [HeatmapLayout].
 *
 * @param counts contributions per day, keyed by [LocalDate.toEpochDay]
 * @param endDay last day to render (usually today's epoch day)
 * @param modifier standard Compose modifier; the composable fills the given
 *   width and derives its height from [weeks] to keep cells square
 * @param weeks number of week columns (must be positive)
 * @param levelColors colors for intensity levels 1..4, defaults to [GithubGreens]
 * @param emptyColor color for zero-contribution days
 * @param maxCount the count that maps to the darkest level. Defaults to the
 *   busiest visible day (relative scale, like github.com); pass a fixed value
 *   for an absolute scale that stays stable as data changes
 * @param onDayClick called with the epoch day under a tap; taps on undrawn
 *   cells after [endDay] are ignored. Null leaves the heatmap non-interactive
 * @param contentDescription accessibility description of the whole grid, e.g.
 *   "Contributions: 12 of the last 140 days active". Null adds no semantics
 */
@Composable
public fun ContributionHeatmap(
    counts: Map<Long, Int>,
    endDay: Long,
    modifier: Modifier = Modifier,
    weeks: Int = 20,
    levelColors: List<Color> = GithubGreens,
    emptyColor: Color = DefaultEmptyColor,
    maxCount: Int? = null,
    onDayClick: ((Long) -> Unit)? = null,
    contentDescription: String? = null,
) {
    require(weeks > 0) { "weeks must be positive" }
    require(levelColors.size == HeatmapLevel.MAX_LEVEL) {
        "levelColors must have exactly ${HeatmapLevel.MAX_LEVEL} entries"
    }
    val scaleMax = maxCount ?: (counts.values.maxOrNull() ?: 0)
    val firstDay = HeatmapLayout.firstDay(endDay, weeks)
    val currentOnDayClick by rememberUpdatedState(onDayClick)

    var canvasModifier = modifier.fillMaxWidth().aspectRatio(weeks / 7f)
    if (contentDescription != null) {
        canvasModifier = canvasModifier.semantics { this.contentDescription = contentDescription }
    }
    if (onDayClick != null) {
        canvasModifier = canvasModifier.pointerInput(weeks, endDay) {
            detectTapGestures { offset ->
                HeatmapLayout.dayAt(offset.x, offset.y, size.width.toFloat(), endDay, weeks)
                    ?.let { day -> currentOnDayClick?.invoke(day) }
            }
        }
    }

    Canvas(modifier = canvasModifier) {
        val step = size.width / weeks
        val cell = step * HeatmapLayout.CELL_FRACTION
        val corner = CornerRadius(cell * 0.18f)
        for (col in 0 until weeks) {
            for (row in 0 until HeatmapLayout.ROWS) {
                val day = firstDay + col * 7L + row
                if (day > endDay) continue
                val level = HeatmapLevel.levelFor(counts[day] ?: 0, scaleMax)
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
