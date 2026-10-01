package shibbir.me.alquranquotes.feature.dailyquote

import androidx.annotation.StringRes
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.QuoteOrigin

/** A small note under the daily quote card's title that says where the quote came from. */
enum class DailyQuoteOriginNote(@param:StringRes val labelResId: Int) {
    YOUR_QUOTE(R.string.daily_quote_user_quote_label),
    EDITED(R.string.daily_quote_edited_label),
}

/** The note for a quote of this origin, or null when the card needs none. */
fun QuoteOrigin.toDailyQuoteOriginNote(): DailyQuoteOriginNote? {
    return when (this) {
        QuoteOrigin.BUNDLED -> null
        QuoteOrigin.EDITED_BUNDLED -> DailyQuoteOriginNote.EDITED
        QuoteOrigin.USER -> DailyQuoteOriginNote.YOUR_QUOTE
    }
}
