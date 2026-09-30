package shibbir.me.alquranquotes.feature.dailyquote

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

private const val FIRST_EPOCH_DAY = 100L

private const val SECOND_EPOCH_DAY = 101L

private const val TEXT_TIMEOUT_MILLIS = 5_000L

/** Checks how [DailyQuoteRoute] wires lifecycle resumes and Retry taps to its ViewModel. */
@RunWith(AndroidJUnit4::class)
class DailyQuoteRouteTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val dailyQuoteRepository = FakeDailyQuoteRepository()

    private val epochDayProvider = FakeEpochDayProvider(currentEpochDay = FIRST_EPOCH_DAY)

    private val firstDayQuote = Quote.AyahQuote(
        quoteId = 1L,
        origin = QuoteOrigin.BUNDLED,
        surahName = "Ash-Sharh",
        surahNumber = 94,
        ayahNumber = 5,
        arabicText = "first-day-arabic-text",
        translation = "first-day-translation",
    )

    private val secondDayQuote = Quote.FreeTextQuote(
        quoteId = 2L,
        origin = QuoteOrigin.USER,
        text = "second-day-free-text",
        reference = "second-day-reference",
    )

    @Test
    fun resumingOnANewDayShowsTheNewDaysQuote() {
        dailyQuoteRepository.dailyQuotesByEpochDay[FIRST_EPOCH_DAY] = firstDayQuote
        dailyQuoteRepository.dailyQuotesByEpochDay[SECOND_EPOCH_DAY] = secondDayQuote
        showRoute()
        waitUntilTextIsShown(firstDayQuote.translation)

        epochDayProvider.currentEpochDay = SECOND_EPOCH_DAY
        val activityScenario = composeTestRule.activityRule.scenario
        activityScenario.moveToState(Lifecycle.State.CREATED)
        activityScenario.moveToState(Lifecycle.State.RESUMED)

        waitUntilTextIsShown(secondDayQuote.text)
        composeTestRule.onNodeWithText(secondDayQuote.text).assertIsDisplayed()
    }

    @Test
    fun retryAfterErrorShowsTheQuote() {
        showRoute()
        val errorMessage = composeTestRule.activity.getString(R.string.daily_quote_error)
        waitUntilTextIsShown(errorMessage)

        dailyQuoteRepository.dailyQuotesByEpochDay[FIRST_EPOCH_DAY] = firstDayQuote
        val retryLabel = composeTestRule.activity.getString(R.string.daily_quote_retry)
        composeTestRule.onNodeWithText(retryLabel).performClick()

        waitUntilTextIsShown(firstDayQuote.translation)
        composeTestRule.onNodeWithText(firstDayQuote.translation).assertIsDisplayed()
    }

    /** Builds the ViewModel by hand with fakes, so the test needs no Hilt graph. */
    private fun showRoute() {
        val viewModel = DailyQuoteViewModel(
            dailyQuoteRepository = dailyQuoteRepository,
            epochDayProvider = epochDayProvider,
            dayChangeSource = FakeDayChangeSource(),
        )
        composeTestRule.setContent {
            AlQuranQuotesTheme {
                DailyQuoteRoute(viewModel = viewModel)
            }
        }
    }

    private fun waitUntilTextIsShown(text: String) {
        composeTestRule.waitUntil(timeoutMillis = TEXT_TIMEOUT_MILLIS) {
            val textNodes = composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes()
            textNodes.isNotEmpty()
        }
    }
}
