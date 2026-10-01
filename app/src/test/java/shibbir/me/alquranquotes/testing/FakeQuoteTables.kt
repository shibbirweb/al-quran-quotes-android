package shibbir.me.alquranquotes.testing

import kotlinx.coroutines.flow.MutableStateFlow
import shibbir.me.alquranquotes.data.local.QuoteEntity

/**
 * In-memory stand-in for the `quotes`, `seeded_bundled_keys`, and `ayah_seed_info` tables. The
 * fake DAOs share one instance, so a test can seed through one DAO and read through another. Like
 * Room it gives every new row the next id (ids only ever grow, like AUTOINCREMENT), rejects a
 * second row with the same bundled key, and [quoteTable] emits the whole table after every change.
 */
class FakeQuoteTables(
    initialQuoteEntities: List<QuoteEntity> = emptyList(),
    initialSeededBundledKeys: Set<String> = emptySet(),
    initialSeedVersion: Int? = null,
) {

    /** Stands in for the `quotes` table, in insertion (so id) order. */
    val quoteTable = MutableStateFlow(initialQuoteEntities)

    /** Stands in for the `seeded_bundled_keys` table. */
    var seededBundledKeys: Set<String> = initialSeededBundledKeys

    /** Stands in for the single `ayah_seed_info` row. */
    var seedVersion: Int? = initialSeedVersion

    private var lastQuoteId = initialQuoteEntities.maxOfOrNull { it.id } ?: 0L

    val storedQuoteEntities: List<QuoteEntity>
        get() = quoteTable.value

    /** Ignores the id of [quoteEntity], like Room does for an autoGenerate id of 0. */
    fun insertQuote(quoteEntity: QuoteEntity): Long {
        checkBundledKeyIsFree(quoteEntity.bundledKey)
        lastQuoteId++
        val insertedQuoteEntity = quoteEntity.copy(id = lastQuoteId)
        quoteTable.value = quoteTable.value + insertedQuoteEntity
        return lastQuoteId
    }

    /** Replaces the row with the id of [quoteEntity], and does nothing when there is none. */
    fun updateQuote(quoteEntity: QuoteEntity) {
        val updatedQuoteEntities = storedQuoteEntities.map { storedQuoteEntity ->
            chooseRowAfterUpdate(storedQuoteEntity, quoteEntity)
        }
        quoteTable.value = updatedQuoteEntities
    }

    fun deleteQuote(quoteId: Long) {
        val remainingQuoteEntities = storedQuoteEntities.filterNot { it.id == quoteId }
        quoteTable.value = remainingQuoteEntities
    }

    private fun chooseRowAfterUpdate(
        storedQuoteEntity: QuoteEntity,
        updatedQuoteEntity: QuoteEntity,
    ): QuoteEntity {
        if (storedQuoteEntity.id == updatedQuoteEntity.id) {
            return updatedQuoteEntity
        }
        return storedQuoteEntity
    }

    /** Throws like the unique index on `bundled_key`; null keys never clash. */
    private fun checkBundledKeyIsFree(bundledKey: String?) {
        if (bundledKey == null) {
            return
        }
        val isDuplicateKey = storedQuoteEntities.any { it.bundledKey == bundledKey }
        check(!isDuplicateKey) { "UNIQUE constraint failed: quotes.bundled_key" }
    }
}
