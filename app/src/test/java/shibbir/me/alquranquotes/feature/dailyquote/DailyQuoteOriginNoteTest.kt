package shibbir.me.alquranquotes.feature.dailyquote

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.QuoteOrigin

/** The small note the daily quote card shows under its title, for every origin. */
class DailyQuoteOriginNoteTest {

    @Test
    fun anUnchangedBundledQuoteHasNoNote() {
        assertEquals(null, QuoteOrigin.BUNDLED.toDailyQuoteOriginNote())
    }

    @Test
    fun anEditedBundledQuoteSaysItWasEdited() {
        val originNote = QuoteOrigin.EDITED_BUNDLED.toDailyQuoteOriginNote()

        assertEquals(DailyQuoteOriginNote.EDITED, originNote)
    }

    @Test
    fun theUsersOwnQuoteSaysItIsTheirs() {
        assertEquals(DailyQuoteOriginNote.YOUR_QUOTE, QuoteOrigin.USER.toDailyQuoteOriginNote())
    }

    @Test
    fun notesShowTheirLabels() {
        val labelResIds = DailyQuoteOriginNote.entries.map { originNote -> originNote.labelResId }

        val expectedLabelResIds = listOf(
            R.string.daily_quote_user_quote_label,
            R.string.daily_quote_edited_label,
        )
        assertEquals(expectedLabelResIds, labelResIds)
    }
}
