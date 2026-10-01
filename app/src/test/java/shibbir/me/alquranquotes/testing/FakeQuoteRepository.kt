package shibbir.me.alquranquotes.testing

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import shibbir.me.alquranquotes.data.repository.QuoteRepository
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.model.QuoteOrigin

/**
 * An in-memory [QuoteRepository]. Quotes are listed in the order they were stored, and new
 * quotes get the next id after the highest one stored. Every call is recorded so tests can
 * check it.
 */
class FakeQuoteRepository : QuoteRepository {

    private val storedQuotes = MutableStateFlow<List<Quote>>(emptyList())

    /** When set, every call except [observeQuotes] throws it. */
    var failureToThrow: Exception? = null

    /** When set, [observeQuotes] emits nothing until this is completed. */
    var quotesGate: CompletableDeferred<Unit>? = null

    /** When set, every suspending call waits until this is completed. */
    var responseGate: CompletableDeferred<Unit>? = null

    val requestedQuoteIds = mutableListOf<Long>()

    val addedQuoteDrafts = mutableListOf<QuoteDraft>()

    val updatedQuoteDrafts = mutableListOf<Pair<Long, QuoteDraft>>()

    val deletedQuoteIds = mutableListOf<Long>()

    /** Stores [quote] as if it were already saved, without recording a call. */
    fun storeQuote(quote: Quote) {
        storedQuotes.value = storedQuotes.value + quote
    }

    /** Stores [quoteDraft] as the user's own quote, without recording a call. */
    fun storeUserQuote(quoteDraft: QuoteDraft): Long {
        val quoteId = nextQuoteId()
        storeQuote(quoteDraft.toQuote(quoteId = quoteId, origin = QuoteOrigin.USER))
        return quoteId
    }

    override fun observeQuotes(): Flow<List<Quote>> = flow {
        val quotesGateForThisCall = quotesGate
        if (quotesGateForThisCall != null) {
            quotesGateForThisCall.await()
        }
        emitAll(storedQuotes)
    }

    override suspend fun getQuoteDraft(quoteId: Long): QuoteDraft? {
        requestedQuoteIds += quoteId
        waitForResponseGateThenFailIfConfigured()
        val storedQuote = findQuote(quoteId)
        return storedQuote?.toQuoteDraft()
    }

    override suspend fun addQuote(quoteDraft: QuoteDraft): Long {
        addedQuoteDrafts += quoteDraft
        waitForResponseGateThenFailIfConfigured()
        return storeUserQuote(quoteDraft)
    }

    override suspend fun updateQuote(quoteId: Long, quoteDraft: QuoteDraft) {
        updatedQuoteDrafts += quoteId to quoteDraft
        waitForResponseGateThenFailIfConfigured()
        storedQuotes.value = storedQuotes.value.map { storedQuote ->
            updatedQuote(storedQuote, quoteId, quoteDraft)
        }
    }

    override suspend fun deleteQuote(quoteId: Long) {
        deletedQuoteIds += quoteId
        waitForResponseGateThenFailIfConfigured()
        storedQuotes.value = storedQuotes.value.filter { storedQuote ->
            storedQuote.quoteId != quoteId
        }
    }

    private fun findQuote(quoteId: Long): Quote? {
        return storedQuotes.value.firstOrNull { storedQuote -> storedQuote.quoteId == quoteId }
    }

    private fun nextQuoteId(): Long {
        val highestQuoteId = storedQuotes.value.maxOfOrNull { storedQuote -> storedQuote.quoteId }
        return (highestQuoteId ?: 0L) + 1L
    }

    private suspend fun waitForResponseGateThenFailIfConfigured() {
        val failureForThisCall = failureToThrow
        val responseGateForThisCall = responseGate
        if (responseGateForThisCall != null) {
            responseGateForThisCall.await()
        }
        if (failureForThisCall != null) {
            throw failureForThisCall
        }
    }
}

/** A bundled ayah that is edited becomes EDITED_BUNDLED, as the real repository promises. */
private fun updatedQuote(storedQuote: Quote, quoteId: Long, quoteDraft: QuoteDraft): Quote {
    if (storedQuote.quoteId != quoteId) {
        return storedQuote
    }
    var updatedOrigin = storedQuote.origin
    if (updatedOrigin == QuoteOrigin.BUNDLED) {
        updatedOrigin = QuoteOrigin.EDITED_BUNDLED
    }
    return quoteDraft.toQuote(quoteId = quoteId, origin = updatedOrigin)
}

private fun QuoteDraft.toQuote(quoteId: Long, origin: QuoteOrigin): Quote {
    return when (this) {
        is QuoteDraft.AyahDraft -> Quote.AyahQuote(
            quoteId = quoteId,
            origin = origin,
            surahName = surahName,
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            arabicText = arabicText,
            translation = translation,
        )
        is QuoteDraft.FreeTextDraft -> Quote.FreeTextQuote(
            quoteId = quoteId,
            origin = origin,
            text = text,
            reference = reference,
        )
    }
}

private fun Quote.toQuoteDraft(): QuoteDraft {
    return when (this) {
        is Quote.AyahQuote -> QuoteDraft.AyahDraft(
            surahName = surahName,
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            arabicText = arabicText,
            translation = translation,
        )
        is Quote.FreeTextQuote -> QuoteDraft.FreeTextDraft(
            text = text,
            reference = reference,
        )
    }
}
