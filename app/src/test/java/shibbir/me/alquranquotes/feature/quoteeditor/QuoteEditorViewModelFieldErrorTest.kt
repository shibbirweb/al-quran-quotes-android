package shibbir.me.alquranquotes.feature.quoteeditor

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import shibbir.me.alquranquotes.testing.MainDispatcherRule

/** When field errors show and how they follow the user's typing after a save attempt. */
class QuoteEditorViewModelFieldErrorTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixture = QuoteEditorViewModelTestFixture()

    private val noErrors = emptyMap<QuoteEditorField, QuoteFieldError>()

    @Test
    fun fixingAFieldAfterASaveAttemptClearsItsError() {
        val viewModel = fixture.createViewModel()
        viewModel.selectQuoteKind(QuoteKind.FREE_TEXT)
        viewModel.save()

        viewModel.updateField(QuoteEditorField.FREE_TEXT, "free text")

        assertEquals(noErrors, viewModel.uiState.value.fieldErrors)
    }

    @Test
    fun typingBeforeASaveAttemptShowsNoErrors() {
        val viewModel = fixture.createViewModel()

        viewModel.updateField(QuoteEditorField.SURAH_NUMBER, "abc")
        viewModel.selectQuoteKind(QuoteKind.FREE_TEXT)

        assertEquals(noErrors, viewModel.uiState.value.fieldErrors)
    }

    @Test
    fun switchingKindAfterASaveAttemptShowsTheErrorsOfTheNewKind() {
        val viewModel = fixture.createViewModel()
        viewModel.selectQuoteKind(QuoteKind.FREE_TEXT)
        viewModel.save()

        viewModel.selectQuoteKind(QuoteKind.AYAH)

        val expectedErrors = mapOf(
            QuoteEditorField.SURAH_NAME to QuoteFieldError.REQUIRED,
            QuoteEditorField.SURAH_NUMBER to QuoteFieldError.REQUIRED,
            QuoteEditorField.AYAH_NUMBER to QuoteFieldError.REQUIRED,
            QuoteEditorField.ARABIC_TEXT to QuoteFieldError.REQUIRED,
            QuoteEditorField.TRANSLATION to QuoteFieldError.REQUIRED,
        )
        assertEquals(expectedErrors, viewModel.uiState.value.fieldErrors)
    }
}
