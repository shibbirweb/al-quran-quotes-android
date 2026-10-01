package shibbir.me.alquranquotes.feature.dailyquote

import android.content.Context
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasScrollAction
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
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin

/** The screen's app bar, loading, and error states. Quotes are in [DailyQuoteScreenQuoteTest]. */
@RunWith(AndroidJUnit4::class)
class DailyQuoteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val loadingDescription = targetContext.getString(R.string.daily_quote_loading)

    private val errorMessage = targetContext.getString(R.string.daily_quote_error)

    @Test
    fun topAppBarShowsTheAppNameAsAHeading() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Loading)

        val appName = targetContext.getString(R.string.app_name)
        composeTestRule.onNode(hasText(appName) and isHeading()).assertExists()
    }

    @Test
    fun loadingStateShowsDescribedProgressIndicator() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Loading)

        composeTestRule.onNodeWithContentDescription(loadingDescription).assertExists()
    }

    @Test
    fun errorStateShowsErrorMessage() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Error)

        composeTestRule.onNodeWithText(errorMessage).assertExists()
        composeTestRule.onNodeWithContentDescription(loadingDescription).assertDoesNotExist()
    }

    @Test
    fun errorStateAnnouncesErrorMessagePolitely() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Error)

        val isPoliteLiveRegion = SemanticsMatcher.expectValue(
            SemanticsProperties.LiveRegion,
            LiveRegionMode.Polite,
        )
        composeTestRule.onNodeWithText(errorMessage).assert(isPoliteLiveRegion)
    }

    @Test
    fun errorStateScrollsSoRetryStaysReachableAtLargeFontSizes() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Error)

        composeTestRule.onNode(hasScrollAction()).assertExists()
    }

    @Test
    fun quoteScrollsSoTheTopAppBarCanCollapse() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Success(sampleQuote()))

        composeTestRule.onNode(hasScrollAction()).assertExists()
    }

    @Test
    fun errorStateRetryButtonCallsOnRetry() {
        var retryCount = 0
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Error, onRetry = { retryCount++ })

        val retryLabel = targetContext.getString(R.string.daily_quote_retry)
        composeTestRule.onNodeWithText(retryLabel).performClick()

        assertEquals(1, retryCount)
    }

    /** Placeholder text only, so tests never repeat Quran text. */
    private fun sampleQuote() = Quote.FreeTextQuote(
        quoteId = 1L,
        origin = QuoteOrigin.USER,
        text = "free-text",
        reference = "reference",
    )
}
