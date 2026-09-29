package shibbir.me.alquranquotes.core.time

import java.util.TimeZone
import javax.inject.Inject

/** Supplies the current local day as days since 1970-01-01. */
fun interface EpochDayProvider {
    fun today(): Long
}

class SystemEpochDayProvider @Inject constructor() : EpochDayProvider {
    override fun today(): Long = epochDayOf(System.currentTimeMillis(), TimeZone.getDefault())
}

private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L

internal fun epochDayOf(epochMillis: Long, timeZone: TimeZone): Long =
    Math.floorDiv(epochMillis + timeZone.getOffset(epochMillis), MILLIS_PER_DAY)
