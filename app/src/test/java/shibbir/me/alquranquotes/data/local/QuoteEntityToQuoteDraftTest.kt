package shibbir.me.alquranquotes.data.local

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.model.QuoteOrigin

/** How a stored [QuoteEntity] row maps back to the fields the quote editor shows. */
class QuoteEntityToQuoteDraftTest {

    @Test
    fun editedBundledAyahRowMapsToItsAyahDraft() {
        val editedBundledAyahRow = QuoteEntity(
            id = 2L,
            origin = QuoteOrigin.EDITED_BUNDLED,
            type = QuoteType.AYAH,
            bundledKey = "13:28",
            surahName = "Surah 13",
            surahNumber = 13,
            ayahNumber = 28,
            arabicText = "edited-arabic",
            translation = "edited-translation",
        )

        val quoteDraft = editedBundledAyahRow.toQuoteDraft()

        val expectedQuoteDraft = QuoteDraft.AyahDraft(
            surahName = "Surah 13",
            surahNumber = 13,
            ayahNumber = 28,
            arabicText = "edited-arabic",
            translation = "edited-translation",
        )
        assertEquals(expectedQuoteDraft, quoteDraft)
    }

    @Test
    fun userFreeTextRowMapsToItsFreeTextDraft() {
        val userFreeTextRow = QuoteEntity(
            id = 4L,
            origin = QuoteOrigin.USER,
            type = QuoteType.FREE_TEXT,
            freeText = "gratitude",
            reference = "a book",
        )

        val quoteDraft = userFreeTextRow.toQuoteDraft()

        val expectedQuoteDraft = QuoteDraft.FreeTextDraft(text = "gratitude", reference = "a book")
        assertEquals(expectedQuoteDraft, quoteDraft)
    }
}
