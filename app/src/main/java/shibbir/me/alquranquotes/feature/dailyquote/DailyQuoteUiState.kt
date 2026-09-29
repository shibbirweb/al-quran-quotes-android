package shibbir.me.alquranquotes.feature.dailyquote

import shibbir.me.alquranquotes.model.Ayah

/** What the daily quote screen shows. */
sealed interface DailyQuoteUiState {
    data object Loading : DailyQuoteUiState

    data class Success(val ayah: Ayah) : DailyQuoteUiState

    /** No ayah could be shown: either no ayahs are available, or loading failed. */
    data object Error : DailyQuoteUiState
}
