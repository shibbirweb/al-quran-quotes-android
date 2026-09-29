package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.local.AyahEntity

/**
 * Device-test copy of the unit-test builder, because `src/androidTest` cannot share code with
 * `src/test`.
 */
fun testAyahEntity(
    surahNumber: Int,
    ayahNumber: Int,
    translation: String = "translation-$surahNumber-$ayahNumber",
) = AyahEntity(
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    surahNameEnglish = "Surah $surahNumber",
    surahNameArabic = "surah-ar-$surahNumber",
    arabicText = "arabic-$surahNumber-$ayahNumber",
    translation = translation,
)
