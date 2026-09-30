package shibbir.me.alquranquotes.feature.dailyquote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.ui.components.TopLevelTopAppBar

/** Stateful entry point: connects [DailyQuoteScreen] to its [DailyQuoteViewModel]. */
@Composable
fun DailyQuoteRoute(
    modifier: Modifier = Modifier,
    viewModel: DailyQuoteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Day change events can be missed or delayed while the app is in the background, so also
    // check the day on every resume.
    LifecycleResumeEffect(key1 = viewModel) {
        viewModel.refreshIfDayChanged()
        onPauseOrDispose {
            // Nothing to release: a load in progress keeps running in the ViewModel.
        }
    }
    DailyQuoteScreen(
        uiState = uiState,
        onRetry = viewModel::loadDailyQuote,
        modifier = modifier,
    )
}

/**
 * Stateless daily quote screen: shows [uiState] under a large top app bar that collapses as the
 * quote scrolls, and reports Retry taps through [onRetry]. Window insets the caller has already
 * consumed are not applied again.
 */
// The top app bar scroll behavior is still experimental in Material 3 1.4.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyQuoteScreen(
    uiState: DailyQuoteUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopLevelTopAppBar(
                title = stringResource(R.string.app_name),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        DailyQuoteBody(
            uiState = uiState,
            onRetry = onRetry,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun DailyQuoteBody(
    uiState: DailyQuoteUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            DailyQuoteUiState.Loading -> DailyQuoteLoading()
            DailyQuoteUiState.Error -> DailyQuoteError(onRetry = onRetry)
            is DailyQuoteUiState.Success -> DailyQuoteContent(quote = uiState.quote)
        }
    }
}

@Composable
private fun DailyQuoteLoading() {
    val loadingDescription = stringResource(R.string.daily_quote_loading)
    CircularProgressIndicator(
        modifier = Modifier.semantics { contentDescription = loadingDescription },
    )
}

/** Scrolls, so long quotes stay readable with large fonts and the top app bar collapses. */
@Composable
private fun DailyQuoteContent(quote: Quote) {
    val dailyQuoteCard = quote.toDailyQuoteCard()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        DailyQuoteCardContent(dailyQuoteCard = dailyQuoteCard)
    }
}

/** Scrolls, so Retry stays reachable with large fonts in landscape. */
@Composable
private fun DailyQuoteError(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DailyQuoteErrorMessage()
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.daily_quote_retry))
        }
    }
}

@Composable
private fun DailyQuoteErrorMessage() {
    Text(
        text = stringResource(R.string.daily_quote_error),
        // Polite, so TalkBack announces the error without interrupting other speech.
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )
}
