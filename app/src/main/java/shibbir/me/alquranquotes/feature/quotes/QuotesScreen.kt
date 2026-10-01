package shibbir.me.alquranquotes.feature.quotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.ui.components.TopLevelTopAppBar

/** Stateful entry point: connects [QuotesScreen] to its [QuotesViewModel]. */
@Composable
fun QuotesRoute(
    onAddQuote: () -> Unit,
    onEditQuote: (quoteId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuotesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    QuotesScreen(
        uiState = uiState,
        onAddQuote = onAddQuote,
        onEditQuote = onEditQuote,
        onDeleteQuote = viewModel::requestDelete,
        onConfirmDelete = viewModel::confirmDelete,
        onDismissDelete = viewModel::dismissDelete,
        modifier = modifier,
    )
}

/**
 * Stateless Quotes screen: every quote in a list under a large top app bar that collapses as
 * the list scrolls, an Add button, and a delete confirmation while
 * [QuotesUiState.Loaded.quoteIdPendingDelete] is set. Window insets the caller has already
 * consumed are not applied again.
 */
// The top app bar scroll behavior is still experimental in Material 3 1.4.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    uiState: QuotesUiState,
    onAddQuote: () -> Unit,
    onEditQuote: (quoteId: Long) -> Unit,
    onDeleteQuote: (quoteId: Long) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopLevelTopAppBar(
                title = stringResource(R.string.quotes_title),
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = { AddQuoteButton(listState = listState, onAddQuote = onAddQuote) },
    ) { innerPadding ->
        QuotesBody(
            uiState = uiState,
            listState = listState,
            contentPadding = innerPadding,
            onEditQuote = onEditQuote,
            onDeleteQuote = onDeleteQuote,
            onConfirmDelete = onConfirmDelete,
            onDismissDelete = onDismissDelete,
        )
    }
}

@Composable
private fun QuotesBody(
    uiState: QuotesUiState,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onEditQuote: (quoteId: Long) -> Unit,
    onDeleteQuote: (quoteId: Long) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
) {
    when (uiState) {
        QuotesUiState.Loading -> QuotesLoading(modifier = Modifier.padding(contentPadding))
        is QuotesUiState.Loaded -> {
            QuoteCardList(
                quoteCards = uiState.quoteCards,
                listState = listState,
                contentPadding = contentPadding,
                onEditQuote = onEditQuote,
                onDeleteQuote = onDeleteQuote,
            )
            DeleteConfirmation(
                quoteIdPendingDelete = uiState.quoteIdPendingDelete,
                onConfirmDelete = onConfirmDelete,
                onDismissDelete = onDismissDelete,
            )
        }
    }
}

@Composable
private fun QuotesLoading(modifier: Modifier = Modifier) {
    val loadingDescription = stringResource(R.string.quotes_loading)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = loadingDescription },
        )
    }
}

/**
 * Pads the list inside the Scaffold's padding, so cards scroll under the top app bar and stay
 * clear of the Add button at the end.
 */
@Composable
private fun QuoteCardList(
    quoteCards: List<QuoteListCard>,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onEditQuote: (quoteId: Long) -> Unit,
    onDeleteQuote: (quoteId: Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        state = listState,
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = quoteCards, key = { quoteCard -> quoteCard.quoteId }) { quoteCard ->
            QuoteListCardContent(
                quoteCard = quoteCard,
                onEditQuote = onEditQuote,
                onDeleteQuote = onDeleteQuote,
            )
        }
    }
}

@Composable
private fun DeleteConfirmation(
    quoteIdPendingDelete: Long?,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
) {
    if (quoteIdPendingDelete == null) {
        return
    }
    DeleteQuoteDialog(
        onConfirm = onConfirmDelete,
        onDismiss = onDismissDelete,
    )
}
