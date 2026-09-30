package shibbir.me.alquranquotes.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
 * Checks [OfflineQuoteRepository] on Room with the real bundled asset: the bundled ayahs come
 * first, then the user's quotes, and every quote can be read back, updated, and deleted.
 * Expectations come from the asset itself, so the test never repeats Quran text.
 */
@RunWith(AndroidJUnit4::class)
class OfflineQuoteRepositoryDeviceTest {

    private val database = createInMemoryQuranDatabase()

    private val ayahSeedSource = AssetAyahSeedSource(
        context = InstrumentationRegistry.getInstrumentation().targetContext,
        ioDispatcher = Dispatchers.IO,
    )

    private val repository = OfflineQuoteRepository(
        bundledAyahSeeder = BundledAyahSeeder(database.bundledAyahSeedDao(), ayahSeedSource),
        quoteDao = database.quoteDao(),
    )

    private val ayahDraft = QuoteDraft.AyahDraft(
        surahName = "Surah 94",
        surahNumber = 94,
        ayahNumber = 5,
        arabicText = "user-arabic-text",
        translation = "user-translation",
    )

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun observeQuotesListsBundledAyahsInOrderThenUserQuotes() = runTest {
        val userQuoteId = repository.addQuote(ayahDraft)

        val quotes = repository.observeQuotes().first()

        val orderedSeedAyahs = orderedBySurahThenAyah(ayahSeedSource.load().ayahs)
        val bundledAyahNumbers = orderedSeedAyahs.map { it.surahNumber to it.ayahNumber }
        val bundledQuotes = quotes.dropLast(1).filterIsInstance<Quote.AyahQuote>()
        val userQuote = quotes.last()
        assertEquals(bundledAyahNumbers, bundledQuotes.map { it.surahNumber to it.ayahNumber })
        assertTrue(bundledQuotes.all { it.origin == QuoteOrigin.BUNDLED })
        assertEquals(userQuoteId, userQuote.quoteId)
        assertEquals(QuoteOrigin.USER, userQuote.origin)
    }

    @Test
    fun bundledAyahCanBeEditedAndDeleted() = runTest {
        val firstBundledQuote = repository.observeQuotes().first().first()

        val bundledQuoteId = firstBundledQuote.quoteId

        repository.updateQuote(bundledQuoteId, ayahDraft)
        val editedQuotes = repository.observeQuotes().first()
        val editedDraft = repository.getQuoteDraft(bundledQuoteId)
        repository.deleteQuote(bundledQuoteId)
        val deletedDraft = repository.getQuoteDraft(bundledQuoteId)

        val editedQuote = editedQuotes.single { quote -> quote.quoteId == bundledQuoteId }
        assertEquals(QuoteOrigin.EDITED_BUNDLED, editedQuote.origin)
        assertEquals(ayahDraft, editedDraft)
        assertNull(deletedDraft)
    }

    @Test
    fun userQuoteCanBeReadUpdatedAndDeleted() = runTest {
        val userQuoteId = repository.addQuote(ayahDraft)
        val updatedDraft = ayahDraft.copy(translation = "updated")

        val savedDraft = repository.getQuoteDraft(userQuoteId)
        repository.updateQuote(userQuoteId, updatedDraft)
        val storedUpdatedDraft = repository.getQuoteDraft(userQuoteId)
        repository.deleteQuote(userQuoteId)
        val deletedDraft = repository.getQuoteDraft(userQuoteId)

        assertEquals(ayahDraft, savedDraft)
        assertEquals(updatedDraft, storedUpdatedDraft)
        assertNull(deletedDraft)
    }

    private fun orderedBySurahThenAyah(ayahs: List<Ayah>): List<Ayah> {
        val surahThenAyahOrder = compareBy<Ayah>({ it.surahNumber }, { it.ayahNumber })
        return ayahs.sortedWith(surahThenAyahOrder)
    }
}
