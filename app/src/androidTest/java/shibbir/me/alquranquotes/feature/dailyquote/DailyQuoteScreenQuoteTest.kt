package shibbir.me.alquranquotes.feature.dailyquote

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.intl.LocaleList
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.ui.text.ARABIC_LANGUAGE_TAG

/** How the screen shows each kind of quote. */
@RunWith(AndroidJUnit4::class)
class DailyQuoteScreenQuoteTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val ayahTitle = targetContext.getString(R.string.daily_quote_title)

    private val userQuoteLabel = targetContext.getString(R.string.daily_quote_user_quote_label)

    private val arabicLocale = LocaleList(ARABIC_LANGUAGE_TAG)

    private val editedLabel = targetContext.getString(R.string.daily_quote_edited_label)

    private val bundledAyah = Quote.AyahQuote(
        quoteId = 1L,
        origin = QuoteOrigin.BUNDLED,
        surahName = "Ash-Sharh",
        surahNumber = 94,
        ayahNumber = 5,
        arabicText = "arabic-text",
        translation = "translation-text",
    )

    private val userAyah = Quote.AyahQuote(
        quoteId = 2L,
        origin = QuoteOrigin.USER,
        surahName = "user-surah-name",
        surahNumber = 2,
        ayahNumber = 153,
        arabicText = "user-arabic-text",
        translation = "user-translation",
    )

    private val userFreeText = Quote.FreeTextQuote(
        quoteId = 3L,
        origin = QuoteOrigin.USER,
        text = "user-free-text",
        reference = "user-reference",
    )

    @Test
    fun bundledAyahShowsTitleTextsAndReferenceWithoutTheUserLabel() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Success(bundledAyah))

        val reference = ayahReference(surahName = "Ash-Sharh", surahNumber = 94, ayahNumber = 5)
        composeTestRule.onNodeWithText(ayahTitle).assertExists()
        composeTestRule.onNodeWithText("arabic-text").assertExists()
        composeTestRule.onNodeWithText("translation-text").assertExists()
        composeTestRule.onNodeWithText(reference).assertExists()
        composeTestRule.onNodeWithText(userQuoteLabel).assertDoesNotExist()
        composeTestRule.onNodeWithText(editedLabel).assertDoesNotExist()
        val loadingDescription = targetContext.getString(R.string.daily_quote_loading)
        composeTestRule.onNodeWithContentDescription(loadingDescription).assertDoesNotExist()
    }

    @Test
    fun bundledAyahMarksTitleAsHeading() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Success(bundledAyah))

        composeTestRule.onNodeWithText(ayahTitle).assert(isHeading())
    }

    @Test
    fun bundledAyahMarksArabicTextAsArabicForAccessibility() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Success(bundledAyah))

        assertTrue(arabicLocale in spanLocalesOfText("arabic-text"))
    }

    @Test
    fun editedBundledAyahShowsTheEditedNoteWithoutTheUserLabel() {
        val editedAyah = bundledAyah.copy(origin = QuoteOrigin.EDITED_BUNDLED)

        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Success(editedAyah))

        composeTestRule.onNodeWithText("translation-text").assertExists()
        composeTestRule.onNodeWithText(editedLabel).assertExists()
        composeTestRule.onNodeWithText(userQuoteLabel).assertDoesNotExist()
    }

    @Test
    fun userAyahShowsTitleTextsReferenceAndTheUserLabel() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Success(userAyah))

        val reference = ayahReference(
            surahName = "user-surah-name",
            surahNumber = 2,
            ayahNumber = 153,
        )
        composeTestRule.onNodeWithText(ayahTitle).assertExists()
        composeTestRule.onNodeWithText("user-arabic-text").assertExists()
        composeTestRule.onNodeWithText("user-translation").assertExists()
        composeTestRule.onNodeWithText(reference).assertExists()
        composeTestRule.onNodeWithText(userQuoteLabel).assertExists()
    }

    @Test
    fun userAyahMarksArabicTextAsArabicForAccessibility() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Success(userAyah))

        assertTrue(arabicLocale in spanLocalesOfText("user-arabic-text"))
    }

    @Test
    fun userFreeTextShowsHeadingTextReferenceAndTheUserLabel() {
        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Success(userFreeText))

        val freeTextTitle = targetContext.getString(R.string.daily_quote_free_text_title)
        composeTestRule.onNodeWithText(freeTextTitle).assert(isHeading())
        composeTestRule.onNodeWithText("user-free-text").assertExists()
        composeTestRule.onNodeWithText("user-reference").assertExists()
        composeTestRule.onNodeWithText(userQuoteLabel).assertExists()
        composeTestRule.onNodeWithText(ayahTitle).assertDoesNotExist()
    }

    @Test
    fun userFreeTextWithABlankReferenceHidesTheReference() {
        val blankReference = "   "
        val freeTextWithoutReference = userFreeText.copy(reference = blankReference)

        composeTestRule.setDailyQuoteScreen(DailyQuoteUiState.Success(freeTextWithoutReference))

        composeTestRule.onNodeWithText("user-free-text").assertExists()
        composeTestRule.onNodeWithText(blankReference).assertDoesNotExist()
    }

    private fun ayahReference(surahName: String, surahNumber: Int, ayahNumber: Int): String {
        return targetContext.getString(R.string.ayah_reference, surahName, surahNumber, ayahNumber)
    }

    private fun spanLocalesOfText(text: String): List<LocaleList?> {
        val textNode = composeTestRule.onNodeWithText(text).fetchSemanticsNode()
        val nodeTexts = textNode.config[SemanticsProperties.Text]
        val spanStyles = nodeTexts.flatMap { nodeText -> nodeText.spanStyles }
        return spanStyles.map { spanStyleRange -> spanStyleRange.item.localeList }
    }
}
