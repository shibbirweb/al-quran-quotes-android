package shibbir.me.alquranquotes.feature.quoteeditor

import shibbir.me.alquranquotes.model.QuoteDraft

/** The outcome of checking a [QuoteEditorForm]. */
sealed interface QuoteValidationResult {

    /** Every field is valid; [quoteDraft] holds the trimmed, parsed values to save. */
    data class Valid(val quoteDraft: QuoteDraft) : QuoteValidationResult

    /** At least one field is not valid; [fieldErrors] holds the error of each such field. */
    data class Invalid(
        val fieldErrors: Map<QuoteEditorField, QuoteFieldError>,
    ) : QuoteValidationResult
}
