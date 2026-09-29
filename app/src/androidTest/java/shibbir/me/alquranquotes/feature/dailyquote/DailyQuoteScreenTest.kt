package shibbir.me.alquranquotes.feature.dailyquote

import android.content.Context
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.intl.LocaleList
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

@RunWith(AndroidJUnit4::class)
class DailyQuoteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val loadingDescription = targetContext.getString(R.string.daily_quote_loading)

    private val errorMessage = targetContext.getString(R.string.daily_quote_error)

    private val ayah = Ayah(
        surahNumber = 94,
        ayahNumber = 5,
        surahNameEnglish = "Ash-Sharh",
        surahNameArabic = "sharh-ar",
        arabicText = "arabic-text",
        translation = "translation-text",
    )

    @Test
    fun loadingStateShowsDescribedProgressIndicator() {
        setScreen(DailyQuoteUiState.Loading)

        composeTestRule.onNodeWithContentDescription(loadingDescription).assertExists()
    }

    @Test
    fun successStateShowsTitleAyahTextTranslationAndReference() {
        setScreen(DailyQuoteUiState.Success(ayah))

        val title = targetContext.getString(R.string.daily_quote_title)
        val reference = targetContext.getString(
            R.string.ayah_reference,
            ayah.surahNameEnglish,
            ayah.surahNumber,
            ayah.ayahNumber,
        )
        composeTestRule.onNodeWithText(title).assertExists()
        composeTestRule.onNodeWithText(ayah.arabicText).assertExists()
        composeTestRule.onNodeWithText(ayah.translation).assertExists()
        composeTestRule.onNodeWithText(reference).assertExists()
        composeTestRule.onNodeWithContentDescription(loadingDescription).assertDoesNotExist()
    }

    @Test
    fun successStateMarksTitleAsHeading() {
        setScreen(DailyQuoteUiState.Success(ayah))

        val title = targetContext.getString(R.string.daily_quote_title)
        composeTestRule.onNodeWithText(title).assert(isHeading())
    }

    @Test
    fun successStateMarksArabicTextAsArabicForAccessibility() {
        setScreen(DailyQuoteUiState.Success(ayah))

        val arabicNode = composeTestRule.onNodeWithText(ayah.arabicText).fetchSemanticsNode()
        val arabicTexts = arabicNode.config[SemanticsProperties.Text]
        val spanStyles = arabicTexts.flatMap { arabicText -> arabicText.spanStyles }
        val spanLocales = spanStyles.map { spanStyleRange -> spanStyleRange.item.localeList }

        assertTrue(LocaleList(ARABIC_LANGUAGE_TAG) in spanLocales)
    }

    @Test
    fun errorStateShowsErrorMessage() {
        setScreen(DailyQuoteUiState.Error)

        composeTestRule.onNodeWithText(errorMessage).assertExists()
        composeTestRule.onNodeWithContentDescription(loadingDescription).assertDoesNotExist()
    }

    @Test
    fun errorStateAnnouncesErrorMessagePolitely() {
        setScreen(DailyQuoteUiState.Error)

        val isPoliteLiveRegion = SemanticsMatcher.expectValue(
            SemanticsProperties.LiveRegion,
            LiveRegionMode.Polite,
        )
        composeTestRule.onNodeWithText(errorMessage).assert(isPoliteLiveRegion)
    }

    @Test
    fun errorStateScrollsSoRetryStaysReachableAtLargeFontSizes() {
        setScreen(DailyQuoteUiState.Error)

        composeTestRule.onNode(hasScrollAction()).assertExists()
    }

    @Test
    fun errorStateRetryButtonCallsOnRetry() {
        var retryCount = 0
        setScreen(DailyQuoteUiState.Error, onRetry = { retryCount++ })

        val retryLabel = targetContext.getString(R.string.daily_quote_retry)
        composeTestRule.onNodeWithText(retryLabel).performClick()

        assertEquals(1, retryCount)
    }

    private fun setScreen(
        uiState: DailyQuoteUiState,
        onRetry: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            AlQuranQuotesTheme {
                DailyQuoteScreen(
                    uiState = uiState,
                    onRetry = onRetry,
                )
            }
        }
    }
}
