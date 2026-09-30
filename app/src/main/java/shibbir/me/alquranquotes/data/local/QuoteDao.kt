package shibbir.me.alquranquotes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import shibbir.me.alquranquotes.model.QuoteDraft

/** Reads and writes single quotes of any origin, and observes them all. */
@Dao
abstract class QuoteDao {

    /**
     * Emits every stored quote whenever the `quotes` table changes, in no particular order:
     * callers sort with [quoteDisplayOrder].
     */
    @Query("SELECT * FROM quotes")
    abstract fun observeQuotes(): Flow<List<QuoteEntity>>

    /** Returns the quote with [quoteId], or null when there is none. */
    @Query("SELECT * FROM quotes WHERE id = :quoteId")
    abstract suspend fun getQuote(quoteId: Long): QuoteEntity?

    /** Inserts [quoteEntity] with a new generated id (its own id must be 0) and returns it. */
    @Insert
    abstract suspend fun insertQuote(quoteEntity: QuoteEntity): Long

    /**
     * Deletes the quote with [quoteId], of any origin. The bundled key of a deleted bundled ayah
     * stays in `seeded_bundled_keys`, so a newer seed never adds it back.
     */
    @Query("DELETE FROM quotes WHERE id = :quoteId")
    abstract suspend fun deleteQuote(quoteId: Long)

    /**
     * Saves the fields of [quoteDraft] for the quote with [quoteId], in one transaction with the
     * read of its current row, and does nothing when there is none. See [editedWith]: the quote
     * keeps its kind and bundled key, and an untouched bundled ayah becomes edited.
     *
     * @throws IllegalArgumentException when [quoteDraft] is of the other kind than the quote.
     */
    @Transaction
    open suspend fun updateQuote(quoteId: Long, quoteDraft: QuoteDraft) {
        val storedQuoteEntity = getQuote(quoteId)
        if (storedQuoteEntity == null) {
            return
        }
        val editedQuoteEntity = storedQuoteEntity.editedWith(quoteDraft)
        updateQuoteRow(editedQuoteEntity)
    }

    @Update
    protected abstract suspend fun updateQuoteRow(quoteEntity: QuoteEntity)
}
