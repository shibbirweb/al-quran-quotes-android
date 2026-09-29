package shibbir.me.alquranquotes.feature.dailyquote

import shibbir.me.alquranquotes.model.Ayah

sealed interface DailyQuoteUiState {
    data object Loading : DailyQuoteUiState
    data class Success(val ayah: Ayah) : DailyQuoteUiState
    data object Error : DailyQuoteUiState
}
