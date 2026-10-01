package shibbir.me.alquranquotes.feature.quoteeditor

import shibbir.me.alquranquotes.model.QuoteDraft

/**
 * What the user has typed into the quote editor, exactly as typed. Only the fields of
 * [quoteKind] are saved; the others keep their text while the user switches kinds.
 */
data class QuoteEditorForm(
    val quoteKind: QuoteKind = QuoteKind.AYAH,
    val surahName: String = "",
    val surahNumber: String = "",
    val ayahNumber: String = "",
    val arabicText: String = "",
    val translation: String = "",
    val freeText: String = "",
    val reference: String = "",
)

/** The text of [quoteEditorField], as typed. */
fun QuoteEditorForm.fieldText(quoteEditorField: QuoteEditorField): String {
    return when (quoteEditorField) {
        QuoteEditorField.SURAH_NAME -> surahName
        QuoteEditorField.SURAH_NUMBER -> surahNumber
        QuoteEditorField.AYAH_NUMBER -> ayahNumber
        QuoteEditorField.ARABIC_TEXT -> arabicText
        QuoteEditorField.TRANSLATION -> translation
        QuoteEditorField.FREE_TEXT -> freeText
        QuoteEditorField.REFERENCE -> reference
    }
}

/** A copy of this form with [fieldText] typed into [quoteEditorField]. */
fun QuoteEditorForm.withFieldText(
    quoteEditorField: QuoteEditorField,
    fieldText: String,
): QuoteEditorForm {
    return when (quoteEditorField) {
        QuoteEditorField.SURAH_NAME -> copy(surahName = fieldText)
        QuoteEditorField.SURAH_NUMBER -> copy(surahNumber = fieldText)
        QuoteEditorField.AYAH_NUMBER -> copy(ayahNumber = fieldText)
        QuoteEditorField.ARABIC_TEXT -> copy(arabicText = fieldText)
        QuoteEditorField.TRANSLATION -> copy(translation = fieldText)
        QuoteEditorField.FREE_TEXT -> copy(freeText = fieldText)
        QuoteEditorField.REFERENCE -> copy(reference = fieldText)
    }
}

/** The form for editing a saved quote, with its numbers as text. */
fun QuoteDraft.toQuoteEditorForm(): QuoteEditorForm {
    return when (this) {
        is QuoteDraft.AyahDraft -> QuoteEditorForm(
            quoteKind = QuoteKind.AYAH,
            surahName = surahName,
            surahNumber = surahNumber.toString(),
            ayahNumber = ayahNumber.toString(),
            arabicText = arabicText,
            translation = translation,
        )
        is QuoteDraft.FreeTextDraft -> QuoteEditorForm(
            quoteKind = QuoteKind.FREE_TEXT,
            freeText = text,
            reference = reference,
        )
    }
}
