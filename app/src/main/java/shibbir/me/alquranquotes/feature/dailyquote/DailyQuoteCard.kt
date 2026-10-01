package shibbir.me.alquranquotes.feature.dailyquote

import shibbir.me.alquranquotes.model.Quote

/**
 * What the daily quote card shows, worked out from a [Quote]. Keeping these rules out of the
 * composables lets plain unit tests check them.
 */
sealed interface DailyQuoteCard {

    /** An ayah. [originNote] is the note under the title, or null when it needs none. */
    data class AyahCard(
        val arabicText: String,
        val translation: String,
        val surahName: String,
        val surahNumber: Int,
        val ayahNumber: Int,
        val originNote: DailyQuoteOriginNote?,
    ) : DailyQuoteCard

    /** A free text quote. [reference] is null when there is none; [originNote] as for ayahs. */
    data class FreeTextCard(
        val text: String,
        val reference: String?,
        val originNote: DailyQuoteOriginNote?,
    ) : DailyQuoteCard
}

fun Quote.toDailyQuoteCard(): DailyQuoteCard {
    return when (this) {
        is Quote.AyahQuote -> toAyahCard()
        is Quote.FreeTextQuote -> toFreeTextCard()
    }
}

private fun Quote.AyahQuote.toAyahCard() = DailyQuoteCard.AyahCard(
    arabicText = arabicText,
    translation = translation,
    surahName = surahName,
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    originNote = origin.toDailyQuoteOriginNote(),
)

private fun Quote.FreeTextQuote.toFreeTextCard() = DailyQuoteCard.FreeTextCard(
    text = text,
    reference = shownReference(reference),
    originNote = origin.toDailyQuoteOriginNote(),
)

/** Returns null for a blank [reference], so the card leaves the reference out. */
private fun shownReference(reference: String): String? {
    if (reference.isBlank()) {
        return null
    }
    return reference
}
