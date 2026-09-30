package shibbir.me.alquranquotes.feature.quotes

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.FakeQuoteRepository
import shibbir.me.alquranquotes.testing.MainDispatcherRule

/** Loading and listing. Deleting is in [QuotesViewModelDeleteTest]. */
@OptIn(ExperimentalCoroutinesApi::class)
class QuotesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val quoteRepository = FakeQuoteRepository()

    @Test
    fun showsLoadingUntilTheFirstListArrives() = runTest {
        quoteRepository.quotesGate = CompletableDeferred()
        val viewModel = QuotesViewModel(quoteRepository)
        collectUiState(viewModel)

        assertEquals(QuotesUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun listsEveryQuoteAsACardInTheRepositorysOrder() = runTest {
        val bundledAyah = sampleAyahQuote(quoteId = 1L, origin = QuoteOrigin.BUNDLED)
        val editedAyah = sampleAyahQuote(quoteId = 2L, origin = QuoteOrigin.EDITED_BUNDLED)
        val userFreeText = sampleFreeTextQuote(quoteId = 3L, origin = QuoteOrigin.USER)
        quoteRepository.storeQuote(bundledAyah)
        quoteRepository.storeQuote(editedAyah)
        quoteRepository.storeQuote(userFreeText)
        val viewModel = QuotesViewModel(quoteRepository)
        collectUiState(viewModel)

        val expectedCards = listOf(
            bundledAyah.toQuoteListCard(),
            editedAyah.toQuoteListCard(),
            userFreeText.toQuoteListCard(),
        )
        val expectedState = QuotesUiState.Loaded(
            quoteCards = expectedCards,
            quoteIdPendingDelete = null,
        )
        assertEquals(expectedState, viewModel.uiState.value)
    }

    private fun TestScope.collectUiState(viewModel: QuotesViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }
}
