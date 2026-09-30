package shibbir.me.alquranquotes.feature.quotes

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.R

/** Delete buttons and the delete confirmation dialog. */
@RunWith(AndroidJUnit4::class)
class QuotesScreenDeleteTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val deleteDescription = targetContext.getString(R.string.quotes_delete)

    private val dialogTitle = targetContext.getString(R.string.quotes_delete_dialog_title)

    @Test
    fun deleteButtonOfTheUsersQuoteCallsOnDeleteQuoteWithItsId() {
        val callbacks = RecordedQuotesScreenCallbacks()
        composeTestRule.setQuotesScreen(loadedStateWithOnly(deviceUserAyahCard), callbacks)

        composeTestRule.onNodeWithContentDescription(deleteDescription).performClick()

        assertEquals(listOf(deviceUserAyahCard.quoteId), callbacks.deletedQuoteIds)
    }

    @Test
    fun deleteButtonOfABundledAyahCallsOnDeleteQuoteWithItsId() {
        val callbacks = RecordedQuotesScreenCallbacks()
        composeTestRule.setQuotesScreen(loadedStateWithOnly(deviceBundledAyahCard), callbacks)

        composeTestRule.onNodeWithContentDescription(deleteDescription).performClick()

        assertEquals(listOf(deviceBundledAyahCard.quoteId), callbacks.deletedQuoteIds)
    }

    @Test
    fun noDialogShowsWhileNoDeleteIsPending() {
        composeTestRule.setQuotesScreen(loadedStateWithEveryKind(quoteIdPendingDelete = null))

        composeTestRule.onNodeWithText(dialogTitle).assertDoesNotExist()
    }

    @Test
    fun confirmingTheDialogCallsOnConfirmDelete() {
        val callbacks = RecordedQuotesScreenCallbacks()
        val pendingState = loadedStateWithEveryKind(quoteIdPendingDelete = 2L)
        composeTestRule.setQuotesScreen(pendingState, callbacks)

        composeTestRule.onNodeWithText(dialogTitle).assertExists()
        val confirmLabel = targetContext.getString(R.string.quotes_delete_confirm)
        composeTestRule.onNodeWithText(confirmLabel).performClick()

        assertEquals(1, callbacks.confirmDeleteCount)
        assertEquals(0, callbacks.dismissDeleteCount)
    }

    @Test
    fun cancellingTheDialogCallsOnDismissDelete() {
        val callbacks = RecordedQuotesScreenCallbacks()
        val pendingState = loadedStateWithEveryKind(quoteIdPendingDelete = 2L)
        composeTestRule.setQuotesScreen(pendingState, callbacks)

        val cancelLabel = targetContext.getString(R.string.quotes_delete_cancel)
        composeTestRule.onNodeWithText(cancelLabel).performClick()

        assertEquals(1, callbacks.dismissDeleteCount)
        assertEquals(0, callbacks.confirmDeleteCount)
    }
}
