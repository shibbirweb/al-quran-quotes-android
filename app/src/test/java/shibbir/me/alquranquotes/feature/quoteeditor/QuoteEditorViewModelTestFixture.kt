package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.lifecycle.SavedStateHandle
import shibbir.me.alquranquotes.navigation.AppDestination
import shibbir.me.alquranquotes.testing.FakeQuoteRepository

/** Builds a [QuoteEditorViewModel] the way navigation does, with a fake repository. */
class QuoteEditorViewModelTestFixture {

    val quoteRepository = FakeQuoteRepository()

    /** Adding when [quoteId] is null, editing otherwise. */
    fun createViewModel(quoteId: Long? = null): QuoteEditorViewModel {
        val savedStateHandle = SavedStateHandle()
        if (quoteId != null) {
            savedStateHandle[AppDestination.EditQuote.QUOTE_ID_KEY] = quoteId
        }
        return QuoteEditorViewModel(
            savedStateHandle = savedStateHandle,
            quoteRepository = quoteRepository,
            quoteDraftValidator = QuoteDraftValidator(),
        )
    }
}
