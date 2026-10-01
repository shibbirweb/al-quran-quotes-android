package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.ui.preview.LightDarkLargeFontPreviews
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

/**
 * Stateful entry point: connects [QuoteEditorScreen] to its [QuoteEditorViewModel] and calls
 * [onDone] once the quote is saved or when the user taps back in the top app bar.
 */
@Composable
fun QuoteEditorRoute(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuoteEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnDone by rememberUpdatedState(onDone)
    val isSaved = uiState.isSaved
    LaunchedEffect(isSaved) {
        if (isSaved) {
            currentOnDone()
        }
    }
    QuoteEditorScreen(
        uiState = uiState,
        onBack = onDone,
        onSave = viewModel::save,
        onSelectQuoteKind = viewModel::selectQuoteKind,
        onFieldChange = viewModel::updateField,
        modifier = modifier,
    )
}

/**
 * Stateless, full screen quote editor for adding a new quote or editing any quote, bundled or
 * the user's own. The top app bar stays pinned and tints while the form scrolls under it.
 */
// The top app bar scroll behavior is still experimental in Material 3 1.4.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteEditorScreen(
    uiState: QuoteEditorUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onSelectQuoteKind: (QuoteKind) -> Unit,
    onFieldChange: (QuoteEditorField, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            QuoteEditorTopAppBar(
                isEditing = uiState.isEditing,
                canSave = uiState.canSave,
                onBack = onBack,
                onSave = onSave,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        val bodyModifier = Modifier.padding(innerPadding)
        when (uiState.status) {
            QuoteEditorStatus.LOADING -> QuoteEditorLoading(modifier = bodyModifier)
            QuoteEditorStatus.UNAVAILABLE -> QuoteEditorUnavailable(modifier = bodyModifier)
            QuoteEditorStatus.READY -> QuoteEditorFormContent(
                uiState = uiState,
                onSelectQuoteKind = onSelectQuoteKind,
                onFieldChange = onFieldChange,
                innerPadding = innerPadding,
            )
        }
    }
}

@Composable
private fun QuoteEditorLoading(modifier: Modifier = Modifier) {
    val loadingDescription = stringResource(R.string.quote_editor_loading)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = loadingDescription },
        )
    }
}

@Composable
private fun QuoteEditorUnavailable(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.quote_editor_unavailable),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@LightDarkLargeFontPreviews
@Composable
private fun QuoteEditorScreenAddPreview() {
    val addingState = QuoteEditorUiState(isEditing = false, status = QuoteEditorStatus.READY)
    QuoteEditorScreenPreviewContent(uiState = addingState)
}

/** Placeholder text only, so previews never repeat Quran text. */
@LightDarkLargeFontPreviews
@Composable
private fun QuoteEditorScreenEditPreview() {
    val editedForm = QuoteEditorForm(
        quoteKind = QuoteKind.FREE_TEXT,
        freeText = "A quote the user wrote",
        reference = "A reference",
    )
    val editingState = QuoteEditorUiState(
        isEditing = true,
        status = QuoteEditorStatus.READY,
        form = editedForm,
    )
    QuoteEditorScreenPreviewContent(uiState = editingState)
}

/** The screen's own Scaffold draws the theme's background. */
@Composable
private fun QuoteEditorScreenPreviewContent(uiState: QuoteEditorUiState) {
    AlQuranQuotesTheme {
        QuoteEditorScreen(
            uiState = uiState,
            onBack = {},
            onSave = {},
            onSelectQuoteKind = {},
            onFieldChange = { _, _ -> },
        )
    }
}
