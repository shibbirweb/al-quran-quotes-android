package shibbir.me.alquranquotes.feature.quotes

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
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

/** The list, its labels, Edit, and the Add button. Deleting is in [QuotesScreenDeleteTest]. */
@RunWith(AndroidJUnit4::class)
class QuotesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val editDescription = targetContext.getString(R.string.quotes_edit)

    private val deleteDescription = targetContext.getString(R.string.quotes_delete)

    private val addLabel = targetContext.getString(R.string.quotes_add)

    @Test
    fun topAppBarShowsTheTitleAsAHeading() {
        composeTestRule.setQuotesScreen(QuotesUiState.Loading)

        val title = targetContext.getString(R.string.quotes_title)
        composeTestRule.onNode(hasText(title) and isHeading()).assertExists()
    }

    @Test
    fun loadingStateShowsDescribedProgressIndicator() {
        composeTestRule.setQuotesScreen(QuotesUiState.Loading)

        val loadingDescription = targetContext.getString(R.string.quotes_loading)
        composeTestRule.onNodeWithContentDescription(loadingDescription).assertExists()
    }

    @Test
    fun everyCardShowsTheLabelOfItsKind() {
        composeTestRule.setQuotesScreen(loadedStateWithEveryKind())

        val kindLabels = listOf(
            R.string.quotes_kind_bundled_ayah,
            R.string.quotes_kind_edited_bundled_ayah,
            R.string.quotes_kind_user_ayah,
            R.string.quotes_kind_user_free_text,
        )
        for (kindLabelResId in kindLabels) {
            composeTestRule.onNodeWithText(targetContext.getString(kindLabelResId)).assertExists()
        }
    }

    @Test
    fun cardsShowTheirPreviewAndReference() {
        composeTestRule.setQuotesScreen(loadedStateWithEveryKind())

        composeTestRule.onNodeWithText("user-arabic").assertExists()
        composeTestRule.onNodeWithText("user-translation").assertExists()
        composeTestRule.onNodeWithText("User Surah 2:7").assertExists()
        composeTestRule.onNodeWithText("user-free-text").assertExists()
        composeTestRule.onNodeWithText("user-reference").assertExists()
    }

    @Test
    fun everyCardHasEditAndDeleteButtons() {
        composeTestRule.setQuotesScreen(loadedStateWithEveryKind())

        val cardCount = deviceQuoteCards.size
        val editButtons = composeTestRule.onAllNodesWithContentDescription(editDescription)
        editButtons.assertCountEquals(cardCount)
        val deleteButtons = composeTestRule.onAllNodesWithContentDescription(deleteDescription)
        deleteButtons.assertCountEquals(cardCount)
    }

    @Test
    fun editButtonOfABundledAyahCallsOnEditQuoteWithItsId() {
        val callbacks = RecordedQuotesScreenCallbacks()
        composeTestRule.setQuotesScreen(loadedStateWithOnly(deviceBundledAyahCard), callbacks)

        composeTestRule.onNodeWithContentDescription(editDescription).performClick()

        assertEquals(listOf(deviceBundledAyahCard.quoteId), callbacks.editedQuoteIds)
    }

    @Test
    fun editButtonOfTheUsersQuoteCallsOnEditQuoteWithItsId() {
        val callbacks = RecordedQuotesScreenCallbacks()
        composeTestRule.setQuotesScreen(loadedStateWithOnly(deviceUserFreeTextCard), callbacks)

        composeTestRule.onNodeWithContentDescription(editDescription).performClick()

        assertEquals(listOf(deviceUserFreeTextCard.quoteId), callbacks.editedQuoteIds)
    }

    @Test
    fun addButtonShowsItsLabelAtTheTopOfTheList() {
        composeTestRule.setQuotesScreen(loadedStateWithEveryKind())

        composeTestRule.onNodeWithText(addLabel, useUnmergedTree = true).assertExists()
    }

    @Test
    fun addButtonCallsOnAddQuote() {
        val callbacks = RecordedQuotesScreenCallbacks()
        composeTestRule.setQuotesScreen(loadedStateWithEveryKind(), callbacks)

        composeTestRule.onNodeWithContentDescription(addLabel).performClick()

        assertEquals(1, callbacks.addQuoteCount)
    }
}
