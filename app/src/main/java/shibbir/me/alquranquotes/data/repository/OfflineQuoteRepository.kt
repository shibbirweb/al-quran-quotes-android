package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import shibbir.me.alquranquotes.data.local.QuoteDao
import shibbir.me.alquranquotes.data.local.QuoteEntity
import shibbir.me.alquranquotes.data.local.quoteDisplayOrder
import shibbir.me.alquranquotes.data.local.toQuote
import shibbir.me.alquranquotes.data.local.toQuoteDraft
import shibbir.me.alquranquotes.data.local.toQuoteEntity
import shibbir.me.alquranquotes.data.seed.BundledAyahSeeder
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.model.QuoteOrigin
import javax.inject.Inject

/**
 * Serves every quote from Room and saves the user's changes there, for bundled ayahs and the
 * user's own quotes alike. Safe to call from the main thread, like [BundledAyahSeeder] and Room's
 * suspend and Flow DAO calls.
 */
class OfflineQuoteRepository @Inject constructor(
    private val bundledAyahSeeder: BundledAyahSeeder,
    private val quoteDao: QuoteDao,
) : QuoteRepository {

    /** Makes sure the bundled ayahs are current before the first emission. */
    override fun observeQuotes(): Flow<List<Quote>> = flow {
        bundledAyahSeeder.ensureStoredSeedIsCurrent()
        val orderedQuotes = quoteDao.observeQuotes().map(::orderedQuotes)
        emitAll(orderedQuotes)
    }

    override suspend fun getQuoteDraft(quoteId: Long): QuoteDraft? {
        val quoteEntity = quoteDao.getQuote(quoteId)
        return quoteEntity?.toQuoteDraft()
    }

    override suspend fun addQuote(quoteDraft: QuoteDraft): Long {
        val newQuoteEntity = quoteDraft.toQuoteEntity(origin = QuoteOrigin.USER)
        return quoteDao.insertQuote(newQuoteEntity)
    }

    override suspend fun updateQuote(quoteId: Long, quoteDraft: QuoteDraft) {
        quoteDao.updateQuote(quoteId, quoteDraft)
    }

    override suspend fun deleteQuote(quoteId: Long) {
        quoteDao.deleteQuote(quoteId)
    }
}

/** Maps the rows to quotes in [quoteDisplayOrder], the order of the Quotes list. */
private fun orderedQuotes(quoteEntities: List<QuoteEntity>): List<Quote> {
    val orderedQuoteEntities = quoteEntities.sortedWith(quoteDisplayOrder)
    return orderedQuoteEntities.map { quoteEntity -> quoteEntity.toQuote() }
}
