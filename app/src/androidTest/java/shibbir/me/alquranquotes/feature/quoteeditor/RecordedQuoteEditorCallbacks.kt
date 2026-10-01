package shibbir.me.alquranquotes.feature.quoteeditor

/** Records every callback [QuoteEditorScreen] makes, so tests can check them. */
class RecordedQuoteEditorCallbacks {

    var backCount = 0

    var saveCount = 0

    val selectedQuoteKinds = mutableListOf<QuoteKind>()

    val fieldChanges = mutableListOf<Pair<QuoteEditorField, String>>()
}
