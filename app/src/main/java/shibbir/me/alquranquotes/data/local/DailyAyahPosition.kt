package shibbir.me.alquranquotes.data.local

/**
 * Maps [epochDay] to a position in a list of [ayahCount] quotes. Every day gets one quote, and the
 * list repeats after [ayahCount] days. Days before the epoch still map to a valid position. The
 * daily quote passes the count of all quotes, bundled ayahs plus user quotes, as [ayahCount] and
 * walks them in [quoteDisplayOrder] (see [DailyQuoteDao.getDailyQuote]).
 *
 * @throws IllegalArgumentException when [ayahCount] is not positive.
 */
internal fun dailyAyahPosition(epochDay: Long, ayahCount: Int): Int {
    require(ayahCount > 0) { "ayahCount must be positive" }
    val ayahCountAsLong = ayahCount.toLong()
    val position = epochDay.mod(ayahCountAsLong)
    return position.toInt()
}
