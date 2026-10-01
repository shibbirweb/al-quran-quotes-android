package shibbir.me.alquranquotes.feature.quotes

import androidx.annotation.StringRes
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin

/** Which kind of quote a card in the Quotes list shows, with the label the card shows for it. */
enum class QuoteListKind(@param:StringRes val labelResId: Int) {
    BUNDLED_AYAH(R.string.quotes_kind_bundled_ayah),
    EDITED_BUNDLED_AYAH(R.string.quotes_kind_edited_bundled_ayah),
    BUNDLED_FREE_TEXT(R.string.quotes_kind_bundled_free_text),
    EDITED_BUNDLED_FREE_TEXT(R.string.quotes_kind_edited_bundled_free_text),
    USER_AYAH(R.string.quotes_kind_user_ayah),
    USER_FREE_TEXT(R.string.quotes_kind_user_free_text),
}

/** The kind of this quote, from where it came from and whether it is an ayah. */
fun Quote.toQuoteListKind(): QuoteListKind {
    return when (this) {
        is Quote.AyahQuote -> ayahKindOf(origin)
        is Quote.FreeTextQuote -> freeTextKindOf(origin)
    }
}

private fun ayahKindOf(origin: QuoteOrigin): QuoteListKind {
    return when (origin) {
        QuoteOrigin.BUNDLED -> QuoteListKind.BUNDLED_AYAH
        QuoteOrigin.EDITED_BUNDLED -> QuoteListKind.EDITED_BUNDLED_AYAH
        QuoteOrigin.USER -> QuoteListKind.USER_AYAH
    }
}

private fun freeTextKindOf(origin: QuoteOrigin): QuoteListKind {
    return when (origin) {
        QuoteOrigin.BUNDLED -> QuoteListKind.BUNDLED_FREE_TEXT
        QuoteOrigin.EDITED_BUNDLED -> QuoteListKind.EDITED_BUNDLED_FREE_TEXT
        QuoteOrigin.USER -> QuoteListKind.USER_FREE_TEXT
    }
}
