package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import shibbir.me.alquranquotes.R

/**
 * The editor form: the kind picker (only when adding), the fields of the chosen kind, and a
 * message when saving failed. Scrolls and stays above the keyboard, so every field stays
 * reachable with large fonts.
 */
@Composable
internal fun QuoteEditorFormContent(
    uiState: QuoteEditorUiState,
    onSelectQuoteKind: (QuoteKind) -> Unit,
    onFieldChange: (QuoteEditorField, String) -> Unit,
    innerPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (!uiState.isEditing) {
            QuoteKindPicker(
                selectedQuoteKind = uiState.form.quoteKind,
                onSelectQuoteKind = onSelectQuoteKind,
            )
        }
        QuoteEditorFields(
            form = uiState.form,
            fieldErrors = uiState.fieldErrors,
            onFieldChange = onFieldChange,
        )
        if (uiState.hasSaveFailed) {
            SaveFailedMessage()
        }
    }
}

// Segmented buttons are still experimental in Material 3 1.4.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuoteKindPicker(
    selectedQuoteKind: QuoteKind,
    onSelectQuoteKind: (QuoteKind) -> Unit,
) {
    val quoteKinds = QuoteKind.entries
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        quoteKinds.forEachIndexed { index, quoteKind ->
            SegmentedButton(
                selected = quoteKind == selectedQuoteKind,
                onClick = { onSelectQuoteKind(quoteKind) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = quoteKinds.size),
            ) {
                Text(text = stringResource(quoteKind.labelResId))
            }
        }
    }
}

@Composable
private fun SaveFailedMessage() {
    Text(
        text = stringResource(R.string.quote_editor_save_failed),
        // Polite, so TalkBack announces the failure without interrupting other speech.
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
    )
}
