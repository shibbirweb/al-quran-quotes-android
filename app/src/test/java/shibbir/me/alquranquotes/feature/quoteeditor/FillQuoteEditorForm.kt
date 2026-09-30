package shibbir.me.alquranquotes.feature.quoteeditor

// Placeholder text only, so tests never repeat Quran text.

/** Types a valid ayah into the editor, as the user would. */
fun QuoteEditorViewModel.fillAyahForm() {
    selectQuoteKind(QuoteKind.AYAH)
    updateField(QuoteEditorField.SURAH_NAME, "User Surah")
    updateField(QuoteEditorField.SURAH_NUMBER, "2")
    updateField(QuoteEditorField.AYAH_NUMBER, "7")
    updateField(QuoteEditorField.ARABIC_TEXT, "user-arabic")
    updateField(QuoteEditorField.TRANSLATION, "user-translation")
}

/** Types a valid free text quote into the editor, as the user would. */
fun QuoteEditorViewModel.fillFreeTextForm(reference: String = "a reference") {
    selectQuoteKind(QuoteKind.FREE_TEXT)
    updateField(QuoteEditorField.FREE_TEXT, "free text")
    updateField(QuoteEditorField.REFERENCE, reference)
}
