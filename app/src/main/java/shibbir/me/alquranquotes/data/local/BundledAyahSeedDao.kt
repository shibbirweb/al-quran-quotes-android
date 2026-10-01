package shibbir.me.alquranquotes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import shibbir.me.alquranquotes.model.QuoteOrigin

/** Stores the bundled ayah seed in the `quotes` table without undoing any change the user made. */
@Dao
abstract class BundledAyahSeedDao {

    /** Returns the seed version recorded by [mergeBundledSeed], or null before the first seed. */
    @Query(
        "SELECT seed_version FROM ayah_seed_info WHERE id = ${AyahSeedInfoEntity.SINGLE_ROW_ID}",
    )
    abstract suspend fun getSeedVersion(): Int?

    @Query("SELECT COUNT(*) FROM quotes")
    abstract suspend fun countQuotes(): Int

    /**
     * Merges the bundled seed rows [seedQuoteEntities] (each with its bundled key) and records
     * [seedVersion], all in one transaction: when any step fails, every table stays as it was.
     * The user's changes win:
     * - a seed ayah whose bundled key no seed ever stored is added, untouched;
     * - an untouched bundled ayah is refreshed from the seed, keeping its id;
     * - an edited bundled ayah is kept as the user left it, even when the seed drops it;
     * - a bundled ayah the user deleted is not added back, because its key stays recorded;
     * - an untouched bundled ayah the seed no longer contains is removed;
     * - the user's own quotes are never touched.
     */
    @Transaction
    open suspend fun mergeBundledSeed(seedQuoteEntities: List<QuoteEntity>, seedVersion: Int) {
        val untouchedBundledQuotes = getUntouchedBundledQuotes()
        val seedBundledKeys = seedQuoteEntities.mapNotNull { seedQuote -> seedQuote.bundledKey }
        removeWithdrawnQuotes(untouchedBundledQuotes, seedBundledKeys)
        refreshUntouchedQuotes(untouchedBundledQuotes, seedQuoteEntities)
        addNeverSeededQuotes(seedQuoteEntities)
        recordSeed(seedBundledKeys, seedVersion)
    }

    /** Bundled ayahs the user never edited: the only stored rows a seed may change. */
    private suspend fun getUntouchedBundledQuotes(): List<QuoteEntity> {
        val storedBundledQuotes = getStoredBundledQuotes()
        return storedBundledQuotes.filter { storedQuote ->
            storedQuote.origin == QuoteOrigin.BUNDLED
        }
    }

    /** Removes the untouched bundled ayahs that the seed no longer contains. */
    private suspend fun removeWithdrawnQuotes(
        untouchedBundledQuotes: List<QuoteEntity>,
        seedBundledKeys: List<String>,
    ) {
        val seedBundledKeySet = seedBundledKeys.toSet()
        val withdrawnQuotes = untouchedBundledQuotes.filterNot { untouchedQuote ->
            untouchedQuote.bundledKey in seedBundledKeySet
        }
        deleteQuotes(withdrawnQuotes)
    }

    private suspend fun refreshUntouchedQuotes(
        untouchedBundledQuotes: List<QuoteEntity>,
        seedQuoteEntities: List<QuoteEntity>,
    ) {
        val untouchedQuoteIdsByBundledKey = untouchedBundledQuotes.associate { untouchedQuote ->
            untouchedQuote.bundledKey to untouchedQuote.id
        }
        val refreshedQuotes = seedQuoteEntities.mapNotNull { seedQuote ->
            refreshedQuoteOrNull(seedQuote, untouchedQuoteIdsByBundledKey)
        }
        updateQuotes(refreshedQuotes)
    }

    /** The seed row with the id of the untouched row that has its bundled key, if any. */
    private fun refreshedQuoteOrNull(
        seedQuote: QuoteEntity,
        untouchedQuoteIdsByBundledKey: Map<String?, Long>,
    ): QuoteEntity? {
        val untouchedQuoteId = untouchedQuoteIdsByBundledKey[seedQuote.bundledKey]
        if (untouchedQuoteId == null) {
            return null
        }
        return seedQuote.copy(id = untouchedQuoteId)
    }

    /** Adds the seed rows whose bundled key no seed ever stored, so deleted ones stay deleted. */
    private suspend fun addNeverSeededQuotes(seedQuoteEntities: List<QuoteEntity>) {
        val seededBundledKeys = getSeededBundledKeys().toSet()
        val neverSeededQuotes = seedQuoteEntities.filterNot { seedQuote ->
            seedQuote.bundledKey in seededBundledKeys
        }
        insertQuotes(neverSeededQuotes)
    }

    private suspend fun recordSeed(seedBundledKeys: List<String>, seedVersion: Int) {
        val seededBundledKeyEntities = seedBundledKeys.map { seedBundledKey ->
            SeededBundledKeyEntity(seedBundledKey)
        }
        insertSeededBundledKeys(seededBundledKeyEntities)
        val seedInfo = AyahSeedInfoEntity(seedVersion = seedVersion)
        upsertSeedInfo(seedInfo)
    }

    /** Every row that came from a seed, edited or not. */
    @Query("SELECT * FROM quotes WHERE bundled_key IS NOT NULL")
    protected abstract suspend fun getStoredBundledQuotes(): List<QuoteEntity>

    @Query("SELECT bundled_key FROM seeded_bundled_keys")
    protected abstract suspend fun getSeededBundledKeys(): List<String>

    /** Fails on a bundled key that is already stored, which rolls back [mergeBundledSeed]. */
    @Insert
    protected abstract suspend fun insertQuotes(quoteEntities: List<QuoteEntity>)

    @Delete
    protected abstract suspend fun deleteQuotes(quoteEntities: List<QuoteEntity>)

    @Update
    protected abstract suspend fun updateQuotes(quoteEntities: List<QuoteEntity>)

    /** Keeps the keys that are already recorded. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertSeededBundledKeys(
        seededBundledKeys: List<SeededBundledKeyEntity>,
    )

    @Upsert
    protected abstract suspend fun upsertSeedInfo(seedInfo: AyahSeedInfoEntity)
}
