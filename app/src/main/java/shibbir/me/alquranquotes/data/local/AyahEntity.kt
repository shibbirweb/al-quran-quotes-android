package shibbir.me.alquranquotes.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import shibbir.me.alquranquotes.model.Ayah

@Entity(tableName = "ayahs", primaryKeys = ["surah", "ayah"])
data class AyahEntity(
    val surah: Int,
    val ayah: Int,
    @ColumnInfo(name = "surah_name_english") val surahNameEnglish: String,
    @ColumnInfo(name = "surah_name_arabic") val surahNameArabic: String,
    val arabic: String,
    val translation: String,
)

fun AyahEntity.toAyah() = Ayah(
    surahNumber = surah,
    ayahNumber = ayah,
    surahNameEnglish = surahNameEnglish,
    surahNameArabic = surahNameArabic,
    arabicText = arabic,
    translation = translation,
)
