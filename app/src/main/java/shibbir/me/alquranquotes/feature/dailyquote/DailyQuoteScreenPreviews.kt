package shibbir.me.alquranquotes.feature.dailyquote

import androidx.compose.runtime.Composable
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.ui.preview.LightDarkLargeFontPreviews
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

// The sample quotes are built inside the previews, not in top-level values, so the coverage
// check skips them like every other composable.

@LightDarkLargeFontPreviews
@Composable
private fun DailyQuoteScreenBundledAyahPreview() {
    val bundledAyah = dailyQuotePreviewQuote()
    DailyQuoteScreenPreviewContent(uiState = DailyQuoteUiState.Success(bundledAyah))
}

/** Reuses the bundled sample's text, so no Quran text is typed here. */
@LightDarkLargeFontPreviews
@Composable
private fun DailyQuoteScreenUserAyahPreview() {
    val userAyah = dailyQuotePreviewQuote().copy(origin = QuoteOrigin.USER)
    DailyQuoteScreenPreviewContent(uiState = DailyQuoteUiState.Success(userAyah))
}

@LightDarkLargeFontPreviews
@Composable
private fun DailyQuoteScreenUserFreeTextPreview() {
    val userFreeText = Quote.FreeTextQuote(
        quoteId = 2L,
        origin = QuoteOrigin.USER,
        text = "Write down one thing you are grateful for today.",
        reference = "My notes",
    )
    DailyQuoteScreenPreviewContent(uiState = DailyQuoteUiState.Success(userFreeText))
}

@LightDarkLargeFontPreviews
@Composable
private fun DailyQuoteScreenErrorPreview() {
    DailyQuoteScreenPreviewContent(uiState = DailyQuoteUiState.Error)
}

/** The screen's own Scaffold draws the theme's background. */
@Composable
private fun DailyQuoteScreenPreviewContent(uiState: DailyQuoteUiState) {
    AlQuranQuotesTheme {
        DailyQuoteScreen(
            uiState = uiState,
            onRetry = {},
        )
    }
}
