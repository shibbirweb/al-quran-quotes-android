package shibbir.me.alquranquotes.core.time

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.TimeZone

class EpochDayTest {

    private val utcTimeZone = TimeZone.getTimeZone("UTC")
    private val sixHoursAheadOfUtcTimeZone = TimeZone.getTimeZone("GMT+06:00")
    private val fiveHoursBehindUtcTimeZone = TimeZone.getTimeZone("GMT-05:00")
    private val newYorkTimeZone = TimeZone.getTimeZone("America/New_York")

    @Test
    fun startOfEpochIsDayZero() {
        assertEquals(0L, epochDayOf(0L, utcTimeZone))
    }

    @Test
    fun lastMillisecondOfDayStaysOnSameDay() {
        assertEquals(0L, epochDayOf(MILLIS_PER_DAY - 1, utcTimeZone))
    }

    @Test
    fun midnightStartsNextDay() {
        assertEquals(1L, epochDayOf(MILLIS_PER_DAY, utcTimeZone))
    }

    @Test
    fun positiveOffsetMovesToNextLocalDay() {
        val eighteenHoursUtc = 18 * MILLIS_PER_HOUR

        assertEquals(0L, epochDayOf(eighteenHoursUtc, utcTimeZone))
        assertEquals(1L, epochDayOf(eighteenHoursUtc, sixHoursAheadOfUtcTimeZone))
    }

    @Test
    fun negativeOffsetStaysOnPreviousLocalDay() {
        val oneAmUtcOnDayOne = MILLIS_PER_DAY + MILLIS_PER_HOUR

        assertEquals(1L, epochDayOf(oneAmUtcOnDayOne, utcTimeZone))
        assertEquals(0L, epochDayOf(oneAmUtcOnDayOne, fiveHoursBehindUtcTimeZone))
    }

    /**
     * US daylight saving started on 2024-03-10 (epoch day 19792) at 07:00 UTC, when 02:00 EST
     * (UTC-5) became 03:00 EDT (UTC-4).
     *
     * - 2024-03-10 04:30 UTC is before the change: 04:30 - 5h = 2024-03-09 23:30 EST, day 19791.
     * - 2024-03-11 04:30 UTC is after the change: 04:30 - 4h = 2024-03-11 00:30 EDT, day 19793.
     *
     * The two instants are one UTC day apart but two local days apart. A fixed UTC-5 offset would
     * give 19792 for the second one, so this only passes when the daylight saving offset is used.
     */
    @Test
    fun daylightSavingOffsetIsUsedAfterTheClocksChange() {
        val fourThirtyAmMillis = 4 * MILLIS_PER_HOUR + 30 * MILLIS_PER_MINUTE
        val beforeChangeMillis = DST_START_EPOCH_DAY * MILLIS_PER_DAY + fourThirtyAmMillis
        val afterChangeMillis = beforeChangeMillis + MILLIS_PER_DAY

        assertEquals(DST_START_EPOCH_DAY - 1, epochDayOf(beforeChangeMillis, newYorkTimeZone))
        assertEquals(DST_START_EPOCH_DAY + 1, epochDayOf(afterChangeMillis, newYorkTimeZone))
    }

    @Test
    fun timeBeforeEpochIsNegativeDay() {
        assertEquals(-1L, epochDayOf(-1L, utcTimeZone))
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60 * 1000L
        const val MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE

        /** 2024-03-10, the day US daylight saving started in 2024. */
        const val DST_START_EPOCH_DAY = 19_792L
    }
}
