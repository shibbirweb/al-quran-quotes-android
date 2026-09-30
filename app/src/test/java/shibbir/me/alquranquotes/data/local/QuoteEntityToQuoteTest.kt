package shibbir.me.alquranquotes.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin

/** How a stored [QuoteEntity] row maps to the [Quote] shown in the app. */
class QuoteEntityToQuoteTest {

    @Test
    fun bundledAyahRowMapsToAyahQuoteWithItsIdAndOrigin() {
        val quote = bundledAyahRow().toQuote()

        assertEquals(expectedBundledAyahQuote(), quote)
    }

    private fun bundledAyahRow() = QuoteEntity(
        id = 3L,
        origin = QuoteOrigin.BUNDLED,
        type = QuoteType.AYAH,
        bundledKey = "94:5",
        surahName = "Surah 94",
        surahNumber = 94,
        ayahNumber = 5,
        arabicText = "arabic-94-5",
        translation = "translation-94-5",
    )

    private fun expectedBundledAyahQuote() = Quote.AyahQuote(
        quoteId = 3L,
        origin = QuoteOrigin.BUNDLED,
        surahName = "Surah 94",
        surahNumber = 94,
        ayahNumber = 5,
        arabicText = "arabic-94-5",
        translation = "translation-94-5",
    )

    @Test
    fun userFreeTextRowMapsToFreeTextQuoteWithItsIdAndOrigin() {
        val userFreeTextRow = QuoteEntity(
            id = 8L,
            origin = QuoteOrigin.USER,
            type = QuoteType.FREE_TEXT,
            freeText = "patience",
            reference = "",
        )

        val quote = userFreeTextRow.toQuote()

        val expectedQuote = Quote.FreeTextQuote(
            quoteId = 8L,
            origin = QuoteOrigin.USER,
            text = "patience",
            reference = "",
        )
        assertEquals(expectedQuote, quote)
    }

    @Test
    fun ayahRowWithoutSurahNameFailsWithTheMissingColumn() {
        val brokenAyahRow = QuoteEntity(id = 5L, origin = QuoteOrigin.USER, type = QuoteType.AYAH)

        val mappingFailure = assertThrows(IllegalStateException::class.java) {
            brokenAyahRow.toQuote()
        }

        assertEquals("surah_name is null in AYAH quote 5", mappingFailure.message)
    }
}
