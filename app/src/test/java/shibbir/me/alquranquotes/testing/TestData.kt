package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.local.AyahEntity

fun testAyahEntity(surah: Int, ayah: Int) = AyahEntity(
    surah = surah,
    ayah = ayah,
    surahNameEnglish = "Surah $surah",
    surahNameArabic = "surah-ar-$surah",
    arabic = "arabic-$surah-$ayah",
    translation = "translation-$surah-$ayah",
)
