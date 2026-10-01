package shibbir.me.alquranquotes.feature.quoteeditor

import android.content.Context
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.R

/** The top app bar, the kind picker, and the fields. States are in [QuoteEditorScreenStateTest]. */
@RunWith(AndroidJUnit4::class)
class QuoteEditorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val saveLabel = targetContext.getString(R.string.quote_editor_save)

    private fun labelOf(quoteEditorField: QuoteEditorField): String {
        return targetContext.getString(quoteEditorField.labelResId)
    }

    @Test
    fun addingShowsTheAddTitleAsAHeadingAndTheKindPicker() {
        composeTestRule.setQuoteEditorScreen(readyToAddState())

        val addTitle = targetContext.getString(R.string.quote_editor_add_title)
        composeTestRule.onNode(hasText(addTitle) and isHeading()).assertExists()
        composeTestRule.onNodeWithText(labelOfKind(QuoteKind.FREE_TEXT)).assertExists()
    }

    @Test
    fun editingShowsTheEditTitleWithoutTheKindPicker() {
        val editingState = readyToAddState().copy(isEditing = true)
        composeTestRule.setQuoteEditorScreen(editingState)

        val editTitle = targetContext.getString(R.string.quote_editor_edit_title)
        composeTestRule.onNode(hasText(editTitle) and isHeading()).assertExists()
        composeTestRule.onNodeWithText(labelOfKind(QuoteKind.AYAH)).assertDoesNotExist()
        composeTestRule.onNodeWithText(labelOfKind(QuoteKind.FREE_TEXT)).assertDoesNotExist()
    }

    @Test
    fun editingAFreeTextQuoteShowsItsFieldsWithoutTheKindPicker() {
        val editingState = readyToAddState(QuoteKind.FREE_TEXT).copy(isEditing = true)
        composeTestRule.setQuoteEditorScreen(editingState)

        composeTestRule.onNodeWithText(labelOf(QuoteEditorField.FREE_TEXT)).assertExists()
        composeTestRule.onNodeWithText(labelOfKind(QuoteKind.AYAH)).assertDoesNotExist()
    }

    @Test
    fun ayahKindShowsOnlyTheAyahFields() {
        composeTestRule.setQuoteEditorScreen(readyToAddState(QuoteKind.AYAH))

        for (ayahField in QuoteKind.AYAH.editorFields) {
            composeTestRule.onNodeWithText(labelOf(ayahField)).assertExists()
        }
        for (freeTextField in QuoteKind.FREE_TEXT.editorFields) {
            composeTestRule.onNodeWithText(labelOf(freeTextField)).assertDoesNotExist()
        }
    }

    @Test
    fun freeTextKindShowsOnlyTheFreeTextFields() {
        composeTestRule.setQuoteEditorScreen(readyToAddState(QuoteKind.FREE_TEXT))

        for (freeTextField in QuoteKind.FREE_TEXT.editorFields) {
            composeTestRule.onNodeWithText(labelOf(freeTextField)).assertExists()
        }
        for (ayahField in QuoteKind.AYAH.editorFields) {
            composeTestRule.onNodeWithText(labelOf(ayahField)).assertDoesNotExist()
        }
    }

    @Test
    fun tappingAKindCallsOnSelectQuoteKind() {
        val callbacks = RecordedQuoteEditorCallbacks()
        composeTestRule.setQuoteEditorScreen(readyToAddState(QuoteKind.AYAH), callbacks)

        composeTestRule.onNodeWithText(labelOfKind(QuoteKind.FREE_TEXT)).performClick()

        assertEquals(listOf(QuoteKind.FREE_TEXT), callbacks.selectedQuoteKinds)
    }

    @Test
    fun typingCallsOnFieldChangeForThatField() {
        val callbacks = RecordedQuoteEditorCallbacks()
        composeTestRule.setQuoteEditorScreen(readyToAddState(QuoteKind.FREE_TEXT), callbacks)

        val referenceLabel = labelOf(QuoteEditorField.REFERENCE)
        composeTestRule.onNodeWithText(referenceLabel).performTextInput("typed")

        assertEquals(listOf(QuoteEditorField.REFERENCE to "typed"), callbacks.fieldChanges)
    }

    @Test
    fun saveCallsOnSave() {
        val callbacks = RecordedQuoteEditorCallbacks()
        composeTestRule.setQuoteEditorScreen(readyToAddState(), callbacks)

        composeTestRule.onNodeWithText(saveLabel).performClick()

        assertEquals(1, callbacks.saveCount)
    }

    @Test
    fun saveIsDisabledWhileSaving() {
        composeTestRule.setQuoteEditorScreen(readyToAddState().copy(isSaving = true))

        composeTestRule.onNodeWithText(saveLabel).assertIsNotEnabled()
    }

    @Test
    fun backCallsOnBack() {
        val callbacks = RecordedQuoteEditorCallbacks()
        composeTestRule.setQuoteEditorScreen(readyToAddState(), callbacks)

        val backDescription = targetContext.getString(R.string.quote_editor_back)
        composeTestRule.onNodeWithContentDescription(backDescription).performClick()

        assertEquals(1, callbacks.backCount)
    }

    @Test
    fun formScrollsSoEveryFieldStaysReachableAtLargeFontSizes() {
        composeTestRule.setQuoteEditorScreen(readyToAddState())

        composeTestRule.onNode(hasScrollAction()).assertExists()
    }

    private fun labelOfKind(quoteKind: QuoteKind): String {
        return targetContext.getString(quoteKind.labelResId)
    }
}
