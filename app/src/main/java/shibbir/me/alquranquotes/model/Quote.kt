package shibbir.me.alquranquotes.model

/**
 * A quote the app shows. Every quote has the id of its database row, so any quote, bundled or
 * the user's own, can be edited and deleted. [origin] tells where it came from.
 */
sealed interface Quote {

    val quoteId: Long

    val origin: QuoteOrigin

    /** An ayah with a translation in any language. Bundled ayahs are always this kind. */
    data class AyahQuote(
        override val quoteId: Long,
        override val origin: QuoteOrigin,
        val surahName: String,
        val surahNumber: Int,
        val ayahNumber: Int,
        val arabicText: String,
        val translation: String,
    ) : Quote

    /** Free text in any language, with an optional reference (may be blank). */
    data class FreeTextQuote(
        override val quoteId: Long,
        override val origin: QuoteOrigin,
        val text: String,
        val reference: String,
    ) : Quote
}
