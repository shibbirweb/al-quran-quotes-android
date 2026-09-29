package shibbir.me.alquranquotes.feature.dailyquote

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

@Preview
@Composable
private fun DailyQuoteScreenSuccessPreview() {
    DailyQuoteScreenPreviewContent(uiState = DailyQuoteUiState.Success(dailyQuotePreviewAyah))
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DailyQuoteScreenSuccessDarkPreview() {
    DailyQuoteScreenPreviewContent(uiState = DailyQuoteUiState.Success(dailyQuotePreviewAyah))
}

@Preview(fontScale = 2f)
@Composable
private fun DailyQuoteScreenSuccessLargeFontPreview() {
    DailyQuoteScreenPreviewContent(uiState = DailyQuoteUiState.Success(dailyQuotePreviewAyah))
}

@Preview
@Composable
private fun DailyQuoteScreenErrorPreview() {
    DailyQuoteScreenPreviewContent(uiState = DailyQuoteUiState.Error)
}

/** Draws the theme's background behind the screen, which Scaffold does in the app. */
@Composable
private fun DailyQuoteScreenPreviewContent(uiState: DailyQuoteUiState) {
    AlQuranQuotesTheme {
        Surface {
            DailyQuoteScreen(
                uiState = uiState,
                onRetry = {},
            )
        }
    }
}
