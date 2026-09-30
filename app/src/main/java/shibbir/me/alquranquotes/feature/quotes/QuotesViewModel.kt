package shibbir.me.alquranquotes.feature.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import shibbir.me.alquranquotes.data.repository.QuoteRepository
import shibbir.me.alquranquotes.model.Quote
import javax.inject.Inject

/** Lists every quote and deletes any quote once the user confirms it. */
@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
) : ViewModel() {

    private val quoteIdPendingDelete = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<QuotesUiState> = combine(
        quoteRepository.observeQuotes(),
        quoteIdPendingDelete,
    ) { quotes, pendingDeleteId -> loadedState(quotes, pendingDeleteId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = QuotesUiState.Loading,
        )

    /** Asks the user to confirm deleting the quote with [quoteId], bundled or their own. */
    fun requestDelete(quoteId: Long) {
        quoteIdPendingDelete.value = quoteId
    }

    /** Deletes the quote the user was asked about and closes the confirmation. */
    fun confirmDelete() {
        val pendingDeleteId = quoteIdPendingDelete.value
        if (pendingDeleteId == null) {
            return
        }
        quoteIdPendingDelete.value = null
        viewModelScope.launch {
            quoteRepository.deleteQuote(pendingDeleteId)
        }
    }

    /** Closes the confirmation without deleting anything. */
    fun dismissDelete() {
        quoteIdPendingDelete.value = null
    }

    private fun loadedState(quotes: List<Quote>, pendingDeleteId: Long?): QuotesUiState {
        val quoteCards = quotes.map { quote -> quote.toQuoteListCard() }
        return QuotesUiState.Loaded(
            quoteCards = quoteCards,
            quoteIdPendingDelete = pendingDeleteId,
        )
    }

    private companion object {
        /** Keeps the list alive through a configuration change, such as a rotation. */
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
