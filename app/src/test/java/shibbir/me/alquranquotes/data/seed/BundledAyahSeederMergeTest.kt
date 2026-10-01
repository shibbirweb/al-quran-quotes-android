package shibbir.me.alquranquotes.data.seed

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.data.local.QuoteEntity
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource
import shibbir.me.alquranquotes.testing.FakeBundledAyahSeedDao
import shibbir.me.alquranquotes.testing.FakeQuoteTables
import shibbir.me.alquranquotes.testing.testAyah
import shibbir.me.alquranquotes.testing.testBundledQuoteEntity
import shibbir.me.alquranquotes.testing.testUserFreeTextEntity

/** How [BundledAyahSeeder] merges a bundled seed into the stored quotes. */
class BundledAyahSeederMergeTest {

    @Test
    fun firstSeedAddsEveryBundledAyahAsUntouched() = runTest {
        val quoteTables = FakeQuoteTables()

        seedStoredQuotes(quoteTables, SampleAyahSeeds.firstAyahSeed)

        val expectedQuoteEntities = listOf(
            testBundledQuoteEntity(quoteId = 1L, surahNumber = 94, ayahNumber = 5),
            testBundledQuoteEntity(quoteId = 2L, surahNumber = 2, ayahNumber = 153),
            testBundledQuoteEntity(quoteId = 3L, surahNumber = 13, ayahNumber = 28),
        )
        assertEquals(expectedQuoteEntities, quoteTables.storedQuoteEntities)
    }

    @Test
    fun deletedBundledAyahIsNotAddedBack() = runTest {
        val quoteTables = FakeQuoteTables(
            initialSeededBundledKeys = setOf("94:5"),
            initialSeedVersion = 1,
        )

        seedStoredQuotes(quoteTables, newerAyahSeed(testAyah(surahNumber = 94, ayahNumber = 5)))

        assertEquals(emptyList<QuoteEntity>(), quoteTables.storedQuoteEntities)
    }

    @Test
    fun newerSeedAddsTheBundledAyahsTheUserNeverHad() = runTest {
        val storedAyahRow = testBundledQuoteEntity(quoteId = 1L, surahNumber = 94, ayahNumber = 5)
        val quoteTables = storedTables(storedAyahRow)
        val newerSeed = newerAyahSeed(
            testAyah(surahNumber = 94, ayahNumber = 5),
            testAyah(surahNumber = 2, ayahNumber = 153),
        )

        seedStoredQuotes(quoteTables, newerSeed)

        val addedAyahRow = testBundledQuoteEntity(quoteId = 2L, surahNumber = 2, ayahNumber = 153)
        assertEquals(listOf(storedAyahRow, addedAyahRow), quoteTables.storedQuoteEntities)
        assertEquals(setOf("94:5", "2:153"), quoteTables.seededBundledKeys)
    }

    @Test
    fun newerSeedRefreshesUntouchedBundledAyahsKeepingTheirIds() = runTest {
        val quoteTables = storedTables(
            testBundledQuoteEntity(quoteId = 4L, surahNumber = 94, ayahNumber = 5),
        )
        val refreshedSeedAyah = testAyah(surahNumber = 94, ayahNumber = 5, translation = "fixed")

        seedStoredQuotes(quoteTables, newerAyahSeed(refreshedSeedAyah))

        val refreshedAyahRow = testBundledQuoteEntity(
            quoteId = 4L,
            surahNumber = 94,
            ayahNumber = 5,
            translation = "fixed",
        )
        assertEquals(listOf(refreshedAyahRow), quoteTables.storedQuoteEntities)
    }

    @Test
    fun newerSeedNeverOverwritesAnEditedBundledAyah() = runTest {
        val editedAyahRow = testBundledQuoteEntity(
            quoteId = 4L,
            surahNumber = 94,
            ayahNumber = 5,
            origin = QuoteOrigin.EDITED_BUNDLED,
            translation = "my own words",
        )
        val quoteTables = storedTables(editedAyahRow)
        val refreshedSeedAyah = testAyah(surahNumber = 94, ayahNumber = 5, translation = "fixed")

        seedStoredQuotes(quoteTables, newerAyahSeed(refreshedSeedAyah))

        assertEquals(listOf(editedAyahRow), quoteTables.storedQuoteEntities)
    }

    @Test
    fun newerSeedRemovesUntouchedBundledAyahsItNoLongerContains() = runTest {
        val keptAyahRow = testBundledQuoteEntity(quoteId = 1L, surahNumber = 94, ayahNumber = 5)
        val withdrawnAyahRow = testBundledQuoteEntity(quoteId = 2L, surahNumber = 1, ayahNumber = 1)
        val quoteTables = storedTables(keptAyahRow, withdrawnAyahRow)

        seedStoredQuotes(quoteTables, newerAyahSeed(testAyah(surahNumber = 94, ayahNumber = 5)))

        assertEquals(listOf(keptAyahRow), quoteTables.storedQuoteEntities)
    }

    @Test
    fun newerSeedKeepsAnEditedBundledAyahItNoLongerContains() = runTest {
        val editedWithdrawnAyahRow = testBundledQuoteEntity(
            quoteId = 2L,
            surahNumber = 1,
            ayahNumber = 1,
            origin = QuoteOrigin.EDITED_BUNDLED,
        )
        val quoteTables = storedTables(editedWithdrawnAyahRow)

        seedStoredQuotes(quoteTables, newerAyahSeed(testAyah(surahNumber = 94, ayahNumber = 5)))

        val addedAyahRow = testBundledQuoteEntity(quoteId = 3L, surahNumber = 94, ayahNumber = 5)
        val expectedQuoteEntities = listOf(editedWithdrawnAyahRow, addedAyahRow)
        assertEquals(expectedQuoteEntities, quoteTables.storedQuoteEntities)
    }

    @Test
    fun newerSeedNeverTouchesTheUserQuotes() = runTest {
        val userQuoteRow = testUserFreeTextEntity(quoteId = 1L)
        val quoteTables = storedTables(userQuoteRow)

        seedStoredQuotes(quoteTables, newerAyahSeed(testAyah(surahNumber = 94, ayahNumber = 5)))

        val addedAyahRow = testBundledQuoteEntity(quoteId = 2L, surahNumber = 94, ayahNumber = 5)
        assertEquals(listOf(userQuoteRow, addedAyahRow), quoteTables.storedQuoteEntities)
    }

    /** Stored rows that an earlier seed at version 1 stored, with their bundled keys recorded. */
    private fun storedTables(vararg storedQuoteEntities: QuoteEntity): FakeQuoteTables {
        val seededBundledKeys = storedQuoteEntities.mapNotNull { it.bundledKey }
        return FakeQuoteTables(
            initialQuoteEntities = storedQuoteEntities.toList(),
            initialSeededBundledKeys = seededBundledKeys.toSet(),
            initialSeedVersion = 1,
        )
    }

    private fun newerAyahSeed(vararg seedAyahs: Ayah) = AyahSeed(
        version = 2,
        ayahs = seedAyahs.toList(),
    )

    private suspend fun seedStoredQuotes(quoteTables: FakeQuoteTables, ayahSeed: AyahSeed) {
        val bundledAyahSeedDao = FakeBundledAyahSeedDao(quoteTables)
        val bundledAyahSeeder = BundledAyahSeeder(bundledAyahSeedDao, FakeAyahSeedSource(ayahSeed))
        bundledAyahSeeder.ensureStoredSeedIsCurrent()
    }
}
