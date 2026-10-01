package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.local.QuoteEntity
import shibbir.me.alquranquotes.data.local.QuoteType
import shibbir.me.alquranquotes.model.QuoteOrigin

/**
 * Device-test copies of the unit-test builders, because `src/androidTest` cannot share code with
 * `src/test`. Every text is a placeholder, so tests never repeat Quran text.
 */
fun testBundledQuoteEntity(
    surahNumber: Int,
    ayahNumber: Int,
    origin: QuoteOrigin = QuoteOrigin.BUNDLED,
    translation: String = "translation-$surahNumber-$ayahNumber",
) = QuoteEntity(
    id = 0L,
    origin = origin,
    type = QuoteType.AYAH,
    bundledKey = "$surahNumber:$ayahNumber",
    surahName = "Surah $surahNumber",
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    arabicText = "arabic-$surahNumber-$ayahNumber",
    translation = translation,
)

/** A new user ayah row (id 0, so Room generates one) with placeholder text. */
fun testUserAyahEntity(
    surahNumber: Int = 94,
    ayahNumber: Int = 5,
) = QuoteEntity(
    id = 0L,
    origin = QuoteOrigin.USER,
    type = QuoteType.AYAH,
    surahName = "Surah $surahNumber",
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    arabicText = "user-arabic-$surahNumber-$ayahNumber",
    translation = "user-translation-$surahNumber-$ayahNumber",
)

/** A new user free text row (id 0, so Room generates one). */
fun testUserFreeTextEntity(
    text: String = "free text",
    reference: String = "a reference",
) = QuoteEntity(
    id = 0L,
    origin = QuoteOrigin.USER,
    type = QuoteType.FREE_TEXT,
    freeText = text,
    reference = reference,
)
