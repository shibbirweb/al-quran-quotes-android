package shibbir.me.alquranquotes.data.seed

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import shibbir.me.alquranquotes.data.local.AyahEntity
import javax.inject.Inject

const val AYAHS_ASSET_NAME = "ayahs.json"

/** Provides the ayahs used to fill an empty database. */
interface AyahSeedSource {
    suspend fun load(): List<AyahEntity>
}

/** Reads the ayahs bundled in `assets/ayahs.json`. Callers are expected to run it off the main thread. */
class AssetAyahSeedSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AyahSeedSource {
    override suspend fun load(): List<AyahEntity> =
        context.assets.open(AYAHS_ASSET_NAME).bufferedReader().use { reader ->
            parseAyahsJson(reader.readText())
        }
}

internal fun parseAyahsJson(json: String): List<AyahEntity> {
    val ayahs = JSONObject(json).getJSONArray("ayahs")
    return List(ayahs.length()) { index ->
        val ayah = ayahs.getJSONObject(index)
        AyahEntity(
            surah = ayah.getInt("surah"),
            ayah = ayah.getInt("ayah"),
            surahNameEnglish = ayah.getString("surahNameEnglish"),
            surahNameArabic = ayah.getString("surahNameArabic"),
            arabic = ayah.getString("arabic"),
            translation = ayah.getString("translation"),
        )
    }
}
