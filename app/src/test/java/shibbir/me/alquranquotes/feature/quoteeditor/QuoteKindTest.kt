package shibbir.me.alquranquotes.feature.quoteeditor

import org.junit.Assert.assertEquals
import org.junit.Test

class QuoteKindTest {

    @Test
    fun ayahShowsItsFieldsInReadingOrder() {
        val expectedFields = listOf(
            QuoteEditorField.SURAH_NAME,
            QuoteEditorField.SURAH_NUMBER,
            QuoteEditorField.AYAH_NUMBER,
            QuoteEditorField.ARABIC_TEXT,
            QuoteEditorField.TRANSLATION,
        )
        assertEquals(expectedFields, QuoteKind.AYAH.editorFields)
    }

    @Test
    fun freeTextShowsTextThenReference() {
        val expectedFields = listOf(QuoteEditorField.FREE_TEXT, QuoteEditorField.REFERENCE)
        assertEquals(expectedFields, QuoteKind.FREE_TEXT.editorFields)
    }
}
