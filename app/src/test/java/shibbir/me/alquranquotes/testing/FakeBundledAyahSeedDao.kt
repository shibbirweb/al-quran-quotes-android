package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.local.AyahSeedInfoEntity
import shibbir.me.alquranquotes.data.local.BundledAyahSeedDao
import shibbir.me.alquranquotes.data.local.QuoteEntity
import shibbir.me.alquranquotes.data.local.SeededBundledKeyEntity

/**
 * In-memory [BundledAyahSeedDao] on [quoteTables]. Its [mergeBundledSeed] wraps the real DAO
 * body, so tests run that body, and rolls every table back when it fails, like a transaction.
 */
class FakeBundledAyahSeedDao(
    private val quoteTables: FakeQuoteTables,
) : BundledAyahSeedDao() {

    var mergeCallCount = 0
        private set

    override suspend fun getSeedVersion(): Int? = quoteTables.seedVersion

    override suspend fun countQuotes(): Int = quoteTables.storedQuoteEntities.size

    override suspend fun mergeBundledSeed(seedQuoteEntities: List<QuoteEntity>, seedVersion: Int) {
        mergeCallCount++
        val quoteEntitiesBeforeMerge = quoteTables.storedQuoteEntities
        val seededBundledKeysBeforeMerge = quoteTables.seededBundledKeys
        val seedVersionBeforeMerge = quoteTables.seedVersion
        try {
            super.mergeBundledSeed(seedQuoteEntities, seedVersion)
        } catch (mergeFailure: Exception) {
            quoteTables.quoteTable.value = quoteEntitiesBeforeMerge
            quoteTables.seededBundledKeys = seededBundledKeysBeforeMerge
            quoteTables.seedVersion = seedVersionBeforeMerge
            throw mergeFailure
        }
    }

    override suspend fun getStoredBundledQuotes(): List<QuoteEntity> {
        return quoteTables.storedQuoteEntities.filter { it.bundledKey != null }
    }

    /** Deletes the rows with the same ids, like `@Delete`. */
    override suspend fun deleteQuotes(quoteEntities: List<QuoteEntity>) {
        quoteEntities.forEach { quoteEntity -> quoteTables.deleteQuote(quoteEntity.id) }
    }

    /** Replaces the rows with the same ids, like `@Update`. */
    override suspend fun updateQuotes(quoteEntities: List<QuoteEntity>) {
        quoteEntities.forEach { quoteEntity -> quoteTables.updateQuote(quoteEntity) }
    }

    override suspend fun getSeededBundledKeys(): List<String> {
        return quoteTables.seededBundledKeys.toList()
    }

    override suspend fun insertQuotes(quoteEntities: List<QuoteEntity>) {
        quoteEntities.forEach { quoteEntity -> quoteTables.insertQuote(quoteEntity) }
    }

    /** Ignores keys already recorded, like OnConflictStrategy.IGNORE. */
    override suspend fun insertSeededBundledKeys(seededBundledKeys: List<SeededBundledKeyEntity>) {
        val newBundledKeys = seededBundledKeys.map { it.bundledKey }
        quoteTables.seededBundledKeys = quoteTables.seededBundledKeys + newBundledKeys
    }

    override suspend fun upsertSeedInfo(seedInfo: AyahSeedInfoEntity) {
        quoteTables.seedVersion = seedInfo.seedVersion
    }
}
