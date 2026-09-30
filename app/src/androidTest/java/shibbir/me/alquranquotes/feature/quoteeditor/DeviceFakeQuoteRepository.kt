package shibbir.me.alquranquotes.feature.quoteeditor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import shibbir.me.alquranquotes.data.repository.QuoteRepository
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteDraft
import java.util.concurrent.ConcurrentHashMap

/**
 * Records the drafts the editor saves, and serves the drafts in [storedQuoteDrafts] to edit.
 * Thread safe, because tests fill it from the instrumentation thread while the ViewModel reads
 * it on the main thread.
 */
class DeviceFakeQuoteRepository : QuoteRepository {

    val storedQuoteDrafts = ConcurrentHashMap<Long, QuoteDraft>()

    val addedQuoteDrafts = mutableListOf<QuoteDraft>()

    val updatedQuoteDrafts = mutableListOf<Pair<Long, QuoteDraft>>()

    override fun observeQuotes(): Flow<List<Quote>> = flowOf(emptyList())

    override suspend fun getQuoteDraft(quoteId: Long): QuoteDraft? = storedQuoteDrafts[quoteId]

    override suspend fun addQuote(quoteDraft: QuoteDraft): Long {
        addedQuoteDrafts += quoteDraft
        return addedQuoteDrafts.size.toLong()
    }

    override suspend fun updateQuote(quoteId: Long, quoteDraft: QuoteDraft) {
        updatedQuoteDrafts += quoteId to quoteDraft
    }

    override suspend fun deleteQuote(quoteId: Long) {
        // Not needed: the editor never deletes.
    }
}
