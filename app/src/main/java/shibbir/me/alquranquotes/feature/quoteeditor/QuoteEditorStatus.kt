package shibbir.me.alquranquotes.feature.quoteeditor

/** Whether the quote editor can show its form. */
enum class QuoteEditorStatus {
    /** Loading the quote being edited. */
    LOADING,

    /** The form is showing and can be saved. */
    READY,

    /** The quote being edited could not be loaded, for example because it was deleted. */
    UNAVAILABLE,
}
