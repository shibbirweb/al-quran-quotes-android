package shibbir.me.alquranquotes.data.local

/**
 * Maps [epochDay] to a position in the ayahs ordered by surah, then ayah number. Every day gets
 * one ayah, and the list repeats after [ayahCount] days. Days before the epoch still map to a
 * valid position.
 *
 * @throws IllegalArgumentException when [ayahCount] is not positive.
 */
internal fun dailyAyahPosition(epochDay: Long, ayahCount: Int): Int {
    require(ayahCount > 0) { "ayahCount must be positive" }
    val ayahCountAsLong = ayahCount.toLong()
    val position = epochDay.mod(ayahCountAsLong)
    return position.toInt()
}
