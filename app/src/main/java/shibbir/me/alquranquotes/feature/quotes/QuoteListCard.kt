package shibbir.me.alquranquotes.feature.quotes

import shibbir.me.alquranquotes.model.Quote

/**
 * What one card in the Quotes list shows, worked out from a [Quote]. Keeping these rules out of
 * the composables lets plain unit tests check them. Every card can be edited and deleted.
 */
sealed interface QuoteListCard {

    /** The id to edit or delete the quote with; unique across the whole list. */
    val quoteId: Long

    val kind: QuoteListKind

    data class AyahCard(
        override val quoteId: Long,
        override val kind: QuoteListKind,
        val arabicText: String,
        val translation: String,
        val surahName: String,
        val surahNumber: Int,
        val ayahNumber: Int,
    ) : QuoteListCard

    /** A free text quote. [reference] is null when there is none. */
    data class FreeTextCard(
        override val quoteId: Long,
        override val kind: QuoteListKind,
        val text: String,
        val reference: String?,
    ) : QuoteListCard
}

fun Quote.toQuoteListCard(): QuoteListCard {
    return when (this) {
        is Quote.AyahQuote -> toAyahCard()
        is Quote.FreeTextQuote -> toFreeTextCard()
    }
}

private fun Quote.AyahQuote.toAyahCard() = QuoteListCard.AyahCard(
    quoteId = quoteId,
    kind = toQuoteListKind(),
    arabicText = arabicText,
    translation = translation,
    surahName = surahName,
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
)

private fun Quote.FreeTextQuote.toFreeTextCard() = QuoteListCard.FreeTextCard(
    quoteId = quoteId,
    kind = toQuoteListKind(),
    text = text,
    reference = shownReference(reference),
)

/** Returns null for a blank [reference], so the card leaves the reference out. */
private fun shownReference(reference: String): String? {
    if (reference.isBlank()) {
        return null
    }
    return reference
}
