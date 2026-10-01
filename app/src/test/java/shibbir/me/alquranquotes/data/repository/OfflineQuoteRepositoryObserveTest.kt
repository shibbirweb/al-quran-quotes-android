package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.testAyahDraft
import shibbir.me.alquranquotes.testing.testFreeTextDraft

/** What [OfflineQuoteRepository.observeQuotes] emits, and in which order. */
@OptIn(ExperimentalCoroutinesApi::class)
class OfflineQuoteRepositoryObserveTest {

    private val fixture = OfflineQuoteRepositoryTestFixture()

    /** The seed lists 94:5, 2:153, 13:28, so ids 1, 2, 3 in that order. */
    @Test
    fun seedsThenEmitsBundledAyahsInSurahAndAyahOrder() = runTest {
        val repository = fixture.createRepository()

        val quotes = repository.observeQuotes().first()

        val expectedQuotes = listOf(
            bundledAyahQuote(quoteId = 2L, surahNumber = 2, ayahNumber = 153),
            bundledAyahQuote(quoteId = 3L, surahNumber = 13, ayahNumber = 28),
            bundledAyahQuote(quoteId = 1L, surahNumber = 94, ayahNumber = 5),
        )
        assertEquals(expectedQuotes, quotes)
        assertEquals(1, fixture.ayahSeedSource.loadCount)
    }

    /** The user's ayah has a lower surah number but still comes after every bundled ayah. */
    @Test
    fun emitsBundledAyahsOfBothOriginsThenUserQuotesOfBothKinds() = runTest {
        val repository = OfflineQuoteRepositoryTestFixture(storedSampleTables()).createRepository()
        val userAyahQuoteId = repository.addQuote(testAyahDraft(surahNumber = 1, ayahNumber = 1))

        val quotes = repository.observeQuotes().first()

        val expectedQuotes = listOf(
            editedBundledAyahQuote(),
            bundledAyahQuote(quoteId = 1L, surahNumber = 94, ayahNumber = 5),
            Quote.FreeTextQuote(
                quoteId = 3L,
                origin = QuoteOrigin.USER,
                text = "free text",
                reference = "a reference",
            ),
            userAyahQuote(userAyahQuoteId),
        )
        assertEquals(expectedQuotes, quotes)
    }

    @Test
    fun emitsAgainAfterEveryAddUpdateAndDelete() = runTest {
        val repository = fixture.createRepository()
        val emittedQuoteLists = mutableListOf<List<Quote>>()
        val unconfinedDispatcher = UnconfinedTestDispatcher(testScheduler)
        backgroundScope.launch(unconfinedDispatcher) {
            repository.observeQuotes().toList(emittedQuoteLists)
        }

        val userQuoteId = repository.addQuote(testFreeTextDraft(text = "added"))
        repository.updateQuote(userQuoteId, testFreeTextDraft(text = "updated"))
        repository.deleteQuote(userQuoteId)

        val freeTextsPerEmission = emittedQuoteLists.map { quotes -> freeTexts(quotes) }
        val expectedFreeTexts = listOf(
            emptyList(),
            listOf("added"),
            listOf("updated"),
            emptyList(),
        )
        assertEquals(expectedFreeTexts, freeTextsPerEmission)
    }

    private fun freeTexts(quotes: List<Quote>): List<String> {
        val freeTextQuotes = quotes.filterIsInstance<Quote.FreeTextQuote>()
        return freeTextQuotes.map { freeTextQuote -> freeTextQuote.text }
    }

    private fun editedBundledAyahQuote() = Quote.AyahQuote(
        quoteId = 2L,
        origin = QuoteOrigin.EDITED_BUNDLED,
        surahName = "Surah 13",
        surahNumber = 13,
        ayahNumber = 28,
        arabicText = "arabic-13-28",
        translation = "edited translation",
    )

    private fun userAyahQuote(userAyahQuoteId: Long) = Quote.AyahQuote(
        quoteId = userAyahQuoteId,
        origin = QuoteOrigin.USER,
        surahName = "Surah 1",
        surahNumber = 1,
        ayahNumber = 1,
        arabicText = "user-arabic-1-1",
        translation = "user-translation-1-1",
    )

    private fun bundledAyahQuote(
        quoteId: Long,
        surahNumber: Int,
        ayahNumber: Int,
    ) = Quote.AyahQuote(
        quoteId = quoteId,
        origin = QuoteOrigin.BUNDLED,
        surahName = "Surah $surahNumber",
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        arabicText = "arabic-$surahNumber-$ayahNumber",
        translation = "translation-$surahNumber-$ayahNumber",
    )
}
