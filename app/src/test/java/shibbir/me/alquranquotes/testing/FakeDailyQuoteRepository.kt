package shibbir.me.alquranquotes.testing

import kotlinx.coroutines.CompletableDeferred
import shibbir.me.alquranquotes.data.repository.DailyQuoteRepository
import shibbir.me.alquranquotes.model.Quote

class FakeDailyQuoteRepository : DailyQuoteRepository {

    var dailyQuote: Quote? = null
    var failureToThrow: Exception? = null

    /**
     * When set, [getDailyQuote] suspends until this is completed, so tests can observe the
     * Loading state. The quote and failure are captured when the call starts, so a call that
     * resumes late still returns what was configured for it.
     */
    var responseGate: CompletableDeferred<Unit>? = null

    val requestedEpochDays = mutableListOf<Long>()

    override suspend fun getDailyQuote(epochDay: Long): Quote? {
        requestedEpochDays += epochDay
        val quoteForThisCall = dailyQuote
        val failureForThisCall = failureToThrow
        val responseGateForThisCall = responseGate
        if (responseGateForThisCall != null) {
            responseGateForThisCall.await()
        }
        if (failureForThisCall != null) {
            throw failureForThisCall
        }
        return quoteForThisCall
    }
}
