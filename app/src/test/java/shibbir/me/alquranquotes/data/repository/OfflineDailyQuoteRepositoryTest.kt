package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import shibbir.me.alquranquotes.data.seed.AyahSeed
import shibbir.me.alquranquotes.data.seed.BundledAyahSeeder
import shibbir.me.alquranquotes.data.seed.SampleAyahSeeds
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource
import shibbir.me.alquranquotes.testing.FakeBundledAyahSeedDao
import shibbir.me.alquranquotes.testing.FakeDailyQuoteDao
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.FakeQuoteTables
import shibbir.me.alquranquotes.testing.testUserAyahEntity

/** Which quote [OfflineDailyQuoteRepository] returns for a day. */
class OfflineDailyQuoteRepositoryTest {

    @Test
    fun returnsNullWhenThereAreNoQuotesAtAll() = runTest {
        val emptyAyahSeed = AyahSeed(version = 1, ayahs = emptyList())
        val repository = createRepository(FakeQuoteTables(), emptyAyahSeed)

        val dailyQuote = repository.getDailyQuote(epochDay = 0L)

        assertNull(dailyQuote)
    }

    /** The seed lists 94:5, 2:153, 13:28, so they get ids 1, 2, 3 in that order. */
    @Test
    fun seedsThenRotatesOverTheBundledAyahsInSurahAndAyahOrder() = runTest {
        val repository = createRepository(FakeQuoteTables())

        val dailyQuotes = (0L..2L).map { epochDay -> repository.getDailyQuote(epochDay) }

        val dailyQuoteIds = dailyQuotes.map { dailyQuote -> dailyQuote?.quoteId }
        assertEquals(listOf(2L, 3L, 1L), dailyQuoteIds)
        assertEquals(Quote.AyahQuote::class.java, dailyQuotes.first()?.javaClass)
    }

    /**
     * The sample tables in order are edited 13:28 (id 2), untouched 94:5 (id 1), the user's free
     * text (id 3), then the user's ayah added here (id 4), so the rotation repeats every four days,
     * also before the epoch.
     */
    @Test
    fun rotatesOverBundledAyahsThenUserQuotesAndWrapsAround() = runTest {
        val quoteTables = storedSampleTables()
        val userAyahRow = testUserAyahEntity(quoteId = 0L, surahNumber = 1, ayahNumber = 1)
        quoteTables.insertQuote(userAyahRow)
        val repository = createRepository(quoteTables)

        val epochDays = listOf(0L, 1L, 2L, 3L, 4L, -1L)
        val dailyQuotes = epochDays.map { epochDay -> repository.getDailyQuote(epochDay) }

        val dailyQuoteIds = dailyQuotes.map { dailyQuote -> dailyQuote?.quoteId }
        val expectedFreeTextQuote = Quote.FreeTextQuote(
            quoteId = 3L,
            origin = QuoteOrigin.USER,
            text = "free text",
            reference = "a reference",
        )
        assertEquals(listOf(2L, 1L, 3L, 4L, 2L, 4L), dailyQuoteIds)
        assertEquals(expectedFreeTextQuote, dailyQuotes[2])
    }

    private fun createRepository(
        quoteTables: FakeQuoteTables,
        ayahSeed: AyahSeed = SampleAyahSeeds.firstAyahSeed,
    ): OfflineDailyQuoteRepository {
        val bundledAyahSeedDao = FakeBundledAyahSeedDao(quoteTables)
        return OfflineDailyQuoteRepository(
            bundledAyahSeeder = BundledAyahSeeder(bundledAyahSeedDao, FakeAyahSeedSource(ayahSeed)),
            dailyQuoteDao = FakeDailyQuoteDao(quoteTables),
        )
    }
}
