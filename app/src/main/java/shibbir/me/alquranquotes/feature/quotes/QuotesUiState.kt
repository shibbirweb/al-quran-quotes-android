package shibbir.me.alquranquotes.feature.quotes

/** What the Quotes screen shows. */
sealed interface QuotesUiState {

    /** Waiting for the first list of quotes. */
    data object Loading : QuotesUiState

    /**
     * Every quote, in the repository's order. [quoteIdPendingDelete] is the quote whose delete
     * confirmation is showing, or null when none is.
     */
    data class Loaded(
        val quoteCards: List<QuoteListCard>,
        val quoteIdPendingDelete: Long?,
    ) : QuotesUiState
}
