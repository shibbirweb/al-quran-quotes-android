package shibbir.me.alquranquotes.feature.quoteeditor

import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.testing.MainDispatcherRule
import java.io.IOException

/** Save in progress, save failures, and taps that must not save twice. */
class QuoteEditorViewModelSavingTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixture = QuoteEditorViewModelTestFixture()

    @Test
    fun aFailedSaveIsReportedAndDoesNotLeaveTheEditor() {
        val viewModel = fixture.createViewModel()
        viewModel.fillFreeTextForm()
        fixture.quoteRepository.failureToThrow = IOException("disk full")

        viewModel.save()

        assertTrue(viewModel.uiState.value.hasSaveFailed)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    @Test
    fun aSecondSaveTapWhileSavingIsIgnored() {
        val viewModel = fixture.createViewModel()
        viewModel.fillFreeTextForm()
        val responseGate = CompletableDeferred<Unit>()
        fixture.quoteRepository.responseGate = responseGate

        viewModel.save()
        viewModel.save()

        assertTrue(viewModel.uiState.value.isSaving)
        assertFalse(viewModel.uiState.value.canSave)
        responseGate.complete(Unit)
        assertEquals(1, fixture.quoteRepository.addedQuoteDrafts.size)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun retryingAfterAFailedSaveClearsTheFailureAndSaves() {
        val viewModel = fixture.createViewModel()
        viewModel.fillFreeTextForm()
        fixture.quoteRepository.failureToThrow = IOException("disk full")
        viewModel.save()

        fixture.quoteRepository.failureToThrow = null
        viewModel.save()

        assertFalse(viewModel.uiState.value.hasSaveFailed)
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun saveIsIgnoredWhileTheEditedQuoteIsStillLoading() {
        val freeTextDraft = QuoteDraft.FreeTextDraft(text = "free text", reference = "")
        val quoteId = fixture.quoteRepository.storeUserQuote(freeTextDraft)
        fixture.quoteRepository.responseGate = CompletableDeferred()
        val viewModel = fixture.createViewModel(quoteId = quoteId)

        viewModel.save()

        assertFalse(viewModel.uiState.value.canSave)
        val noUpdates = emptyList<Pair<Long, QuoteDraft>>()
        assertEquals(noUpdates, fixture.quoteRepository.updatedQuoteDrafts)
        assertFalse(viewModel.uiState.value.hasAttemptedSave)
    }
}
