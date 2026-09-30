package shibbir.me.alquranquotes.data.seed

import shibbir.me.alquranquotes.data.local.QuoteEntity
import shibbir.me.alquranquotes.data.local.QuoteType
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.model.QuoteOrigin

/**
 * Maps this seed ayah to a new, untouched bundled row. Its bundled key, such as "94:5", comes
 * from the seed numbers and stays fixed even when the user later edits the row's numbers.
 */
internal fun Ayah.toBundledQuoteEntity() = QuoteEntity(
    id = 0L,
    origin = QuoteOrigin.BUNDLED,
    type = QuoteType.AYAH,
    bundledKey = "$surahNumber:$ayahNumber",
    surahName = surahNameEnglish,
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    arabicText = arabicText,
    translation = translation,
)
