package shibbir.me.alquranquotes.core.time

import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.TimeZone

class SystemEpochDayProviderTest {

    private val originalDefaultTimeZone: TimeZone = TimeZone.getDefault()
    private val epochDayProvider = SystemEpochDayProvider()

    /** Etc/GMT-14 is UTC+14: the POSIX sign in Etc zone names is inverted. */
    private val fourteenHoursAheadOfUtcTimeZone = TimeZone.getTimeZone("Etc/GMT-14")

    /** Etc/GMT+12 is UTC-12: the POSIX sign in Etc zone names is inverted. */
    private val twelveHoursBehindUtcTimeZone = TimeZone.getTimeZone("Etc/GMT+12")

    @After
    fun restoreDefaultTimeZone() {
        TimeZone.setDefault(originalDefaultTimeZone)
    }

    /** The call can cross midnight, so the result may be the day just before or just after it. */
    @Test
    fun todayIsTheCurrentDayInTheDeviceTimeZone() {
        val deviceTimeZone = TimeZone.getDefault()
        val epochDayBeforeCall = epochDayOf(System.currentTimeMillis(), deviceTimeZone)

        val today = epochDayProvider.today()

        val epochDayAfterCall = epochDayOf(System.currentTimeMillis(), deviceTimeZone)
        assertTrue(today in epochDayBeforeCall..epochDayAfterCall)
    }

    /**
     * UTC+14 is 26 hours ahead of UTC-12, so its local day is 2 days ahead from 10:00 to 12:00
     * UTC and 1 day ahead the rest of the time. A provider that cached the time zone would return
     * the same zone's day for both calls, which fails the per-zone check in
     * [todayWithDefaultTimeZone].
     */
    @Test
    fun todayReadsTheCurrentDefaultTimeZoneOnEveryCall() {
        val todayFourteenHoursAhead = todayWithDefaultTimeZone(fourteenHoursAheadOfUtcTimeZone)
        val todayTwelveHoursBehind = todayWithDefaultTimeZone(twelveHoursBehindUtcTimeZone)

        val dayDifference = todayFourteenHoursAhead - todayTwelveHoursBehind
        assertTrue(dayDifference in 1L..2L)
    }

    /**
     * Makes [timeZone] the default, reads today from the shared provider, and checks it is the
     * local day in [timeZone] just before or just after the call (the call can cross midnight).
     */
    private fun todayWithDefaultTimeZone(timeZone: TimeZone): Long {
        TimeZone.setDefault(timeZone)
        val epochDayBeforeCall = epochDayOf(System.currentTimeMillis(), timeZone)

        val today = epochDayProvider.today()

        val epochDayAfterCall = epochDayOf(System.currentTimeMillis(), timeZone)
        assertTrue(today in epochDayBeforeCall..epochDayAfterCall)
        return today
    }
}
