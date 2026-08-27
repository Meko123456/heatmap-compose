package io.github.meko123456.heatmap

import java.time.LocalDate

/**
 * The grid arithmetic shared by [ContributionHeatmap] and [HeatmapBitmap], exposed so consumers
 * can hit-test taps, build accessibility descriptions, or lay out their own decorations without
 * re-implementing the layout.
 *
 * One column per week (oldest on the left), seven rows Sunday (0) to Saturday (6). Each cell owns
 * a square "step" box; the painted square inside it is smaller, but hit-testing uses the whole
 * step so a finger landing in the gutter still resolves to the nearest day. Days are
 * [LocalDate.toEpochDay] values; cells after the end day are not drawn and never hit.
 */
public object HeatmapLayout {

    /** Rows in the grid: one per day of the week. */
    public const val ROWS: Int = 7

    /** Fraction of the step box that is painted; the rest is gutter. */
    public const val CELL_FRACTION: Float = 0.82f

    /** Row of [day]: Sunday = 0, Monday = 1 … Saturday = 6. */
    public fun rowOf(day: Long): Int = LocalDate.ofEpochDay(day).dayOfWeek.value % ROWS

    /** Epoch day drawn in the top-left cell (column 0, row 0) of a grid ending at [endDay]. */
    public fun firstDay(endDay: Long, weeks: Int): Long {
        require(weeks > 0) { "weeks must be positive" }
        return endDay - rowOf(endDay) - (weeks - 1) * 7L
    }

    /** Epoch day at ([col], [row]), or null when that cell is after [endDay] and therefore undrawn. */
    public fun dayAt(col: Int, row: Int, endDay: Long, weeks: Int): Long? {
        require(col in 0 until weeks && row in 0 until ROWS) { "cell ($col, $row) is outside the grid" }
        val day = firstDay(endDay, weeks) + col * 7L + row
        return if (day > endDay) null else day
    }

    /**
     * Epoch day whose step box contains the point ([x], [y]) in a grid [width] px wide, or null
     * when the point is outside `[0, width) × [0, height)` or lands on an undrawn cell. The grid's
     * height is `width * 7 / weeks`, matching the composable's aspect ratio.
     */
    public fun dayAt(x: Float, y: Float, width: Float, endDay: Long, weeks: Int): Long? {
        if (weeks < 1 || width <= 0f) return null
        val height = width * ROWS / weeks
        if (x < 0f || y < 0f || x >= width || y >= height) return null
        val step = width / weeks
        // coerceIn guards against float rounding pushing a far-edge point one past the grid.
        val col = (x / step).toInt().coerceIn(0, weeks - 1)
        val row = (y / step).toInt().coerceIn(0, ROWS - 1)
        return dayAt(col, row, endDay, weeks)
    }

    /** Number of days drawn in a grid ending at [endDay]: full weeks plus the end week so far. */
    public fun daysShown(endDay: Long, weeks: Int): Int = (endDay - firstDay(endDay, weeks) + 1).toInt()

    /** How many of [days] fall inside the drawn range of a grid ending at [endDay]. */
    public fun daysWithin(days: Collection<Long>, endDay: Long, weeks: Int): Int {
        val first = firstDay(endDay, weeks)
        return days.count { it in first..endDay }
    }

    /** Week columns that fit in [widthPx] when each step must be at least [minStepPx]; never below 1. */
    public fun weeksThatFit(widthPx: Float, minStepPx: Float): Int {
        if (minStepPx <= 0f || widthPx.isNaN() || minStepPx.isNaN()) return 1
        return (widthPx / minStepPx).toInt().coerceAtLeast(1)
    }
}
