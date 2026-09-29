package shibbir.me.alquranquotes.core.time

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.TimeZone

class EpochDayTest {

    private val utc = TimeZone.getTimeZone("UTC")
    private val dhaka = TimeZone.getTimeZone("GMT+06:00")

    @Test
    fun startOfEpochIsDayZero() {
        assertEquals(0L, epochDayOf(0L, utc))
    }

    @Test
    fun lastMillisecondOfDayStaysOnSameDay() {
        assertEquals(0L, epochDayOf(MILLIS_PER_DAY - 1, utc))
    }

    @Test
    fun midnightStartsNextDay() {
        assertEquals(1L, epochDayOf(MILLIS_PER_DAY, utc))
    }

    @Test
    fun timeZoneOffsetMovesToNextLocalDay() {
        val eighteenHoursUtc = 18 * 60 * 60 * 1000L

        assertEquals(0L, epochDayOf(eighteenHoursUtc, utc))
        assertEquals(1L, epochDayOf(eighteenHoursUtc, dhaka))
    }

    @Test
    fun timeBeforeEpochIsNegativeDay() {
        assertEquals(-1L, epochDayOf(-1L, utc))
    }

    private companion object {
        const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    }
}
