package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.local.AyahEntity
import shibbir.me.alquranquotes.model.Ayah

fun testAyah(
    surahNumber: Int,
    ayahNumber: Int,
    translation: String = "translation-$surahNumber-$ayahNumber",
) = Ayah(
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    surahNameEnglish = "Surah $surahNumber",
    surahNameArabic = "surah-ar-$surahNumber",
    arabicText = "arabic-$surahNumber-$ayahNumber",
    translation = translation,
)

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

/**
 * Ayah 94:5 of Ash-Sharh with placeholder text, for tests that need one complete, fixed ayah.
 * The Arabic and translation are deliberately fake, so tests never repeat Quran text.
 */
fun ashSharhSampleAyah() = Ayah(
    surahNumber = 94,
    ayahNumber = 5,
    surahNameEnglish = "Ash-Sharh",
    surahNameArabic = "surah-name-arabic",
    arabicText = "arabic-text",
    translation = "translation-text",
)
