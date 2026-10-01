package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.flow.Flow
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteDraft

/** All quotes for the Quotes list, and create, read, update, delete for every quote. */
interface QuoteRepository {

    /**
     * Emits every quote whenever it changes: bundled ayahs (edited or not) first, by surah
     * number then ayah number, then the user's own quotes, oldest first.
     */
    fun observeQuotes(): Flow<List<Quote>>

    /** Returns the saved fields of a quote, or null when it does not exist. */
    suspend fun getQuoteDraft(quoteId: Long): QuoteDraft?

    /** Saves a new quote of origin [shibbir.me.alquranquotes.model.QuoteOrigin.USER]. */
    suspend fun addQuote(quoteDraft: QuoteDraft): Long

    /** Saves new fields for any quote. A bundled ayah becomes EDITED_BUNDLED. */
    suspend fun updateQuote(quoteId: Long, quoteDraft: QuoteDraft)

    /** Deletes any quote. A deleted bundled ayah never comes back with an app update. */
    suspend fun deleteQuote(quoteId: Long)
}
