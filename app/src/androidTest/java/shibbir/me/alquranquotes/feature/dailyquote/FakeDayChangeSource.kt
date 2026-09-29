package shibbir.me.alquranquotes.feature.dailyquote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import shibbir.me.alquranquotes.core.time.DayChangeSource

/** Device-test day change source that never emits, so only a resume can move to a new day. */
class FakeDayChangeSource : DayChangeSource {

    override fun dayChanges(): Flow<Unit> = emptyFlow()
}
