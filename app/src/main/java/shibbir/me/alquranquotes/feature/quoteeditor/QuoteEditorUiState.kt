package shibbir.me.alquranquotes.feature.quoteeditor

/** What the quote editor shows. [isEditing] is false when adding a new quote. */
data class QuoteEditorUiState(
    val isEditing: Boolean,
    val status: QuoteEditorStatus,
    val form: QuoteEditorForm = QuoteEditorForm(),
    /** The error shown under each invalid field; empty until the user first taps Save. */
    val fieldErrors: Map<QuoteEditorField, QuoteFieldError> = emptyMap(),
    /** True after the first Save tap; from then on errors follow the user's typing. */
    val hasAttemptedSave: Boolean = false,
    /** True while a save is in progress. */
    val isSaving: Boolean = false,
    /** True when the last save failed; the form stays so the user can try again. */
    val hasSaveFailed: Boolean = false,
    /** True once the quote is saved; the route then leaves the editor. */
    val isSaved: Boolean = false,
) {

    /** Save only works on a loaded form, and only once at a time. */
    val canSave: Boolean
        get() = status == QuoteEditorStatus.READY && !isSaving
}
