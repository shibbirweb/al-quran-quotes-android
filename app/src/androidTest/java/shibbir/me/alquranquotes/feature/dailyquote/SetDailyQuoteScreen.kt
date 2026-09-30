package shibbir.me.alquranquotes.feature.dailyquote

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

/** Shows the stateless [DailyQuoteScreen] for [uiState] inside the app theme. */
fun ComposeContentTestRule.setDailyQuoteScreen(
    uiState: DailyQuoteUiState,
    onRetry: () -> Unit = {},
) {
    setContent {
        AlQuranQuotesTheme {
            DailyQuoteScreen(
                uiState = uiState,
                onRetry = onRetry,
            )
        }
    }
}
