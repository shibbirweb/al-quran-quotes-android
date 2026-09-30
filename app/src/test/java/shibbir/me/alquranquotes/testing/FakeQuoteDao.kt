package shibbir.me.alquranquotes.testing

import kotlinx.coroutines.flow.Flow
import shibbir.me.alquranquotes.data.local.QuoteDao
import shibbir.me.alquranquotes.data.local.QuoteEntity

/**
 * In-memory [QuoteDao] on [quoteTables]. Its open functions are not overridden, so tests run the
 * real DAO bodies on top of the in-memory table.
 */
class FakeQuoteDao(
    private val quoteTables: FakeQuoteTables,
) : QuoteDao() {

    override fun observeQuotes(): Flow<List<QuoteEntity>> = quoteTables.quoteTable

    override suspend fun getQuote(quoteId: Long): QuoteEntity? {
        return quoteTables.storedQuoteEntities.firstOrNull { it.id == quoteId }
    }

    override suspend fun insertQuote(quoteEntity: QuoteEntity): Long {
        return quoteTables.insertQuote(quoteEntity)
    }

    override suspend fun deleteQuote(quoteId: Long) {
        quoteTables.deleteQuote(quoteId)
    }

    override suspend fun updateQuoteRow(quoteEntity: QuoteEntity) {
        quoteTables.updateQuote(quoteEntity)
    }
}
