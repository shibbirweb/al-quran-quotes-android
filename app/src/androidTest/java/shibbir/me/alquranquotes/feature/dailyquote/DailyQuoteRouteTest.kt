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
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

private const val FIRST_EPOCH_DAY = 100L

private const val SECOND_EPOCH_DAY = 101L

private const val TEXT_TIMEOUT_MILLIS = 5_000L

/** Checks how [DailyQuoteRoute] wires lifecycle resumes and Retry taps to its ViewModel. */
@RunWith(AndroidJUnit4::class)
class DailyQuoteRouteTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val ayahRepository = FakeAyahRepository()

    private val epochDayProvider = FakeEpochDayProvider(currentEpochDay = FIRST_EPOCH_DAY)

    private val firstDayAyah = Ayah(
        surahNumber = 94,
        ayahNumber = 5,
        surahNameEnglish = "Ash-Sharh",
        surahNameArabic = "sharh-ar",
        arabicText = "first-day-arabic-text",
        translation = "first-day-translation",
    )

    private val secondDayAyah = Ayah(
        surahNumber = 13,
        ayahNumber = 28,
        surahNameEnglish = "Ar-Ra'd",
        surahNameArabic = "rad-ar",
        arabicText = "second-day-arabic-text",
        translation = "second-day-translation",
    )

    @Test
    fun resumingOnANewDayShowsTheNewDaysAyah() {
        ayahRepository.dailyAyahsByEpochDay[FIRST_EPOCH_DAY] = firstDayAyah
        ayahRepository.dailyAyahsByEpochDay[SECOND_EPOCH_DAY] = secondDayAyah
        showRoute()
        waitUntilTextIsShown(firstDayAyah.translation)

        epochDayProvider.currentEpochDay = SECOND_EPOCH_DAY
        val activityScenario = composeTestRule.activityRule.scenario
        activityScenario.moveToState(Lifecycle.State.CREATED)
        activityScenario.moveToState(Lifecycle.State.RESUMED)

        waitUntilTextIsShown(secondDayAyah.translation)
        composeTestRule.onNodeWithText(secondDayAyah.translation).assertIsDisplayed()
    }

    @Test
    fun retryAfterErrorShowsTheAyah() {
        showRoute()
        val errorMessage = composeTestRule.activity.getString(R.string.daily_quote_error)
        waitUntilTextIsShown(errorMessage)

        ayahRepository.dailyAyahsByEpochDay[FIRST_EPOCH_DAY] = firstDayAyah
        val retryLabel = composeTestRule.activity.getString(R.string.daily_quote_retry)
        composeTestRule.onNodeWithText(retryLabel).performClick()

        waitUntilTextIsShown(firstDayAyah.translation)
        composeTestRule.onNodeWithText(firstDayAyah.translation).assertIsDisplayed()
    }

    /** Builds the ViewModel by hand with fakes, so the test needs no Hilt graph. */
    private fun showRoute() {
        val viewModel = DailyQuoteViewModel(
            ayahRepository = ayahRepository,
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
