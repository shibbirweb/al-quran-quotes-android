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

    private val ayahRepository = fixture.ayahRepository

    private val epochDayProvider = fixture.epochDayProvider

    private val ayah = fixture.ayah

    private val otherAyah = fixture.otherAyah

    @Test
    fun showsLoadingWhileRepositoryIsSuspendedThenAyah() = runTest {
        epochDayProvider.currentEpochDay = 100L
        ayahRepository.dailyAyah = ayah
        val responseGate = fixture.holdRepositoryResponse()

        val viewModel = fixture.createViewModel()

        assertEquals(listOf(100L), ayahRepository.requestedEpochDays)
        assertEquals(DailyQuoteUiState.Loading, viewModel.uiState.value)

        responseGate.complete(Unit)
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(ayah), viewModel.uiState.value)
    }

    @Test
    fun requestsTheProvidersEpochDay() = runTest {
        ayahRepository.dailyAyah = ayah
        epochDayProvider.currentEpochDay = 20_000L

        fixture.createViewModel()
        advanceUntilIdle()

        assertEquals(listOf(20_000L), ayahRepository.requestedEpochDays)
    }

    @Test
    fun showsErrorWhenNoAyahIsAvailable() = runTest {
        ayahRepository.dailyAyah = null

        val viewModel = fixture.createViewModel()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun showsErrorWhenLoadingFails() = runTest {
        ayahRepository.failureToThrow = IOException("asset missing")

        val viewModel = fixture.createViewModel()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun showsErrorWhenRepositoryThrowsCancellationWhileTheLoadIsStillActive() = runTest {
        // For example a timeout inside the repository: the load itself was not cancelled.
        ayahRepository.failureToThrow = CancellationException("timed out")

        val viewModel = fixture.createViewModel()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun retryAfterErrorShowsLoadingThenAyah() = runTest {
        ayahRepository.failureToThrow = IOException("asset missing")
        val viewModel = fixture.createViewModel()
        advanceUntilIdle()
        assertEquals(DailyQuoteUiState.Error, viewModel.uiState.value)

        ayahRepository.failureToThrow = null
        ayahRepository.dailyAyah = ayah
        val responseGate = fixture.holdRepositoryResponse()
        viewModel.loadDailyQuote()

        assertEquals(DailyQuoteUiState.Loading, viewModel.uiState.value)

        responseGate.complete(Unit)
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(ayah), viewModel.uiState.value)
    }

    @Test
    fun newerLoadWinsOverOlderSuspendedLoad() = runTest {
        ayahRepository.dailyAyah = otherAyah
        val olderResponseGate = fixture.holdRepositoryResponse()
        val viewModel = fixture.createViewModel()

        ayahRepository.responseGate = null
        ayahRepository.dailyAyah = ayah
        viewModel.loadDailyQuote()
        advanceUntilIdle()
        assertEquals(DailyQuoteUiState.Success(ayah), viewModel.uiState.value)

        olderResponseGate.complete(Unit)
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(ayah), viewModel.uiState.value)
    }
}
