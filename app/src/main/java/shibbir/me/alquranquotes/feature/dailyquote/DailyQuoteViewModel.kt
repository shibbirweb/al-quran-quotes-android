package shibbir.me.alquranquotes.feature.dailyquote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import shibbir.me.alquranquotes.core.time.EpochDayProvider
import shibbir.me.alquranquotes.data.repository.AyahRepository
import javax.inject.Inject

@HiltViewModel
class DailyQuoteViewModel @Inject constructor(
    private val ayahRepository: AyahRepository,
    private val epochDayProvider: EpochDayProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DailyQuoteUiState>(DailyQuoteUiState.Loading)
    val uiState: StateFlow<DailyQuoteUiState> = _uiState.asStateFlow()

    init {
        loadDailyQuote()
    }

    fun loadDailyQuote() {
        _uiState.value = DailyQuoteUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                when (val ayah = ayahRepository.getDailyAyah(epochDayProvider.today())) {
                    null -> DailyQuoteUiState.Error
                    else -> DailyQuoteUiState.Success(ayah)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                DailyQuoteUiState.Error
            }
        }
    }
}
