package shibbir.me.alquranquotes.data.local

import shibbir.me.alquranquotes.model.QuoteOrigin

/**
 * The origin a quote has once the user edits it: an untouched bundled ayah becomes
 * [QuoteOrigin.EDITED_BUNDLED], so seed updates stop refreshing it. Other origins stay.
 */
internal fun QuoteOrigin.afterEdit(): QuoteOrigin {
    return when (this) {
        QuoteOrigin.BUNDLED -> QuoteOrigin.EDITED_BUNDLED
        QuoteOrigin.EDITED_BUNDLED -> QuoteOrigin.EDITED_BUNDLED
        QuoteOrigin.USER -> QuoteOrigin.USER
    }
}
