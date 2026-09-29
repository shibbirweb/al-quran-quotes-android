package shibbir.me.alquranquotes.core.time

import java.util.TimeZone

internal const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L

/**
 * Returns the local calendar day of [epochMillis] in [timeZone], as days since 1970-01-01.
 *
 * Epoch millis count UTC time, so the zone's offset at that instant (including daylight saving)
 * is added first to get local wall-clock millis. Dividing by whole days with floorDiv then gives
 * the local calendar day, and keeps times before the epoch on negative days.
 */
internal fun epochDayOf(epochMillis: Long, timeZone: TimeZone): Long {
    val zoneOffsetMillis = timeZone.getOffset(epochMillis)
    val localEpochMillis = epochMillis + zoneOffsetMillis
    return Math.floorDiv(localEpochMillis, MILLIS_PER_DAY)
}
