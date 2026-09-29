package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.core.time.EpochDayProvider

/** Epoch day provider whose day tests can change between calls. */
class FakeEpochDayProvider(
    var currentEpochDay: Long = 0L,
) : EpochDayProvider {

    override fun today(): Long = currentEpochDay
}
