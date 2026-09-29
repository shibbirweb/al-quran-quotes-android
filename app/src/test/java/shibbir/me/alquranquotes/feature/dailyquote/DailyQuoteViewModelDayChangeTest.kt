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
 * Moving to the new day's ayah, either on resume through
 * [DailyQuoteViewModel.refreshIfDayChanged] or on a day change event.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DailyQuoteViewModelDayChangeTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixture = DailyQuoteViewModelTestFixture()

    private val ayahRepository = fixture.ayahRepository

    private val epochDayProvider = fixture.epochDayProvider

    private val dayChangeSource = fixture.dayChangeSource

    private val ayah = fixture.ayah

    private val otherAyah = fixture.otherAyah

    @Test
    fun refreshIfDayChangedReloadsWhenTheDayChanges() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)

        epochDayProvider.currentEpochDay = 101L
        ayahRepository.dailyAyah = otherAyah
        viewModel.refreshIfDayChanged()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(otherAyah), viewModel.uiState.value)
        assertEquals(listOf(100L, 101L), ayahRepository.requestedEpochDays)
    }

    @Test
    fun refreshIfDayChangedShowsLoadingWhileTheNewDayLoads() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)

        epochDayProvider.currentEpochDay = 101L
        ayahRepository.dailyAyah = otherAyah
        val responseGate = fixture.holdRepositoryResponse()
        viewModel.refreshIfDayChanged()

        assertEquals(listOf(100L, 101L), ayahRepository.requestedEpochDays)
        assertEquals(DailyQuoteUiState.Loading, viewModel.uiState.value)

        responseGate.complete(Unit)
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(otherAyah), viewModel.uiState.value)
    }

    @Test
    fun refreshIfDayChangedDoesNothingOnTheSameDay() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)

        viewModel.refreshIfDayChanged()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(ayah), viewModel.uiState.value)
        assertEquals(listOf(100L), ayahRepository.requestedEpochDays)
    }

    @Test
    fun refreshIfDayChangedComparesWithTheDayOfTheLatestRetry() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)
        epochDayProvider.currentEpochDay = 101L
        viewModel.loadDailyQuote()
        advanceUntilIdle()

        viewModel.refreshIfDayChanged()
        advanceUntilIdle()

        assertEquals(listOf(100L, 101L), ayahRepository.requestedEpochDays)
    }

    @Test
    fun dayChangeEventOnANewDayReloads() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)

        epochDayProvider.currentEpochDay = 101L
        ayahRepository.dailyAyah = otherAyah
        dayChangeSource.emitDayChange()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(otherAyah), viewModel.uiState.value)
        assertEquals(listOf(100L, 101L), ayahRepository.requestedEpochDays)
    }

    @Test
    fun dayChangeEventOnTheSameDayDoesNothing() = runTest {
        val viewModel = createLoadedViewModel(epochDay = 100L)

        dayChangeSource.emitDayChange()
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(ayah), viewModel.uiState.value)
        assertEquals(listOf(100L), ayahRepository.requestedEpochDays)
    }

    /** Creates a view model that has finished loading the fixture's ayah for [epochDay]. */
    private fun TestScope.createLoadedViewModel(epochDay: Long): DailyQuoteViewModel {
        epochDayProvider.currentEpochDay = epochDay
        ayahRepository.dailyAyah = ayah
        val viewModel = fixture.createViewModel()
        advanceUntilIdle()
        return viewModel
    }
}
