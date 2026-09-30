package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

/** Shows the stateless [QuoteEditorScreen] for [uiState] inside the app theme. */
fun ComposeContentTestRule.setQuoteEditorScreen(
    uiState: QuoteEditorUiState,
    editorCallbacks: RecordedQuoteEditorCallbacks = RecordedQuoteEditorCallbacks(),
) {
    setContent {
        AlQuranQuotesTheme {
            QuoteEditorScreen(
                uiState = uiState,
                onBack = { editorCallbacks.backCount += 1 },
                onSave = { editorCallbacks.saveCount += 1 },
                onSelectQuoteKind = { quoteKind ->
                    editorCallbacks.selectedQuoteKinds += quoteKind
                },
                onFieldChange = { quoteEditorField, fieldText ->
                    editorCallbacks.fieldChanges += quoteEditorField to fieldText
                },
            )
        }
    }
}

/** The editor ready to add a quote of [quoteKind], with nothing typed yet. */
fun readyToAddState(quoteKind: QuoteKind = QuoteKind.AYAH) = QuoteEditorUiState(
    isEditing = false,
    status = QuoteEditorStatus.READY,
    form = QuoteEditorForm(quoteKind = quoteKind),
)
