package shibbir.me.alquranquotes.data.local

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.testAyahDraft
import shibbir.me.alquranquotes.testing.testFreeTextDraft

/** How the fields from the quote editor map to a [QuoteEntity] row. */
class QuoteDraftToQuoteEntityTest {

    @Test
    fun newAyahDraftMapsToAyahRowWithOnlyAyahColumns() {
        val ayahDraft = testAyahDraft(surahNumber = 2, ayahNumber = 153)

        val quoteEntity = ayahDraft.toQuoteEntity(origin = QuoteOrigin.USER)

        val expectedQuoteEntity = QuoteEntity(
            id = 0L,
            origin = QuoteOrigin.USER,
            type = QuoteType.AYAH,
            bundledKey = null,
            surahName = "Surah 2",
            surahNumber = 2,
            ayahNumber = 153,
            arabicText = "user-arabic-2-153",
            translation = "user-translation-2-153",
            freeText = null,
            reference = null,
        )
        assertEquals(expectedQuoteEntity, quoteEntity)
    }

    @Test
    fun freeTextDraftMapsToFreeTextRowWithOnlyFreeTextColumns() {
        val freeTextDraft = testFreeTextDraft(text = "patience", reference = "")

        val quoteEntity = freeTextDraft.toQuoteEntity(origin = QuoteOrigin.USER)

        val expectedQuoteEntity = QuoteEntity(
            id = 0L,
            origin = QuoteOrigin.USER,
            type = QuoteType.FREE_TEXT,
            freeText = "patience",
            reference = "",
        )
        assertEquals(expectedQuoteEntity, quoteEntity)
    }
}
