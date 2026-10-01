package shibbir.me.alquranquotes.feature.quotes

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

/** Delete with confirmation, for any quote. Loading and listing are in [QuotesViewModelTest]. */
@OptIn(ExperimentalCoroutinesApi::class)
class QuotesViewModelDeleteTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val quoteRepository = FakeQuoteRepository()

    private val bundledAyah = sampleAyahQuote(quoteId = 1L, origin = QuoteOrigin.BUNDLED)

    private val userFreeText = sampleFreeTextQuote(quoteId = 2L, origin = QuoteOrigin.USER)

    init {
        quoteRepository.storeQuote(bundledAyah)
        quoteRepository.storeQuote(userFreeText)
    }

    @Test
    fun requestingDeleteAsksForConfirmationOfThatQuote() = runTest {
        val viewModel = createCollectedViewModel()

        viewModel.requestDelete(userFreeText.quoteId)

        assertEquals(userFreeText.quoteId, loadedState(viewModel).quoteIdPendingDelete)
    }

    @Test
    fun dismissingTheConfirmationKeepsTheQuote() = runTest {
        val viewModel = createCollectedViewModel()
        viewModel.requestDelete(userFreeText.quoteId)

        viewModel.dismissDelete()

        assertEquals(null, loadedState(viewModel).quoteIdPendingDelete)
        assertEquals(emptyList<Long>(), quoteRepository.deletedQuoteIds)
        assertEquals(2, loadedState(viewModel).quoteCards.size)
    }

    @Test
    fun confirmingDeletesTheUsersQuoteAndClosesTheConfirmation() = runTest {
        val viewModel = createCollectedViewModel()
        viewModel.requestDelete(userFreeText.quoteId)

        viewModel.confirmDelete()

        assertEquals(listOf(userFreeText.quoteId), quoteRepository.deletedQuoteIds)
        assertEquals(null, loadedState(viewModel).quoteIdPendingDelete)
        assertEquals(listOf(bundledAyah.toQuoteListCard()), loadedState(viewModel).quoteCards)
    }

    @Test
    fun confirmingDeletesABundledAyah() = runTest {
        val viewModel = createCollectedViewModel()
        viewModel.requestDelete(bundledAyah.quoteId)

        viewModel.confirmDelete()

        assertEquals(listOf(bundledAyah.quoteId), quoteRepository.deletedQuoteIds)
        assertEquals(listOf(userFreeText.toQuoteListCard()), loadedState(viewModel).quoteCards)
    }

    @Test
    fun confirmingWithoutARequestDeletesNothing() = runTest {
        val viewModel = createCollectedViewModel()

        viewModel.confirmDelete()

        assertEquals(emptyList<Long>(), quoteRepository.deletedQuoteIds)
        assertEquals(2, loadedState(viewModel).quoteCards.size)
    }

    private fun TestScope.createCollectedViewModel(): QuotesViewModel {
        val viewModel = QuotesViewModel(quoteRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        return viewModel
    }

    private fun loadedState(viewModel: QuotesViewModel): QuotesUiState.Loaded {
        return viewModel.uiState.value as QuotesUiState.Loaded
    }
}
