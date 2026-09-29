package shibbir.me.alquranquotes.feature.dailyquote

import shibbir.me.alquranquotes.core.time.EpochDayProvider

/**
 * Device-test epoch day provider whose day tests can change between calls. Volatile, because
 * tests set it on the instrumentation thread and the ViewModel reads it on the main thread.
 */
class FakeEpochDayProvider(
    @Volatile var currentEpochDay: Long = 0L,
) : EpochDayProvider {

    override fun today(): Long = currentEpochDay
}
