package shibbir.me.alquranquotes.core.time

import java.util.TimeZone
import javax.inject.Inject

/** Reads today from the device clock in the device's current time zone. */
class SystemEpochDayProvider @Inject constructor() : EpochDayProvider {

    override fun today(): Long {
        val nowEpochMillis = System.currentTimeMillis()
        val deviceTimeZone = TimeZone.getDefault()
        return epochDayOf(nowEpochMillis, deviceTimeZone)
    }
}
