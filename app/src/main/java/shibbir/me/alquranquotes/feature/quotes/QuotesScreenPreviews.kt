package shibbir.me.alquranquotes.feature.quotes

import androidx.compose.runtime.Composable
import shibbir.me.alquranquotes.ui.preview.LightDarkLargeFontPreviews
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

// The sample cards are built inside the previews, not in top-level values, so the coverage
// check skips them like every other composable.

@LightDarkLargeFontPreviews
@Composable
private fun QuotesScreenPreview() {
    val loadedState = QuotesUiState.Loaded(
        quoteCards = previewQuoteCards(),
        quoteIdPendingDelete = null,
    )
    QuotesScreenPreviewContent(uiState = loadedState)
}

@LightDarkLargeFontPreviews
@Composable
private fun QuotesScreenDeleteConfirmationPreview() {
    val pendingDeleteState = QuotesUiState.Loaded(
        quoteCards = previewQuoteCards(),
        quoteIdPendingDelete = 1L,
    )
    QuotesScreenPreviewContent(uiState = pendingDeleteState)
}

/** The screen's own Scaffold draws the theme's background. */
@Composable
private fun QuotesScreenPreviewContent(uiState: QuotesUiState) {
    AlQuranQuotesTheme {
        QuotesScreen(
            uiState = uiState,
            onAddQuote = {},
            onEditQuote = {},
            onDeleteQuote = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}

/** Placeholder text only, so previews never repeat Quran text. */
@Composable
private fun previewQuoteCards(): List<QuoteListCard> {
    return listOf(
        previewAyahCard(quoteId = 1L, kind = QuoteListKind.BUNDLED_AYAH),
        previewAyahCard(quoteId = 2L, kind = QuoteListKind.EDITED_BUNDLED_AYAH),
        QuoteListCard.FreeTextCard(
            quoteId = 3L,
            kind = QuoteListKind.USER_FREE_TEXT,
            text = "A quote the user wrote",
            reference = "A reference",
        ),
    )
}

@Composable
private fun previewAyahCard(quoteId: Long, kind: QuoteListKind) = QuoteListCard.AyahCard(
    quoteId = quoteId,
    kind = kind,
    arabicText = "Arabic text",
    translation = "Translation of an ayah",
    surahName = "Surah name",
    surahNumber = 1,
    ayahNumber = quoteId.toInt(),
)
