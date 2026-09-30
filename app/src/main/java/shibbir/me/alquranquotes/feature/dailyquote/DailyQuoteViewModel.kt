package shibbir.me.alquranquotes.feature.dailyquote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import shibbir.me.alquranquotes.core.time.DayChangeSource
import shibbir.me.alquranquotes.core.time.EpochDayProvider
import shibbir.me.alquranquotes.data.repository.DailyQuoteRepository
import shibbir.me.alquranquotes.model.Quote
import javax.inject.Inject

/**
 * Loads the quote of the day and moves to the next day's quote when the day changes, either on
 * a [DayChangeSource] event or when the screen resumes.
 */
@HiltViewModel
class DailyQuoteViewModel @Inject constructor(
    private val dailyQuoteRepository: DailyQuoteRepository,
    private val epochDayProvider: EpochDayProvider,
    private val dayChangeSource: DayChangeSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DailyQuoteUiState>(DailyQuoteUiState.Loading)
    val uiState: StateFlow<DailyQuoteUiState> = _uiState.asStateFlow()

    /** The epoch day of the most recently started load. */
    private var requestedEpochDay: Long = epochDayProvider.today()

    private var loadJob: Job? = null

    init {
        startLoading(epochDay = requestedEpochDay)
        refreshOnEveryDayChange()
    }

    /** Loads today's quote, for example when the user taps Retry. */
    fun loadDailyQuote() {
        val today = epochDayProvider.today()
        startLoading(epochDay = today)
    }

    /** Reloads only when the day has changed since the last load, for example after midnight. */
    fun refreshIfDayChanged() {
        val today = epochDayProvider.today()
        if (today == requestedEpochDay) {
            return
        }
        startLoading(epochDay = today)
    }

    /** Listens for day changes for as long as this ViewModel lives. */
    private fun refreshOnEveryDayChange() {
        val dayChanges = dayChangeSource.dayChanges()
        viewModelScope.launch {
            dayChanges.collect {
                refreshIfDayChanged()
            }
        }
    }

    private fun startLoading(epochDay: Long) {
        loadJob?.cancel()
        requestedEpochDay = epochDay
        _uiState.value = DailyQuoteUiState.Loading
        loadJob = viewModelScope.launch {
            val dailyQuoteState = loadDailyQuoteState(epochDay)
            _uiState.value = dailyQuoteState
        }
    }

    private suspend fun loadDailyQuoteState(epochDay: Long): DailyQuoteUiState {
        try {
            val quote = dailyQuoteRepository.getDailyQuote(epochDay)
            return dailyQuoteStateFor(quote)
        } catch (exception: Exception) {
            // A CancellationException can also come from inside the repository, for example from
            // a timeout. This rethrows only when this load itself was cancelled, so a cancelled
            // load never overwrites the newer one (DailyQuoteViewModelCancelledLoadTest).
            currentCoroutineContext().ensureActive()
            return DailyQuoteUiState.Error
        }
    }

    private fun dailyQuoteStateFor(quote: Quote?): DailyQuoteUiState {
        if (quote == null) {
            return DailyQuoteUiState.Error
        }
        return DailyQuoteUiState.Success(quote)
    }
}
