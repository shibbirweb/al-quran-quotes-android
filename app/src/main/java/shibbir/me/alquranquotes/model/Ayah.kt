package shibbir.me.alquranquotes.model

data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahNameEnglish: String,
    val surahNameArabic: String,
    val arabicText: String,
    val translation: String,
)
