package shibbir.me.alquranquotes.data.local

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.testBundledQuoteEntity
import shibbir.me.alquranquotes.testing.testUserAyahEntity
import shibbir.me.alquranquotes.testing.testUserFreeTextEntity

/** The one order of the Quotes list and the daily rotation. */
class QuoteDisplayOrderTest {

    @Test
    fun bundledAyahsAreOrderedBySurahThenAyahNumber() {
        val ayah94v5 = testBundledQuoteEntity(quoteId = 1L, surahNumber = 94, ayahNumber = 5)
        val ayah2v286 = testBundledQuoteEntity(quoteId = 2L, surahNumber = 2, ayahNumber = 286)
        val ayah2v153 = testBundledQuoteEntity(quoteId = 3L, surahNumber = 2, ayahNumber = 153)

        val orderedQuotes = listOf(ayah94v5, ayah2v286, ayah2v153).sortedWith(quoteDisplayOrder)

        assertEquals(listOf(ayah2v153, ayah2v286, ayah94v5), orderedQuotes)
    }

    /** A user ayah with a low surah number still comes after every bundled ayah. */
    @Test
    fun userQuotesComeAfterEveryBundledAyahByIdWhateverTheirNumbers() {
        val bundledAyah = testBundledQuoteEntity(quoteId = 5L, surahNumber = 94, ayahNumber = 5)
        val newerUserAyah = testUserAyahEntity(quoteId = 4L, surahNumber = 1, ayahNumber = 1)
        val olderUserFreeText = testUserFreeTextEntity(quoteId = 2L)
        val quotes = listOf(newerUserAyah, bundledAyah, olderUserFreeText)

        val orderedQuotes = quotes.sortedWith(quoteDisplayOrder)

        assertEquals(listOf(bundledAyah, olderUserFreeText, newerUserAyah), orderedQuotes)
    }

    /** The user moved bundled ayah 94:5 to 2:153, so it now ties with the untouched 2:153. */
    @Test
    fun editedBundledAyahsSortByTheirCurrentNumbersAndTiesById() {
        val untouchedAyah = testBundledQuoteEntity(quoteId = 3L, surahNumber = 2, ayahNumber = 153)
        val otherUntouchedAyah = testBundledQuoteEntity(
            quoteId = 2L,
            surahNumber = 13,
            ayahNumber = 28,
        )
        val editedAyah = testBundledQuoteEntity(
            quoteId = 1L,
            surahNumber = 2,
            ayahNumber = 153,
            origin = QuoteOrigin.EDITED_BUNDLED,
        ).copy(bundledKey = "94:5")
        val quotes = listOf(otherUntouchedAyah, untouchedAyah, editedAyah)

        val orderedQuotes = quotes.sortedWith(quoteDisplayOrder)

        assertEquals(listOf(editedAyah, untouchedAyah, otherUntouchedAyah), orderedQuotes)
    }
}
