package shibbir.me.alquranquotes.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import shibbir.me.alquranquotes.model.Ayah

@Entity(
    tableName = "ayahs",
    primaryKeys = ["surah_number", "ayah_number"],
)
data class AyahEntity(
    @ColumnInfo(name = "surah_number") val surahNumber: Int,
    @ColumnInfo(name = "ayah_number") val ayahNumber: Int,
    @ColumnInfo(name = "surah_name_english") val surahNameEnglish: String,
    @ColumnInfo(name = "surah_name_arabic") val surahNameArabic: String,
    @ColumnInfo(name = "arabic_text") val arabicText: String,
    @ColumnInfo(name = "translation") val translation: String,
)

fun AyahEntity.toAyah() = Ayah(
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    surahNameEnglish = surahNameEnglish,
    surahNameArabic = surahNameArabic,
    arabicText = arabicText,
    translation = translation,
)

fun Ayah.toAyahEntity() = AyahEntity(
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    surahNameEnglish = surahNameEnglish,
    surahNameArabic = surahNameArabic,
    arabicText = arabicText,
    translation = translation,
)
