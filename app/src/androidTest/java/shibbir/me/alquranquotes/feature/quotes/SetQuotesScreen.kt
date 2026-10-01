package shibbir.me.alquranquotes.feature.quotes

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

/** Shows the stateless [QuotesScreen] for [uiState] inside the app theme. */
fun ComposeContentTestRule.setQuotesScreen(
    uiState: QuotesUiState,
    quotesScreenCallbacks: RecordedQuotesScreenCallbacks = RecordedQuotesScreenCallbacks(),
) {
    setContent {
        AlQuranQuotesTheme {
            QuotesScreen(
                uiState = uiState,
                onAddQuote = { quotesScreenCallbacks.addQuoteCount += 1 },
                onEditQuote = { quoteId -> quotesScreenCallbacks.editedQuoteIds += quoteId },
                onDeleteQuote = { quoteId -> quotesScreenCallbacks.deletedQuoteIds += quoteId },
                onConfirmDelete = { quotesScreenCallbacks.confirmDeleteCount += 1 },
                onDismissDelete = { quotesScreenCallbacks.dismissDeleteCount += 1 },
            )
        }
    }
}
