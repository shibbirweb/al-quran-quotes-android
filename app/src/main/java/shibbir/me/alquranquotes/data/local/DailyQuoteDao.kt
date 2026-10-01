package shibbir.me.alquranquotes.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import shibbir.me.alquranquotes.model.Quote

/** Picks the quote of the day across every stored quote, bundled or the user's own. */
@Dao
abstract class DailyQuoteDao {

    /**
     * Returns the quote for [epochDay], or null when there are no quotes at all. The rotation
     * walks the quotes in [quoteDisplayOrder], the order of the Quotes list, and repeats after
     * the last one.
     *
     * The read runs in one transaction, so even a result too large for one cursor window is one
     * consistent snapshot that a seed merge or a user edit cannot change halfway through.
     */
    @Transaction
    open suspend fun getDailyQuote(epochDay: Long): Quote? {
        val storedQuoteEntities = getAllQuotes()
        if (storedQuoteEntities.isEmpty()) {
            return null
        }
        val orderedQuoteEntities = storedQuoteEntities.sortedWith(quoteDisplayOrder)
        val dailyPosition = dailyAyahPosition(epochDay, orderedQuoteEntities.size)
        val dailyQuoteEntity = orderedQuoteEntities[dailyPosition]
        return dailyQuoteEntity.toQuote()
    }

    @Query("SELECT * FROM quotes")
    protected abstract suspend fun getAllQuotes(): List<QuoteEntity>
}
