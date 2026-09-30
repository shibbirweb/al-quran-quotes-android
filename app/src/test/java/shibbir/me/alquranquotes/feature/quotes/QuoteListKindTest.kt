package shibbir.me.alquranquotes.feature.quotes

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.QuoteOrigin

/** The kind label of a Quotes list card, for every origin and kind of quote. */
class QuoteListKindTest {

    @Test
    fun bundledAyahIsABundledAyah() {
        val bundledAyah = sampleAyahQuote(origin = QuoteOrigin.BUNDLED)

        assertEquals(QuoteListKind.BUNDLED_AYAH, bundledAyah.toQuoteListKind())
    }

    @Test
    fun editedBundledAyahIsAnEditedBundledAyah() {
        val editedAyah = sampleAyahQuote(origin = QuoteOrigin.EDITED_BUNDLED)

        assertEquals(QuoteListKind.EDITED_BUNDLED_AYAH, editedAyah.toQuoteListKind())
    }

    @Test
    fun userAyahIsTheUsersAyah() {
        val userAyah = sampleAyahQuote(origin = QuoteOrigin.USER)

        assertEquals(QuoteListKind.USER_AYAH, userAyah.toQuoteListKind())
    }

    @Test
    fun bundledFreeTextIsABundledQuote() {
        val bundledFreeText = sampleFreeTextQuote(origin = QuoteOrigin.BUNDLED)

        assertEquals(QuoteListKind.BUNDLED_FREE_TEXT, bundledFreeText.toQuoteListKind())
    }

    @Test
    fun editedBundledFreeTextIsAnEditedBundledQuote() {
        val editedFreeText = sampleFreeTextQuote(origin = QuoteOrigin.EDITED_BUNDLED)

        assertEquals(QuoteListKind.EDITED_BUNDLED_FREE_TEXT, editedFreeText.toQuoteListKind())
    }

    @Test
    fun userFreeTextIsTheUsersQuote() {
        val userFreeText = sampleFreeTextQuote(origin = QuoteOrigin.USER)

        assertEquals(QuoteListKind.USER_FREE_TEXT, userFreeText.toQuoteListKind())
    }

    @Test
    fun kindsShowTheirLabels() {
        val labelResIds = QuoteListKind.entries.map { quoteListKind -> quoteListKind.labelResId }

        val expectedLabelResIds = listOf(
            R.string.quotes_kind_bundled_ayah,
            R.string.quotes_kind_edited_bundled_ayah,
            R.string.quotes_kind_bundled_free_text,
            R.string.quotes_kind_edited_bundled_free_text,
            R.string.quotes_kind_user_ayah,
            R.string.quotes_kind_user_free_text,
        )
        assertEquals(expectedLabelResIds, labelResIds)
    }
}
