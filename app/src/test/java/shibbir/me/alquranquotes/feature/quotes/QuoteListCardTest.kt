package shibbir.me.alquranquotes.feature.quotes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteOrigin

class QuoteListCardTest {

    @Test
    fun bundledAyahBecomesAnAyahCardWithItsQuoteId() {
        val bundledAyah = sampleAyahQuote(quoteId = 5L, origin = QuoteOrigin.BUNDLED)

        val quoteListCard = bundledAyah.toQuoteListCard()

        val expectedCard = QuoteListCard.AyahCard(
            quoteId = 5L,
            kind = QuoteListKind.BUNDLED_AYAH,
            arabicText = "arabic-94-5",
            translation = "translation-94-5",
            surahName = "Surah 94",
            surahNumber = 94,
            ayahNumber = 5,
        )
        assertEquals(expectedCard, quoteListCard)
    }

    @Test
    fun userAyahBecomesAnAyahCardOfTheUsersKind() {
        val userAyah = sampleAyahQuote(quoteId = 12L, origin = QuoteOrigin.USER)

        val ayahCard = userAyah.toQuoteListCard() as QuoteListCard.AyahCard

        assertEquals(12L, ayahCard.quoteId)
        assertEquals(QuoteListKind.USER_AYAH, ayahCard.kind)
    }

    @Test
    fun editedBundledAyahBecomesAnAyahCardOfTheEditedKind() {
        val editedAyah = sampleAyahQuote(quoteId = 6L, origin = QuoteOrigin.EDITED_BUNDLED)

        val ayahCard = editedAyah.toQuoteListCard() as QuoteListCard.AyahCard

        assertEquals(6L, ayahCard.quoteId)
        assertEquals(QuoteListKind.EDITED_BUNDLED_AYAH, ayahCard.kind)
    }

    @Test
    fun freeTextBecomesAFreeTextCardWithItsReference() {
        val userFreeText = sampleFreeTextQuote(quoteId = 3L, origin = QuoteOrigin.USER)

        val quoteListCard = userFreeText.toQuoteListCard()

        val expectedCard = QuoteListCard.FreeTextCard(
            quoteId = 3L,
            kind = QuoteListKind.USER_FREE_TEXT,
            text = "free text",
            reference = "a reference",
        )
        assertEquals(expectedCard, quoteListCard)
    }

    @Test
    fun blankFreeTextReferenceIsLeftOut() {
        val userFreeText = sampleFreeTextQuote(reference = "  ")

        val freeTextCard = userFreeText.toQuoteListCard() as QuoteListCard.FreeTextCard

        assertNull(freeTextCard.reference)
    }
}
