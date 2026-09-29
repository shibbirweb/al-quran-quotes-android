package shibbir.me.alquranquotes.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test

class DailyAyahIndexTest {

    @Test
    fun sameDayAlwaysGivesSameIndex() {
        assertEquals(dailyAyahIndex(20_000L, 33), dailyAyahIndex(20_000L, 33))
    }

    @Test
    fun consecutiveDaysGiveConsecutiveIndexes() {
        assertEquals(0, dailyAyahIndex(33L, 33))
        assertEquals(1, dailyAyahIndex(34L, 33))
    }

    @Test
    fun indexWrapsAroundAyahCount() {
        assertEquals(32, dailyAyahIndex(32L, 33))
        assertEquals(0, dailyAyahIndex(66L, 33))
    }

    @Test
    fun negativeEpochDayGivesValidIndex() {
        assertEquals(32, dailyAyahIndex(-1L, 33))
    }

    @Test(expected = IllegalArgumentException::class)
    fun zeroAyahCountIsRejected() {
        dailyAyahIndex(1L, 0)
    }
}
