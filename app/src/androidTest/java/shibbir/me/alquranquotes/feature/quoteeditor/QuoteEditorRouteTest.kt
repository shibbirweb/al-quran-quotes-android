package shibbir.me.alquranquotes.feature.quoteeditor

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.navigation.AppDestination
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

private const val EDITOR_TIMEOUT_MILLIS = 5_000L

private const val EDITED_QUOTE_ID = 7L

/** Checks that [QuoteEditorRoute] adds, loads and updates, and leaves the editor when done. */
@RunWith(AndroidJUnit4::class)
class QuoteEditorRouteTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val quoteRepository = DeviceFakeQuoteRepository()

    private var doneCount = 0

    @Test
    fun savingAValidQuoteCallsOnDone() {
        showRoute(savedStateHandle = SavedStateHandle())

        clickText(targetContext.getString(QuoteKind.FREE_TEXT.labelResId))
        val textLabel = targetContext.getString(QuoteEditorField.FREE_TEXT.labelResId)
        composeTestRule.onNodeWithText(textLabel).performTextInput("free text")
        clickText(targetContext.getString(R.string.quote_editor_save))

        composeTestRule.waitUntil(EDITOR_TIMEOUT_MILLIS) { doneCount == 1 }
        val expectedDraft = QuoteDraft.FreeTextDraft(text = "free text", reference = "")
        assertEquals(listOf(expectedDraft), quoteRepository.addedQuoteDrafts)
    }

    @Test
    fun editingLoadsTheQuoteByItsIdAndUpdatesIt() {
        val storedDraft = QuoteDraft.FreeTextDraft(text = "stored text", reference = "")
        quoteRepository.storedQuoteDrafts[EDITED_QUOTE_ID] = storedDraft
        showRoute(savedStateHandle = editQuoteSavedStateHandle())
        waitUntilTextIsShown("stored text")

        composeTestRule.onNodeWithText(labelOfKind(QuoteKind.FREE_TEXT)).assertDoesNotExist()
        clickText(targetContext.getString(R.string.quote_editor_save))

        composeTestRule.waitUntil(EDITOR_TIMEOUT_MILLIS) { doneCount == 1 }
        assertEquals(listOf(EDITED_QUOTE_ID to storedDraft), quoteRepository.updatedQuoteDrafts)
    }

    @Test
    fun backCallsOnDoneWithoutSaving() {
        showRoute(savedStateHandle = SavedStateHandle())

        val backDescription = targetContext.getString(R.string.quote_editor_back)
        composeTestRule.onNodeWithContentDescription(backDescription).performClick()

        assertEquals(1, doneCount)
        assertEquals(emptyList<QuoteDraft>(), quoteRepository.addedQuoteDrafts)
    }

    /** Builds the ViewModel by hand with a fake, so the test needs no Hilt graph. */
    private fun showRoute(savedStateHandle: SavedStateHandle) {
        val viewModel = QuoteEditorViewModel(
            savedStateHandle = savedStateHandle,
            quoteRepository = quoteRepository,
            quoteDraftValidator = QuoteDraftValidator(),
        )
        composeTestRule.setContent {
            AlQuranQuotesTheme {
                QuoteEditorRoute(onDone = { doneCount += 1 }, viewModel = viewModel)
            }
        }
    }

    /** What navigation passes the editor for [AppDestination.EditQuote]. */
    private fun editQuoteSavedStateHandle(): SavedStateHandle {
        val quoteIdArgument = AppDestination.EditQuote.QUOTE_ID_KEY to EDITED_QUOTE_ID
        return SavedStateHandle(mapOf(quoteIdArgument))
    }

    private fun waitUntilTextIsShown(text: String) {
        composeTestRule.waitUntil(EDITOR_TIMEOUT_MILLIS) {
            val textNodes = composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes()
            textNodes.isNotEmpty()
        }
    }

    private fun clickText(text: String) {
        composeTestRule.onNodeWithText(text).performClick()
    }

    private fun labelOfKind(quoteKind: QuoteKind): String {
        return targetContext.getString(quoteKind.labelResId)
    }
}
