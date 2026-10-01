package shibbir.me.alquranquotes.feature.quoteeditor

import org.junit.Assert.assertEquals
import org.junit.Test

class QuoteEditorFormTest {

    @Test
    fun settingAFieldChangesOnlyThatField() {
        val emptyForm = QuoteEditorForm()

        for (quoteEditorField in QuoteEditorField.entries) {
            val changedForm = emptyForm.withFieldText(quoteEditorField, "typed")

            for (otherField in QuoteEditorField.entries) {
                val expectedText = expectedFieldText(otherField, quoteEditorField)
                assertEquals(expectedText, changedForm.fieldText(otherField))
            }
        }
    }

    @Test
    fun readsEveryFieldFromItsProperty() {
        val filledForm = QuoteEditorForm(
            surahName = "surah name",
            surahNumber = "2",
            ayahNumber = "7",
            arabicText = "arabic",
            translation = "translation",
            freeText = "free text",
            reference = "reference",
        )

        val fieldTexts = QuoteEditorField.entries.map { filledForm.fieldText(it) }

        val expectedTexts = listOf(
            "surah name",
            "2",
            "7",
            "arabic",
            "translation",
            "free text",
            "reference",
        )
        assertEquals(expectedTexts, fieldTexts)
    }

    private fun expectedFieldText(
        otherField: QuoteEditorField,
        changedField: QuoteEditorField,
    ): String {
        if (otherField == changedField) {
            return "typed"
        }
        return ""
    }
}
