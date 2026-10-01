package shibbir.me.alquranquotes.feature.dailyquote

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import shibbir.me.alquranquotes.testing.MainDispatcherRule

/**
 * Moving to the new day's quote, either on resume through
 * [DailyQuoteViewModel.refreshIfDayChanged] or on a day change event.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DailyQuoteViewModelDayChangeTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixture = DailyQuoteViewModelTestFixture()

    private val dailyQuoteRepository = fixture.dailyQuoteRepository

    private val epochDayProvider = fixture.epochDayProvider

    private val dayChangeSource = fixture.dayChangeSource

    private val quote = fixture.quote

    private val otherQuote = fixture.otherQuote

    @Test
    fun refreshIfDayChangedReloadsWhenTheDayChanges() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)

        epochDayProvider.currentEpochDay = 101L
        dailyQuoteRepository.dailyQuote = otherQuote
        viewModel.refreshIfDayChanged()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(otherQuote), viewModel.uiState.value)
        assertEquals(listOf(100L, 101L), dailyQuoteRepository.requestedEpochDays)
    }

    @Test
    fun refreshIfDayChangedShowsLoadingWhileTheNewDayLoads() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)

        epochDayProvider.currentEpochDay = 101L
        dailyQuoteRepository.dailyQuote = otherQuote
        val responseGate = fixture.holdRepositoryResponse()
        viewModel.refreshIfDayChanged()

        assertEquals(listOf(100L, 101L), dailyQuoteRepository.requestedEpochDays)
        assertEquals(DailyQuoteUiState.Loading, viewModel.uiState.value)

        responseGate.complete(Unit)
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(otherQuote), viewModel.uiState.value)
    }

    @Test
    fun refreshIfDayChangedDoesNothingOnTheSameDay() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)

        viewModel.refreshIfDayChanged()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(quote), viewModel.uiState.value)
        assertEquals(listOf(100L), dailyQuoteRepository.requestedEpochDays)
    }

    @Test
    fun refreshIfDayChangedComparesWithTheDayOfTheLatestRetry() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)
        epochDayProvider.currentEpochDay = 101L
        viewModel.loadDailyQuote()
        advanceUntilIdle()

        viewModel.refreshIfDayChanged()
        advanceUntilIdle()

        assertEquals(listOf(100L, 101L), dailyQuoteRepository.requestedEpochDays)
    }

    @Test
    fun dayChangeEventOnANewDayReloads() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)

        epochDayProvider.currentEpochDay = 101L
        dailyQuoteRepository.dailyQuote = otherQuote
        dayChangeSource.emitDayChange()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(otherQuote), viewModel.uiState.value)
        assertEquals(listOf(100L, 101L), dailyQuoteRepository.requestedEpochDays)
    }

    @Test
    fun dayChangeEventOnTheSameDayDoesNothing() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)

        dayChangeSource.emitDayChange()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(quote), viewModel.uiState.value)
        assertEquals(listOf(100L), dailyQuoteRepository.requestedEpochDays)
    }

    /** Creates a view model that has finished loading the fixture's quote for [epochDay]. */
    private fun TestScope.createLoadedViewModel(epochDay: Long): DailyQuoteViewModel {
        epochDayProvider.currentEpochDay = epochDay
        dailyQuoteRepository.dailyQuote = quote
        val viewModel = fixture.createViewModel()
        advanceUntilIdle()
        return viewModel
    }
}
