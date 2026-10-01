package shibbir.me.alquranquotes.feature.quotes

import android.content.Context
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
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

/** The list, its labels, Edit, and the Add button. Deleting is in [QuotesScreenDeleteTest]. */
@RunWith(AndroidJUnit4::class)
class QuotesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val editDescription = targetContext.getString(R.string.quotes_edit)

    private val deleteDescription = targetContext.getString(R.string.quotes_delete)

    private val addLabel = targetContext.getString(R.string.quotes_add)

    /** Every sample card, in list order, with the kind label it must show. */
    private val cardsWithTheirKindLabels = listOf(
        deviceBundledAyahCard to R.string.quotes_kind_bundled_ayah,
        deviceEditedAyahCard to R.string.quotes_kind_edited_bundled_ayah,
        deviceUserAyahCard to R.string.quotes_kind_user_ayah,
        deviceUserFreeTextCard to R.string.quotes_kind_user_free_text,
    )

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

        for ((quoteCard, kindLabelResId) in cardsWithTheirKindLabels) {
            composeTestRule.scrollToQuoteCard(quoteCard)
            composeTestRule.onNodeWithText(targetContext.getString(kindLabelResId)).assertExists()
        }
    }

    @Test
    fun cardsShowTheirPreviewAndReference() {
        composeTestRule.setQuotesScreen(loadedStateWithEveryKind())

        composeTestRule.scrollToQuoteCard(deviceUserAyahCard)
        composeTestRule.onNodeWithText("user-arabic").assertExists()
        composeTestRule.onNodeWithText("user-translation").assertExists()
        composeTestRule.onNodeWithText("User Surah 2:7").assertExists()
        composeTestRule.scrollToQuoteCard(deviceUserFreeTextCard)
        composeTestRule.onNodeWithText("user-free-text").assertExists()
        composeTestRule.onNodeWithText("user-reference").assertExists()
    }

    /** The buttons and texts of a card are all direct children of the card's node. */
    @Test
    fun everyCardHasEditAndDeleteButtons() {
        composeTestRule.setQuotesScreen(loadedStateWithEveryKind())

        for ((quoteCard, kindLabelResId) in cardsWithTheirKindLabels) {
            composeTestRule.scrollToQuoteCard(quoteCard)
            val inThisCard = hasParent(hasAnyChild(hasText(targetContext.getString(kindLabelResId))))
            composeTestRule.onNode(hasContentDescription(editDescription) and inThisCard)
                .assertExists()
            composeTestRule.onNode(hasContentDescription(deleteDescription) and inThisCard)
                .assertExists()
        }
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
