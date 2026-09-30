package shibbir.me.alquranquotes.feature.quoteeditor

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.testing.MainDispatcherRule

/** Adding a new quote. Editing is in [QuoteEditorViewModelEditTest]. */
class QuoteEditorViewModelAddTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixture = QuoteEditorViewModelTestFixture()

    @Test
    fun startsWithAnEmptyAyahFormWhenNoQuoteIdIsGiven() {
        val viewModel = fixture.createViewModel(quoteId = null)

        val expectedState = QuoteEditorUiState(
            isEditing = false,
            status = QuoteEditorStatus.READY,
            form = QuoteEditorForm(quoteKind = QuoteKind.AYAH),
        )
        assertEquals(expectedState, viewModel.uiState.value)
    }

    @Test
    fun typingIntoAFieldUpdatesTheForm() {
        val viewModel = fixture.createViewModel()

        viewModel.updateField(QuoteEditorField.SURAH_NAME, "User Surah")

        assertEquals("User Surah", viewModel.uiState.value.form.surahName)
    }

    @Test
    fun selectingFreeTextSwitchesTheFormKind() {
        val viewModel = fixture.createViewModel()

        viewModel.selectQuoteKind(QuoteKind.FREE_TEXT)

        assertEquals(QuoteKind.FREE_TEXT, viewModel.uiState.value.form.quoteKind)
    }

    @Test
    fun savingAValidAyahAddsItAndSignalsSaved() = runTest {
        val viewModel = fixture.createViewModel()
        viewModel.fillAyahForm()

        viewModel.save()

        val expectedDraft = QuoteDraft.AyahDraft(
            surahName = "User Surah",
            surahNumber = 2,
            ayahNumber = 7,
            arabicText = "user-arabic",
            translation = "user-translation",
        )
        assertEquals(listOf(expectedDraft), fixture.quoteRepository.addedQuoteDrafts)
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun savingFreeTextWithABlankReferenceAddsIt() = runTest {
        val viewModel = fixture.createViewModel()
        viewModel.fillFreeTextForm(reference = " ")

        viewModel.save()

        val expectedDraft = QuoteDraft.FreeTextDraft(text = "free text", reference = "")
        assertEquals(listOf(expectedDraft), fixture.quoteRepository.addedQuoteDrafts)
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun savingAnInvalidFormShowsFieldErrorsAndSavesNothing() = runTest {
        val viewModel = fixture.createViewModel()
        viewModel.selectQuoteKind(QuoteKind.FREE_TEXT)

        viewModel.save()

        val expectedErrors = mapOf(QuoteEditorField.FREE_TEXT to QuoteFieldError.REQUIRED)
        assertEquals(expectedErrors, viewModel.uiState.value.fieldErrors)
        assertEquals(emptyList<QuoteDraft>(), fixture.quoteRepository.addedQuoteDrafts)
        assertFalse(viewModel.uiState.value.isSaved)
    }
}
