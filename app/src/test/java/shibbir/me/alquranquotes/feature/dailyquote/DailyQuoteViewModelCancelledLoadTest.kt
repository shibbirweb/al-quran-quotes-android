package shibbir.me.alquranquotes.feature.dailyquote

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import shibbir.me.alquranquotes.testing.MainDispatcherRule

/**
 * Runs on a [StandardTestDispatcher], so a cancelled load resumes only after the newer load has
 * already set Loading. That is the order that shows whether a cancelled load can still write its
 * result. The unconfined dispatcher used elsewhere resumes it too early to tell.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DailyQuoteViewModelCancelledLoadTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val fixture = DailyQuoteViewModelTestFixture()

    private val ayah = fixture.ayah

    @Test
    fun cancelledLoadDoesNotOverwriteTheNewerLoadsState() = runTest {
        fixture.ayahRepository.dailyAyah = ayah
        // Deliberately never completed: the older load stays suspended until it is cancelled.
        val olderResponseGate = fixture.holdRepositoryResponse()
        val viewModel = fixture.createViewModel()
        runCurrent()

        val newerResponseGate = fixture.holdRepositoryResponse()
        viewModel.loadDailyQuote()
        runCurrent()

        assertEquals(DailyQuoteUiState.Loading, viewModel.uiState.value)

        newerResponseGate.complete(Unit)
        advanceUntilIdle()

        assertEquals(DailyQuoteUiState.Success(ayah), viewModel.uiState.value)
        assertFalse(olderResponseGate.isCompleted)
    }
}
