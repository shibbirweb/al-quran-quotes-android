package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.local.DailyQuoteDao
import shibbir.me.alquranquotes.data.local.QuoteEntity

/**
 * In-memory [DailyQuoteDao] that reads [quoteTables], so a test can seed and add quotes through
 * the other fakes. It does not override [getDailyQuote], so tests run the real DAO body.
 */
class FakeDailyQuoteDao(
    private val quoteTables: FakeQuoteTables,
) : DailyQuoteDao() {

    override suspend fun getAllQuotes(): List<QuoteEntity> = quoteTables.storedQuoteEntities
}
