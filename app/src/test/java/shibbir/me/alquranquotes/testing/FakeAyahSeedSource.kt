package shibbir.me.alquranquotes.testing

import kotlinx.coroutines.CompletableDeferred
import shibbir.me.alquranquotes.data.seed.AyahSeed
import shibbir.me.alquranquotes.data.seed.AyahSeedSource

class FakeAyahSeedSource(private val ayahSeed: AyahSeed) : AyahSeedSource {

    var loadCount = 0
        private set

    /** When set, the next [load] throws this failure once and then clears it. */
    var nextLoadFailure: Exception? = null

    /**
     * When set, [load] suspends until this is completed, so tests can start several callers
     * while the first one is still loading.
     */
    var loadGate: CompletableDeferred<Unit>? = null

    override suspend fun load(): AyahSeed {
        loadCount++
        val loadGateForThisCall = loadGate
        if (loadGateForThisCall != null) {
            loadGateForThisCall.await()
        }
        val failureForThisCall = nextLoadFailure
        if (failureForThisCall != null) {
            nextLoadFailure = null
            throw failureForThisCall
        }
        return ayahSeed
    }
}
