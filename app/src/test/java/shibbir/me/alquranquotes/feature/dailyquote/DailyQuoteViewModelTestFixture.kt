package shibbir.me.alquranquotes.feature.dailyquote

import kotlinx.coroutines.CompletableDeferred
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.FakeDailyQuoteRepository
import shibbir.me.alquranquotes.testing.FakeDayChangeSource
import shibbir.me.alquranquotes.testing.FakeEpochDayProvider

/** Fakes, sample quotes, and builders shared by the [DailyQuoteViewModel] test classes. */
class DailyQuoteViewModelTestFixture {

    val dailyQuoteRepository = FakeDailyQuoteRepository()

    val epochDayProvider = FakeEpochDayProvider()

    val dayChangeSource = FakeDayChangeSource()

    // Placeholder text only, so tests never repeat Quran text.
    val quote = Quote.AyahQuote(
        quoteId = 1L,
        origin = QuoteOrigin.BUNDLED,
        surahName = "Ash-Sharh",
        surahNumber = 94,
        ayahNumber = 5,
        arabicText = "arabic-text",
        translation = "translation-text",
    )

    val otherQuote = Quote.AyahQuote(
        quoteId = 2L,
        origin = QuoteOrigin.BUNDLED,
        surahName = "Ar-Ra'd",
        surahNumber = 13,
        ayahNumber = 28,
        arabicText = "other-arabic-text",
        translation = "other-translation",
    )

    val userAyahQuote = Quote.AyahQuote(
        quoteId = 3L,
        origin = QuoteOrigin.USER,
        surahName = "user-surah-name",
        surahNumber = 2,
        ayahNumber = 153,
        arabicText = "user-arabic-text",
        translation = "user-translation",
    )

    val userFreeTextQuote = Quote.FreeTextQuote(
        quoteId = 4L,
        origin = QuoteOrigin.USER,
        text = "user-free-text",
        reference = "user-reference",
    )

    fun createViewModel() = DailyQuoteViewModel(
        dailyQuoteRepository = dailyQuoteRepository,
        epochDayProvider = epochDayProvider,
        dayChangeSource = dayChangeSource,
    )

    /** Makes the fake repository suspend until the returned gate is completed. */
    fun holdRepositoryResponse(): CompletableDeferred<Unit> {
        val responseGate = CompletableDeferred<Unit>()
        dailyQuoteRepository.responseGate = responseGate
        return responseGate
    }
}
