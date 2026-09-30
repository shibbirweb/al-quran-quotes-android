package shibbir.me.alquranquotes.data.local

import shibbir.me.alquranquotes.model.QuoteOrigin

/**
 * The one order of the Quotes list and the daily rotation: bundled ayahs (edited or not) first,
 * by surah number then ayah number, then the user's quotes by id, so oldest first. Rows that tie
 * keep the lower id first.
 */
internal val quoteDisplayOrder = compareBy<QuoteEntity>(
    { quoteEntity -> quoteEntity.origin == QuoteOrigin.USER },
    { quoteEntity -> bundledSortNumber(quoteEntity, quoteEntity.surahNumber) },
    { quoteEntity -> bundledSortNumber(quoteEntity, quoteEntity.ayahNumber) },
    { quoteEntity -> quoteEntity.id },
)

/** User quotes sort by id only, so their surah and ayah numbers are ignored. */
private fun bundledSortNumber(quoteEntity: QuoteEntity, number: Int?): Int? {
    if (quoteEntity.origin == QuoteOrigin.USER) {
        return null
    }
    return number
}
