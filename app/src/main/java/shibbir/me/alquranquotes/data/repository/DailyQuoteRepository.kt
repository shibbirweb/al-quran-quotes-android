package shibbir.me.alquranquotes.data.repository

import shibbir.me.alquranquotes.model.Quote

interface DailyQuoteRepository {
    /**
     * Returns the quote for [epochDay], or null when there are no quotes at all. The rotation
     * uses the same order as [QuoteRepository.observeQuotes].
     */
    suspend fun getDailyQuote(epochDay: Long): Quote?
}
