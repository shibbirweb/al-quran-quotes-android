package shibbir.me.alquranquotes.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.data.seed.AssetAyahSeedSource
import shibbir.me.alquranquotes.data.seed.BundledAyahSeeder
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.createInMemoryQuranDatabase

/**
 * Checks the real bundled asset flows through Room into the daily quote, followed by the user's
 * quotes. Expectations come from the asset itself, so the test never repeats Quran text.
 */
@RunWith(AndroidJUnit4::class)
class OfflineDailyQuoteRepositoryDeviceTest {

    private val database = createInMemoryQuranDatabase()

    private val ayahSeedSource = AssetAyahSeedSource(
        context = InstrumentationRegistry.getInstrumentation().targetContext,
        ioDispatcher = Dispatchers.IO,
    )

    private val bundledAyahSeeder = BundledAyahSeeder(database.bundledAyahSeedDao(), ayahSeedSource)

    private val repository = OfflineDailyQuoteRepository(
        bundledAyahSeeder = bundledAyahSeeder,
        dailyQuoteDao = database.dailyQuoteDao(),
    )

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun everyDayOfTheCycleGivesTheBundledAyahAtThatPosition() = runTest {
        val orderedSeedAyahs = orderedBySurahThenAyah(ayahSeedSource.load().ayahs)

        val dailyQuotes = orderedSeedAyahs.indices.map { position ->
            repository.getDailyQuote(epochDay = position.toLong())
        }

        val dailyAyahQuotes = dailyQuotes.filterIsInstance<Quote.AyahQuote>()
        val dailyAyahNumbers = dailyAyahQuotes.map { it.surahNumber to it.ayahNumber }
        val seedAyahNumbers = orderedSeedAyahs.map { it.surahNumber to it.ayahNumber }
        assertTrue(orderedSeedAyahs.isNotEmpty())
        assertEquals(seedAyahNumbers, dailyAyahNumbers)
        assertTrue(dailyAyahQuotes.all { it.origin == QuoteOrigin.BUNDLED })
    }

    @Test
    fun theDayAfterTheLastBundledAyahGivesTheUserQuote() = runTest {
        val bundledAyahCount = ayahSeedSource.load().ayahs.size
        val quoteRepository = OfflineQuoteRepository(
            bundledAyahSeeder = bundledAyahSeeder,
            quoteDao = database.quoteDao(),
        )
        val freeTextDraft = QuoteDraft.FreeTextDraft(text = "free text", reference = "")
        val userQuoteId = quoteRepository.addQuote(freeTextDraft)

        val userQuoteDay = repository.getDailyQuote(epochDay = bundledAyahCount.toLong())

        val expectedQuote = Quote.FreeTextQuote(
            quoteId = userQuoteId,
            origin = QuoteOrigin.USER,
            text = "free text",
            reference = "",
        )
        assertEquals(expectedQuote, userQuoteDay)
    }

    private fun orderedBySurahThenAyah(ayahs: List<Ayah>): List<Ayah> {
        val surahThenAyahOrder = compareBy<Ayah>({ it.surahNumber }, { it.ayahNumber })
        return ayahs.sortedWith(surahThenAyahOrder)
    }
}
