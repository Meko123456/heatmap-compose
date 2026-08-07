package io.github.meko123456.heatmap

import kotlin.math.ceil

/**
 * Buckets a day's contribution count into a GitHub-style intensity level:
 * 0 = empty, 1..4 = progressively darker, where the busiest day maps to 4.
 */
public object HeatmapLevel {

    public const val MAX_LEVEL: Int = 4

    /**
     * @param count the day's contribution count
     * @param maxCount the highest count across the rendered range
     * @return 0 when [count] is zero/negative or [maxCount] is not positive;
     *   [MAX_LEVEL] when [count] >= [maxCount]; otherwise 1..3 proportionally.
     */
    public fun levelFor(count: Int, maxCount: Int): Int {
        if (count <= 0 || maxCount <= 0) return 0
        if (count >= maxCount) return MAX_LEVEL
        return ceil(count * (MAX_LEVEL - 1f) / maxCount).toInt().coerceIn(1, MAX_LEVEL - 1)
    }
}
