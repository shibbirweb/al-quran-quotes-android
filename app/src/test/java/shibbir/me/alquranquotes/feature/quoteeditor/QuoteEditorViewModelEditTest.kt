package shibbir.me.alquranquotes.feature.quoteeditor

import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.testing.MainDispatcherRule
import java.io.IOException

/** Editing an existing quote. Adding is in [QuoteEditorViewModelAddTest]. */
class QuoteEditorViewModelEditTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixture = QuoteEditorViewModelTestFixture()

    private val ayahDraft = QuoteDraft.AyahDraft(
        surahName = "User Surah",
        surahNumber = 2,
        ayahNumber = 7,
        arabicText = "user-arabic",
        translation = "user-translation",
    )

    @Test
    fun loadsTheAyahBeingEdited() {
        val quoteId = fixture.quoteRepository.storeUserQuote(ayahDraft)

        val viewModel = fixture.createViewModel(quoteId = quoteId)

        val expectedForm = QuoteEditorForm(
            quoteKind = QuoteKind.AYAH,
            surahName = "User Surah",
            surahNumber = "2",
            ayahNumber = "7",
            arabicText = "user-arabic",
            translation = "user-translation",
        )
        val expectedState = QuoteEditorUiState(
            isEditing = true,
            status = QuoteEditorStatus.READY,
            form = expectedForm,
        )
        assertEquals(expectedState, viewModel.uiState.value)
        assertEquals(listOf(quoteId), fixture.quoteRepository.requestedQuoteIds)
    }

    @Test
    fun loadsTheFreeTextBeingEdited() {
        val freeTextDraft = QuoteDraft.FreeTextDraft(text = "free text", reference = "")
        val quoteId = fixture.quoteRepository.storeUserQuote(freeTextDraft)

        val viewModel = fixture.createViewModel(quoteId = quoteId)

        val expectedForm = QuoteEditorForm(
            quoteKind = QuoteKind.FREE_TEXT,
            freeText = "free text",
            reference = "",
        )
        assertEquals(expectedForm, viewModel.uiState.value.form)
        assertEquals(QuoteEditorStatus.READY, viewModel.uiState.value.status)
    }

    @Test
    fun theKindCannotBeChangedWhileEditing() {
        val quoteId = fixture.quoteRepository.storeUserQuote(ayahDraft)
        val viewModel = fixture.createViewModel(quoteId = quoteId)

        viewModel.selectQuoteKind(QuoteKind.FREE_TEXT)

        assertEquals(QuoteKind.AYAH, viewModel.uiState.value.form.quoteKind)
    }

    @Test
    fun showsLoadingUntilTheQuoteArrives() {
        val quoteId = fixture.quoteRepository.storeUserQuote(ayahDraft)
        val responseGate = CompletableDeferred<Unit>()
        fixture.quoteRepository.responseGate = responseGate

        val viewModel = fixture.createViewModel(quoteId = quoteId)

        assertEquals(QuoteEditorStatus.LOADING, viewModel.uiState.value.status)
        responseGate.complete(Unit)
        assertEquals(QuoteEditorStatus.READY, viewModel.uiState.value.status)
    }

    @Test
    fun aQuoteThatNoLongerExistsIsUnavailable() {
        val viewModel = fixture.createViewModel(quoteId = 404L)

        assertEquals(QuoteEditorStatus.UNAVAILABLE, viewModel.uiState.value.status)
    }

    @Test
    fun aQuoteThatFailsToLoadIsUnavailable() {
        val quoteId = fixture.quoteRepository.storeUserQuote(ayahDraft)
        fixture.quoteRepository.failureToThrow = IOException("disk error")

        val viewModel = fixture.createViewModel(quoteId = quoteId)

        assertEquals(QuoteEditorStatus.UNAVAILABLE, viewModel.uiState.value.status)
    }

    @Test
    fun savingAnEditedQuoteUpdatesItInsteadOfAddingOne() {
        val quoteId = fixture.quoteRepository.storeUserQuote(ayahDraft)
        val viewModel = fixture.createViewModel(quoteId = quoteId)
        viewModel.updateField(QuoteEditorField.TRANSLATION, "edited translation")

        viewModel.save()

        val editedDraft = ayahDraft.copy(translation = "edited translation")
        val expectedUpdates = listOf(quoteId to editedDraft)
        assertEquals(expectedUpdates, fixture.quoteRepository.updatedQuoteDrafts)
        assertEquals(emptyList<QuoteDraft>(), fixture.quoteRepository.addedQuoteDrafts)
        assertTrue(viewModel.uiState.value.isSaved)
    }
}
