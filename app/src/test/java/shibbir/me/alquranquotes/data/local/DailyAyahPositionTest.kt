package shibbir.me.alquranquotes.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class DailyAyahPositionTest {

    private val ayahCount = 33

    /** 20000 mod 33 = 20000 - 33 * 606 = 20000 - 19998 = 2. */
    @Test
    fun dayTwentyThousandGivesPositionTwo() {
        assertEquals(2, dailyAyahPosition(20_000L, ayahCount))
    }

    @Test
    fun consecutiveDaysGiveConsecutivePositions() {
        val firstDayOfSecondCycle = ayahCount.toLong()

        assertEquals(0, dailyAyahPosition(firstDayOfSecondCycle, ayahCount))
        assertEquals(1, dailyAyahPosition(firstDayOfSecondCycle + 1, ayahCount))
    }

    @Test
    fun positionWrapsAroundAyahCount() {
        val lastPosition = ayahCount - 1
        val firstDayOfThirdCycle = 2L * ayahCount

        assertEquals(lastPosition, dailyAyahPosition(lastPosition.toLong(), ayahCount))
        assertEquals(0, dailyAyahPosition(firstDayOfThirdCycle, ayahCount))
    }

    @Test
    fun negativeEpochDayGivesValidPosition() {
        val lastPosition = ayahCount - 1

        assertEquals(lastPosition, dailyAyahPosition(-1L, ayahCount))
    }

    @Test(expected = IllegalArgumentException::class)
    fun zeroAyahCountIsRejected() {
        dailyAyahPosition(1L, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeAyahCountIsRejected() {
        dailyAyahPosition(1L, -1)
    }
}
