package shibbir.me.alquranquotes.data.local

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteOrigin

/** Which origin a quote has after the user edits it. */
class QuoteOriginAfterEditTest {

    @Test
    fun untouchedBundledAyahBecomesEditedBundled() {
        assertEquals(QuoteOrigin.EDITED_BUNDLED, QuoteOrigin.BUNDLED.afterEdit())
    }

    @Test
    fun editedUserQuoteStaysUser() {
        assertEquals(QuoteOrigin.USER, QuoteOrigin.USER.afterEdit())
    }

    @Test
    fun editedBundledAyahStaysEditedBundled() {
        assertEquals(QuoteOrigin.EDITED_BUNDLED, QuoteOrigin.EDITED_BUNDLED.afterEdit())
    }
}
