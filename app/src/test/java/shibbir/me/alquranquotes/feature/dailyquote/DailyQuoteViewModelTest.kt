package shibbir.me.alquranquotes.feature.dailyquote

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import shibbir.me.alquranquotes.testing.MainDispatcherRule
import java.io.IOException

/** Loading, error, and retry behavior. Day changes are in [DailyQuoteViewModelDayChangeTest]. */
@OptIn(ExperimentalCoroutinesApi::class)
class DailyQuoteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixture = DailyQuoteViewModelTestFixture()

    private val dailyQuoteRepository = fixture.dailyQuoteRepository

    private val epochDayProvider = fixture.epochDayProvider

    private val quote = fixture.quote

    private val otherQuote = fixture.otherQuote

    @Test
    fun showsLoadingWhileRepositoryIsSuspendedThenQuote() = runTest {
        epochDayProvider.currentEpochDay = 100L
        dailyQuoteRepository.dailyQuote = quote
        val responseGate = fixture.holdRepositoryResponse()

        val viewModel = fixture.createViewModel()

        assertEquals(listOf(100L), dailyQuoteRepository.requestedEpochDays)
        assertEquals(DailyQuoteUiState.Loading, viewModel.uiState.value)

        responseGate.complete(Unit)
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(quote), viewModel.uiState.value)
    }

    @Test
    fun showsTheUsersAyahQuote() = runTest {
        dailyQuoteRepository.dailyQuote = fixture.userAyahQuote

        val viewModel = fixture.createViewModel()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(fixture.userAyahQuote), viewModel.uiState.value)
    }

    @Test
    fun showsTheUsersFreeTextQuote() = runTest {
        dailyQuoteRepository.dailyQuote = fixture.userFreeTextQuote

        val viewModel = fixture.createViewModel()
        advanceUntilIdle()

        val expectedState = DailyQuoteUiState.Success(fixture.userFreeTextQuote)
        assertEquals(expectedState, viewModel.uiState.value)
    }

    @Test
    fun requestsTheProvidersEpochDay() = runTest {
        dailyQuoteRepository.dailyQuote = quote
        epochDayProvider.currentEpochDay = 20_000L

        fixture.createViewModel()
        advanceUntilIdle()

        assertEquals(listOf(20_000L), dailyQuoteRepository.requestedEpochDays)
    }

    @Test
    fun showsErrorWhenNoQuoteIsAvailable() = runTest {
        dailyQuoteRepository.dailyQuote = null

        val viewModel = fixture.createViewModel()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun showsErrorWhenLoadingFails() = runTest {
        dailyQuoteRepository.failureToThrow = IOException("asset missing")

        val viewModel = fixture.createViewModel()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun showsErrorWhenRepositoryThrowsCancellationWhileTheLoadIsStillActive() = runTest {
        // For example a timeout inside the repository: the load itself was not cancelled.
        dailyQuoteRepository.failureToThrow = CancellationException("timed out")

        val viewModel = fixture.createViewModel()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun retryAfterErrorShowsLoadingThenQuote() = runTest {
        dailyQuoteRepository.failureToThrow = IOException("asset missing")
        val viewModel = fixture.createViewModel()
        advanceUntilIdle()
        assertEquals(DailyQuoteUiState.Error, viewModel.uiState.value)

        dailyQuoteRepository.failureToThrow = null
        dailyQuoteRepository.dailyQuote = quote
        val responseGate = fixture.holdRepositoryResponse()
        viewModel.loadDailyQuote()

        assertEquals(DailyQuoteUiState.Loading, viewModel.uiState.value)

        responseGate.complete(Unit)
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(quote), viewModel.uiState.value)
    }

    @Test
    fun newerLoadWinsOverOlderSuspendedLoad() = runTest {
        dailyQuoteRepository.dailyQuote = otherQuote
        val olderResponseGate = fixture.holdRepositoryResponse()
        val viewModel = fixture.createViewModel()

        dailyQuoteRepository.responseGate = null
        dailyQuoteRepository.dailyQuote = quote
        viewModel.loadDailyQuote()
        advanceUntilIdle()
        assertEquals(DailyQuoteUiState.Success(quote), viewModel.uiState.value)

        olderResponseGate.complete(Unit)
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(quote), viewModel.uiState.value)
    }
}
