package shibbir.me.alquranquotes.feature.dailyquote

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin

/** What the daily quote card shows for each kind and origin of [Quote]. */
class DailyQuoteCardTest {

    // Placeholder text only, so tests never repeat Quran text.
    private val bundledAyah = Quote.AyahQuote(
        quoteId = 1L,
        origin = QuoteOrigin.BUNDLED,
        surahName = "Ash-Sharh",
        surahNumber = 94,
        ayahNumber = 5,
        arabicText = "arabic-text",
        translation = "translation-text",
    )

    private val userFreeText = Quote.FreeTextQuote(
        quoteId = 2L,
        origin = QuoteOrigin.USER,
        text = "user-free-text",
        reference = "user-reference",
    )

    @Test
    fun bundledAyahShowsItsTextsAndReferenceWithoutANote() {
        val dailyQuoteCard = bundledAyah.toDailyQuoteCard()

        val expectedCard = DailyQuoteCard.AyahCard(
            arabicText = "arabic-text",
            translation = "translation-text",
            surahName = "Ash-Sharh",
            surahNumber = 94,
            ayahNumber = 5,
            originNote = null,
        )
        assertEquals(expectedCard, dailyQuoteCard)
    }

    @Test
    fun userAyahCarriesTheYourQuoteNote() {
        val userAyah = bundledAyah.copy(origin = QuoteOrigin.USER)

        val ayahCard = userAyah.toDailyQuoteCard() as DailyQuoteCard.AyahCard

        assertEquals(DailyQuoteOriginNote.YOUR_QUOTE, ayahCard.originNote)
    }

    @Test
    fun editedBundledAyahCarriesTheEditedNote() {
        val editedAyah = bundledAyah.copy(origin = QuoteOrigin.EDITED_BUNDLED)

        val ayahCard = editedAyah.toDailyQuoteCard() as DailyQuoteCard.AyahCard

        assertEquals(DailyQuoteOriginNote.EDITED, ayahCard.originNote)
    }

    @Test
    fun userFreeTextShowsItsTextReferenceAndTheYourQuoteNote() {
        val dailyQuoteCard = userFreeText.toDailyQuoteCard()

        val expectedCard = DailyQuoteCard.FreeTextCard(
            text = "user-free-text",
            reference = "user-reference",
            originNote = DailyQuoteOriginNote.YOUR_QUOTE,
        )
        assertEquals(expectedCard, dailyQuoteCard)
    }

    @Test
    fun bundledFreeTextHasNoNote() {
        val bundledFreeText = userFreeText.copy(origin = QuoteOrigin.BUNDLED)

        val freeTextCard = bundledFreeText.toDailyQuoteCard() as DailyQuoteCard.FreeTextCard

        assertEquals(null, freeTextCard.originNote)
    }

    @Test
    fun editedBundledFreeTextCarriesTheEditedNote() {
        val editedFreeText = userFreeText.copy(origin = QuoteOrigin.EDITED_BUNDLED)

        val freeTextCard = editedFreeText.toDailyQuoteCard() as DailyQuoteCard.FreeTextCard

        assertEquals(DailyQuoteOriginNote.EDITED, freeTextCard.originNote)
    }

    @Test
    fun userFreeTextWithABlankReferenceShowsNoReference() {
        val freeTextWithoutReference = userFreeText.copy(reference = "   ")

        val dailyQuoteCard = freeTextWithoutReference.toDailyQuoteCard()

        val freeTextCard = dailyQuoteCard as DailyQuoteCard.FreeTextCard
        assertEquals(null, freeTextCard.reference)
    }
}
