package shibbir.me.alquranquotes.feature.dailyquote

import shibbir.me.alquranquotes.model.Quote

/** What the daily quote screen shows. */
sealed interface DailyQuoteUiState {
    data object Loading : DailyQuoteUiState

    data class Success(val quote: Quote) : DailyQuoteUiState

    /** No quote could be shown: either no quotes are available, or loading failed. */
    data object Error : DailyQuoteUiState
}
