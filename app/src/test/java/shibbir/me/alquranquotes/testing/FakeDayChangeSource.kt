package shibbir.me.alquranquotes.testing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import shibbir.me.alquranquotes.core.time.DayChangeSource

/** Day change source that emits only when a test calls [emitDayChange]. */
class FakeDayChangeSource : DayChangeSource {

    private val dayChangeEvents = MutableSharedFlow<Unit>()

    override fun dayChanges(): Flow<Unit> = dayChangeEvents

    /** Sends one day change event and suspends until every collector has received it. */
    suspend fun emitDayChange() {
        dayChangeEvents.emit(Unit)
    }
}
