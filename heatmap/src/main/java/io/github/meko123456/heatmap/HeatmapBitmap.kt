package io.github.meko123456.heatmap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Renders the contribution heatmap into a [Bitmap] for surfaces that cannot
 * host Compose — Glance widgets, notifications, share images. Same layout
 * rules as [ContributionHeatmap]: week columns, Sunday-first rows, newest
 * column on the right.
 */
public object HeatmapBitmap {

    /**
     * @param counts contributions per day, keyed by [LocalDate.toEpochDay]
     * @param endDay last day to render (usually today's epoch day)
     * @param weeks number of week columns (must be positive)
     * @param cellPx cell edge in pixels (must be positive)
     * @param gapPx gap between cells in pixels
     * @param levelColors ARGB colors for levels 1..4, defaults to GitHub greens
     * @param emptyColor ARGB color for zero-contribution days
     */
    public fun render(
        counts: Map<Long, Int>,
        endDay: Long,
        weeks: Int = 26,
        cellPx: Int = 24,
        gapPx: Int = 6,
        levelColors: IntArray = intArrayOf(
            0xFF0E4429.toInt(),
            0xFF006D32.toInt(),
            0xFF26A641.toInt(),
            0xFF39D353.toInt(),
        ),
        emptyColor: Int = 0x40808080,
    ): Bitmap {
        require(weeks > 0 && cellPx > 0) { "weeks and cellPx must be positive" }
        require(levelColors.size == HeatmapLevel.MAX_LEVEL) {
            "levelColors must have exactly ${HeatmapLevel.MAX_LEVEL} entries"
        }
        val step = cellPx + gapPx
        val bitmap = Bitmap.createBitmap(
            weeks * step - gapPx,
            7 * step - gapPx,
            Bitmap.Config.ARGB_8888,
        )
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val corner = cellPx * 0.18f
        val maxCount = counts.values.maxOrNull() ?: 0

        val endRow = LocalDate.ofEpochDay(endDay).dayOfWeek.let { dow ->
            if (dow == DayOfWeek.SUNDAY) 0 else dow.value
        }
        val firstDay = endDay - endRow - (weeks - 1) * 7L

        val rect = RectF()
        for (col in 0 until weeks) {
            for (row in 0 until 7) {
                val day = firstDay + col * 7L + row
                if (day > endDay) continue
                val level = HeatmapLevel.levelFor(counts[day] ?: 0, maxCount)
                paint.color = if (level == 0) emptyColor else levelColors[level - 1]
                val left = (col * step).toFloat()
                val top = (row * step).toFloat()
                rect.set(left, top, left + cellPx, top + cellPx)
                canvas.drawRoundRect(rect, corner, corner, paint)
            }
        }
        return bitmap
    }
}
