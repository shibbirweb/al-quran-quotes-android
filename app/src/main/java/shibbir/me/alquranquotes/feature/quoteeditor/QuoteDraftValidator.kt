package shibbir.me.alquranquotes.feature.quoteeditor

import shibbir.me.alquranquotes.model.QuoteDraft
import javax.inject.Inject

/** Checks a [QuoteEditorForm] and turns a valid one into a [QuoteDraft]. */
class QuoteDraftValidator @Inject constructor() {

    fun validate(quoteEditorForm: QuoteEditorForm): QuoteValidationResult {
        val fieldErrors = fieldErrorsOf(quoteEditorForm)
        if (fieldErrors.isNotEmpty()) {
            return QuoteValidationResult.Invalid(fieldErrors)
        }
        val quoteDraft = when (quoteEditorForm.quoteKind) {
            QuoteKind.AYAH -> ayahDraftOf(quoteEditorForm)
            QuoteKind.FREE_TEXT -> freeTextDraftOf(quoteEditorForm)
        }
        return QuoteValidationResult.Valid(quoteDraft)
    }

    /** The error of each field of the form's kind that is not valid; empty when all are. */
    fun fieldErrorsOf(quoteEditorForm: QuoteEditorForm): Map<QuoteEditorField, QuoteFieldError> {
        return when (quoteEditorForm.quoteKind) {
            QuoteKind.AYAH -> ayahFieldErrors(quoteEditorForm)
            QuoteKind.FREE_TEXT -> freeTextFieldErrors(quoteEditorForm)
        }
    }

    private fun ayahFieldErrors(quoteEditorForm: QuoteEditorForm) = collectFieldErrors(
        QuoteEditorField.SURAH_NAME to requiredTextError(quoteEditorForm.surahName),
        QuoteEditorField.SURAH_NUMBER to surahNumberError(quoteEditorForm.surahNumber),
        QuoteEditorField.AYAH_NUMBER to ayahNumberError(quoteEditorForm.ayahNumber),
        QuoteEditorField.ARABIC_TEXT to requiredTextError(quoteEditorForm.arabicText),
        QuoteEditorField.TRANSLATION to requiredTextError(quoteEditorForm.translation),
    )

    private fun freeTextFieldErrors(quoteEditorForm: QuoteEditorForm) = collectFieldErrors(
        QuoteEditorField.FREE_TEXT to requiredTextError(quoteEditorForm.freeText),
    )

    private fun surahNumberError(surahNumberText: String): QuoteFieldError? {
        val numberError = numberError(surahNumberText)
        if (numberError != null) {
            return numberError
        }
        val surahNumber = surahNumberText.trim().toInt()
        if (surahNumber !in FIRST_SURAH_NUMBER..LAST_SURAH_NUMBER) {
            return QuoteFieldError.SURAH_NUMBER_OUT_OF_RANGE
        }
        return null
    }

    private fun ayahNumberError(ayahNumberText: String): QuoteFieldError? {
        val numberError = numberError(ayahNumberText)
        if (numberError != null) {
            return numberError
        }
        val ayahNumber = ayahNumberText.trim().toInt()
        if (ayahNumber < FIRST_AYAH_NUMBER) {
            return QuoteFieldError.AYAH_NUMBER_TOO_SMALL
        }
        return null
    }

    private fun numberError(numberText: String): QuoteFieldError? {
        val requiredError = requiredTextError(numberText)
        if (requiredError != null) {
            return requiredError
        }
        val number = numberText.trim().toIntOrNull()
        if (number == null) {
            return QuoteFieldError.NOT_A_NUMBER
        }
        return null
    }

    private fun requiredTextError(fieldText: String): QuoteFieldError? {
        if (fieldText.isBlank()) {
            return QuoteFieldError.REQUIRED
        }
        return null
    }

    private fun ayahDraftOf(quoteEditorForm: QuoteEditorForm) = QuoteDraft.AyahDraft(
        surahName = quoteEditorForm.surahName.trim(),
        surahNumber = quoteEditorForm.surahNumber.trim().toInt(),
        ayahNumber = quoteEditorForm.ayahNumber.trim().toInt(),
        arabicText = quoteEditorForm.arabicText.trim(),
        translation = quoteEditorForm.translation.trim(),
    )

    private fun freeTextDraftOf(quoteEditorForm: QuoteEditorForm) = QuoteDraft.FreeTextDraft(
        text = quoteEditorForm.freeText.trim(),
        reference = quoteEditorForm.reference.trim(),
    )

    private companion object {
        const val FIRST_SURAH_NUMBER = 1
        const val LAST_SURAH_NUMBER = 114
        const val FIRST_AYAH_NUMBER = 1
    }
}

/** Keeps only the checks that found an error. */
private fun collectFieldErrors(
    vararg fieldChecks: Pair<QuoteEditorField, QuoteFieldError?>,
): Map<QuoteEditorField, QuoteFieldError> {
    val fieldErrors = mutableMapOf<QuoteEditorField, QuoteFieldError>()
    for ((quoteEditorField, quoteFieldError) in fieldChecks) {
        if (quoteFieldError != null) {
            fieldErrors[quoteEditorField] = quoteFieldError
        }
    }
    return fieldErrors
}
