package shibbir.me.alquranquotes.feature.dailyquote

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

@RunWith(AndroidJUnit4::class)
class DailyQuoteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val ayah = Ayah(
        surahNumber = 94,
        ayahNumber = 5,
        surahNameEnglish = "Ash-Sharh",
        surahNameArabic = "sharh-ar",
        arabicText = "arabic-text",
        translation = "For indeed, with hardship [will be] ease.",
    )

    @Test
    fun loadingStateShowsProgressIndicator() {
        setScreen(DailyQuoteUiState.Loading)

        composeTestRule.onNodeWithTag(DAILY_QUOTE_LOADING_TAG).assertExists()
    }

    @Test
    fun successStateShowsAyahTextTranslationAndReference() {
        setScreen(DailyQuoteUiState.Success(ayah))

        composeTestRule.onNodeWithText("arabic-text").assertExists()
        composeTestRule.onNodeWithText("For indeed, with hardship [will be] ease.").assertExists()
        composeTestRule.onNodeWithText("Ash-Sharh 94:5").assertExists()
    }

    @Test
    fun errorStateRetryButtonCallsOnRetry() {
        var retryCount = 0
        setScreen(DailyQuoteUiState.Error, onRetry = { retryCount++ })

        composeTestRule.onNodeWithText("Retry").performClick()

        assertEquals(1, retryCount)
    }

    private fun setScreen(uiState: DailyQuoteUiState, onRetry: () -> Unit = {}) {
        composeTestRule.setContent {
            AlQuranQuotesTheme {
                DailyQuoteScreen(uiState = uiState, onRetry = onRetry)
            }
        }
    }
}
