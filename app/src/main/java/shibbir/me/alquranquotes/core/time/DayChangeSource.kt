package shibbir.me.alquranquotes.core.time

import kotlinx.coroutines.flow.Flow

/**
 * Tells collectors when the local calendar day may have changed, so they can read today again.
 */
fun interface DayChangeSource {

    /**
     * Emits whenever the local calendar day may have changed: the date changed at midnight, the
     * user or the network changed the clock, or the time zone changed. An emission only means
     * "check again"; the day itself may be the same.
     */
    fun dayChanges(): Flow<Unit>
}
