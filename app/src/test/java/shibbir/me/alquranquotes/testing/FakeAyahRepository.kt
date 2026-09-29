package shibbir.me.alquranquotes.testing

import kotlinx.coroutines.CompletableDeferred
import shibbir.me.alquranquotes.data.repository.AyahRepository
import shibbir.me.alquranquotes.model.Ayah

class FakeAyahRepository : AyahRepository {

    var dailyAyah: Ayah? = null
    var failureToThrow: Exception? = null

    /**
     * When set, [getDailyAyah] suspends until this is completed, so tests can observe the
     * Loading state. The ayah and failure are captured when the call starts, so a call that
     * resumes late still returns what was configured for it.
     */
    var responseGate: CompletableDeferred<Unit>? = null

    val requestedEpochDays = mutableListOf<Long>()

    override suspend fun getDailyAyah(epochDay: Long): Ayah? {
        requestedEpochDays += epochDay
        val ayahForThisCall = dailyAyah
        val failureForThisCall = failureToThrow
        val responseGateForThisCall = responseGate
        if (responseGateForThisCall != null) {
            responseGateForThisCall.await()
        }
        if (failureForThisCall != null) {
            throw failureForThisCall
        }
        return ayahForThisCall
    }
}
