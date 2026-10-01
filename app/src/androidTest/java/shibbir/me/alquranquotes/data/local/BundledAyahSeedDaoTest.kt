package shibbir.me.alquranquotes.data.local

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.testing.createInMemoryQuranDatabase
import shibbir.me.alquranquotes.testing.testBundledQuoteEntity
import shibbir.me.alquranquotes.testing.testUserFreeTextEntity

/** The merge transaction of [BundledAyahSeedDao] on a real Room database. */
@RunWith(AndroidJUnit4::class)
class BundledAyahSeedDaoTest {

    private val database = createInMemoryQuranDatabase()

    private val bundledAyahSeedDao = database.bundledAyahSeedDao()

    private val quoteDao = database.quoteDao()

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun emptyDatabaseHasNoQuotesAndNoSeedVersion() = runTest {
        assertEquals(0, bundledAyahSeedDao.countQuotes())
        assertNull(bundledAyahSeedDao.getSeedVersion())
    }

    @Test
    fun firstMergeStoresEveryBundledAyahAndTheVersion() = runTest {
        val seedQuoteEntities = listOf(
            testBundledQuoteEntity(surahNumber = 94, ayahNumber = 5),
            testBundledQuoteEntity(surahNumber = 2, ayahNumber = 153),
        )

        bundledAyahSeedDao.mergeBundledSeed(seedQuoteEntities, seedVersion = 1)

        val storedBundledKeys = storedQuotes().map { it.bundledKey }
        assertEquals(listOf("94:5", "2:153"), storedBundledKeys)
        assertEquals(2, bundledAyahSeedDao.countQuotes())
        assertEquals(1, bundledAyahSeedDao.getSeedVersion())
    }

    /**
     * Version 1 stores 94:5, 2:153, 13:28, and 1:1. The user edits 13:28 and deletes 2:153 and
     * adds a free text quote. Version 2 changes every translation, drops 1:1 and 13:28, and adds
     * 2:286.
     */
    @Test
    fun newerMergeKeepsEveryUserChange() = runTest {
        val firstSeed = listOf("94:5", "2:153", "13:28", "1:1").map(::seedRow)
        bundledAyahSeedDao.mergeBundledSeed(firstSeed, seedVersion = 1)
        val editedQuoteId = storedQuoteId(bundledKey = "13:28")
        quoteDao.updateQuote(editedQuoteId, editedDraft())
        quoteDao.deleteQuote(storedQuoteId(bundledKey = "2:153"))
        val userQuoteId = quoteDao.insertQuote(testUserFreeTextEntity())

        val secondSeed = listOf("94:5", "2:153", "2:286").map { seedRow(it, "new translation") }
        bundledAyahSeedDao.mergeBundledSeed(secondSeed, seedVersion = 2)

        val storedTranslationsByKey = storedQuotes().associate { it.bundledKey to it.translation }
        val expectedTranslationsByKey = mapOf(
            "94:5" to "new translation",
            "13:28" to "edited translation",
            null to null,
            "2:286" to "new translation",
        )
        assertEquals(expectedTranslationsByKey, storedTranslationsByKey)
        assertEquals(userQuoteId, storedQuotes().single { it.bundledKey == null }.id)
        assertEquals(2, bundledAyahSeedDao.getSeedVersion())
    }

    @Test
    fun failedMergeRollsBackToThePreviousQuotesAndVersion() = runTest {
        bundledAyahSeedDao.mergeBundledSeed(listOf(seedRow("94:5")), seedVersion = 1)
        val quotesBeforeFailedMerge = storedQuotes()
        val duplicateKeySeed = listOf(seedRow("2:153"), seedRow("2:153", "duplicate"))

        val failedMerge = runCatching {
            bundledAyahSeedDao.mergeBundledSeed(duplicateKeySeed, seedVersion = 2)
        }

        val mergeFailure = failedMerge.exceptionOrNull()
        assertEquals(SQLiteConstraintException::class.java, mergeFailure?.javaClass)
        assertEquals(quotesBeforeFailedMerge, storedQuotes())
        assertEquals(1, bundledAyahSeedDao.getSeedVersion())
    }

    private fun seedRow(
        bundledKey: String,
        translation: String? = null,
    ): QuoteEntity {
        val (surahNumber, ayahNumber) = bundledKey.split(":").map(String::toInt)
        val seedRow = testBundledQuoteEntity(surahNumber = surahNumber, ayahNumber = ayahNumber)
        if (translation == null) {
            return seedRow
        }
        return seedRow.copy(translation = translation)
    }

    private fun editedDraft() = QuoteDraft.AyahDraft(
        surahName = "Surah 13",
        surahNumber = 13,
        ayahNumber = 28,
        arabicText = "edited-arabic",
        translation = "edited translation",
    )

    private suspend fun storedQuotes(): List<QuoteEntity> {
        val storedQuoteEntities = quoteDao.observeQuotes().first()
        return storedQuoteEntities.sortedBy { it.id }
    }

    private suspend fun storedQuoteId(bundledKey: String): Long {
        return storedQuotes().single { it.bundledKey == bundledKey }.id
    }
}
