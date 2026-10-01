package shibbir.me.alquranquotes.feature.quoteeditor

import android.content.Context
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.R

/** Field errors, loading, unavailable, and save failure states of the editor. */
@RunWith(AndroidJUnit4::class)
class QuoteEditorScreenStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun fieldErrorsShowTheirMessages() {
        val fieldErrors = mapOf(
            QuoteEditorField.SURAH_NUMBER to QuoteFieldError.SURAH_NUMBER_OUT_OF_RANGE,
            QuoteEditorField.AYAH_NUMBER to QuoteFieldError.AYAH_NUMBER_TOO_SMALL,
        )
        val stateWithErrors = readyToAddState().copy(
            fieldErrors = fieldErrors,
            hasAttemptedSave = true,
        )
        composeTestRule.setQuoteEditorScreen(stateWithErrors)

        for (quoteFieldError in fieldErrors.values) {
            val errorMessage = targetContext.getString(quoteFieldError.messageResId)
            composeTestRule.onNodeWithText(errorMessage, useUnmergedTree = true).assertExists()
        }
    }

    @Test
    fun noErrorMessagesShowWithoutErrors() {
        composeTestRule.setQuoteEditorScreen(readyToAddState())

        val requiredMessage = targetContext.getString(QuoteFieldError.REQUIRED.messageResId)
        composeTestRule.onNodeWithText(requiredMessage, useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun loadingShowsDescribedProgressIndicatorAndDisablesSave() {
        val loadingState = QuoteEditorUiState(isEditing = true, status = QuoteEditorStatus.LOADING)
        composeTestRule.setQuoteEditorScreen(loadingState)

        val loadingDescription = targetContext.getString(R.string.quote_editor_loading)
        composeTestRule.onNodeWithContentDescription(loadingDescription).assertExists()
        val saveLabel = targetContext.getString(R.string.quote_editor_save)
        composeTestRule.onNodeWithText(saveLabel).assertIsNotEnabled()
    }

    @Test
    fun unavailableQuoteShowsAMessageInsteadOfTheForm() {
        val unavailableState = QuoteEditorUiState(
            isEditing = true,
            status = QuoteEditorStatus.UNAVAILABLE,
        )
        composeTestRule.setQuoteEditorScreen(unavailableState)

        val unavailableMessage = targetContext.getString(R.string.quote_editor_unavailable)
        composeTestRule.onNodeWithText(unavailableMessage).assertExists()
        val surahNameLabel = targetContext.getString(QuoteEditorField.SURAH_NAME.labelResId)
        composeTestRule.onNodeWithText(surahNameLabel).assertDoesNotExist()
    }

    @Test
    fun failedSaveShowsAMessage() {
        composeTestRule.setQuoteEditorScreen(readyToAddState().copy(hasSaveFailed = true))

        val saveFailedMessage = targetContext.getString(R.string.quote_editor_save_failed)
        composeTestRule.onNodeWithText(saveFailedMessage).assertExists()
    }
}
