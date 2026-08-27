package io.github.meko123456.heatmap

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HeatmapLayoutTest {

    // Thursday → row 4.
    private val thursday = LocalDate.of(2026, 8, 27)
    private val endDay = thursday.toEpochDay()
    private val weeks = 20
    private val width = 400f
    private val step = width / weeks

    @Test
    fun `rows are Sunday-first`() {
        assertEquals(0, HeatmapLayout.rowOf(LocalDate.of(2026, 8, 30).toEpochDay()))
        assertEquals(1, HeatmapLayout.rowOf(LocalDate.of(2026, 8, 31).toEpochDay()))
        assertEquals(4, HeatmapLayout.rowOf(endDay))
        assertEquals(6, HeatmapLayout.rowOf(LocalDate.of(2026, 8, 29).toEpochDay()))
    }

    @Test
    fun `firstDay is the Sunday that starts the oldest column`() {
        assertEquals(thursday.minusDays(4).toEpochDay(), HeatmapLayout.firstDay(endDay, weeks = 1))
        assertEquals(thursday.minusDays(4).minusWeeks(19).toEpochDay(), HeatmapLayout.firstDay(endDay, weeks))
    }

    @Test
    fun `cell coordinates map to days and undrawn cells are null`() {
        assertEquals(HeatmapLayout.firstDay(endDay, weeks), HeatmapLayout.dayAt(0, 0, endDay, weeks))
        assertEquals(endDay, HeatmapLayout.dayAt(weeks - 1, 4, endDay, weeks))
        assertNull(HeatmapLayout.dayAt(weeks - 1, 5, endDay, weeks))
        assertNull(HeatmapLayout.dayAt(weeks - 1, 6, endDay, weeks))
    }

    @Test
    fun `tap in the bottom-right drawn cell returns endDay`() {
        val x = (weeks - 1) * step + step / 2
        val y = 4 * step + step / 2
        assertEquals(endDay, HeatmapLayout.dayAt(x, y, width, endDay, weeks))
    }

    @Test
    fun `tap in the top-left cell returns firstDay`() {
        assertEquals(HeatmapLayout.firstDay(endDay, weeks), HeatmapLayout.dayAt(1f, 1f, width, endDay, weeks))
    }

    @Test
    fun `tap on a cell after endDay is null`() {
        val x = (weeks - 1) * step + step / 2
        assertNull(HeatmapLayout.dayAt(x, 5 * step + 1f, width, endDay, weeks))
    }

    @Test
    fun `taps outside the grid are null`() {
        val height = width * 7 / weeks
        assertNull(HeatmapLayout.dayAt(-1f, 1f, width, endDay, weeks))
        assertNull(HeatmapLayout.dayAt(1f, -1f, width, endDay, weeks))
        assertNull(HeatmapLayout.dayAt(width, 1f, width, endDay, weeks))
        assertNull(HeatmapLayout.dayAt(1f, height, width, endDay, weeks))
        assertNull(HeatmapLayout.dayAt(1f, 1f, 0f, endDay, weeks))
    }

    @Test
    fun `a point on a cell's top-left edge belongs to that cell`() {
        val first = HeatmapLayout.firstDay(endDay, weeks)
        assertEquals(first + 7 + 1, HeatmapLayout.dayAt(step, step, width, endDay, weeks))
        assertEquals(first, HeatmapLayout.dayAt(step - 0.01f, step - 0.01f, width, endDay, weeks))
    }

    @Test
    fun `every drawn cell round-trips through pixel hit-testing`() {
        for (col in 0 until weeks) for (row in 0 until 7) {
            val expected = HeatmapLayout.dayAt(col, row, endDay, weeks)
            val actual = HeatmapLayout.dayAt(col * step + step / 2, row * step + step / 2, width, endDay, weeks)
            assertEquals("cell ($col, $row)", expected, actual)
        }
    }

    @Test
    fun `Sunday endDay sits in row 0 and shows a single day in its column`() {
        val sunday = LocalDate.of(2026, 8, 30).toEpochDay()
        assertEquals(sunday, HeatmapLayout.dayAt(0, 0, sunday, weeks = 1))
        assertEquals(1, HeatmapLayout.daysShown(sunday, weeks = 1))
        assertEquals(7 * 3 + 1, HeatmapLayout.daysShown(sunday, weeks = 4))
    }

    @Test
    fun `daysShown counts full weeks plus the end week so far`() {
        assertEquals(19 * 7 + 5, HeatmapLayout.daysShown(endDay, weeks))
        assertEquals(5, HeatmapLayout.daysShown(endDay, weeks = 1))
    }

    @Test
    fun `daysWithin only counts days inside the drawn range`() {
        val first = HeatmapLayout.firstDay(endDay, weeks)
        assertEquals(3, HeatmapLayout.daysWithin(setOf(first - 1, first, endDay - 3, endDay, endDay + 1), endDay, weeks))
        assertEquals(0, HeatmapLayout.daysWithin(emptySet(), endDay, weeks))
    }

    @Test
    fun `weeksThatFit floors and never drops below one`() {
        assertEquals(10, HeatmapLayout.weeksThatFit(250f, 24f))
        assertEquals(1, HeatmapLayout.weeksThatFit(10f, 24f))
        assertEquals(1, HeatmapLayout.weeksThatFit(100f, 0f))
    }
}
