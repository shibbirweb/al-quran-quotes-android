package shibbir.me.alquranquotes.feature.quotes

/** Records every callback [QuotesScreen] makes, so tests can check them. */
class RecordedQuotesScreenCallbacks {

    var addQuoteCount = 0

    val editedQuoteIds = mutableListOf<Long>()

    val deletedQuoteIds = mutableListOf<Long>()

    var confirmDeleteCount = 0

    var dismissDeleteCount = 0
}
