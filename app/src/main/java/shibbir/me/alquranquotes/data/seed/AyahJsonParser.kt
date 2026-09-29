package shibbir.me.alquranquotes.data.seed

import org.json.JSONObject
import shibbir.me.alquranquotes.model.Ayah

/**
 * Parses the ayah seed JSON: a top-level integer `version` and an `ayahs` array of objects with
 * `surah`, `ayah`, `surahNameEnglish`, `surahNameArabic`, `arabic`, and `translation`.
 *
 * @throws org.json.JSONException when the JSON is malformed, or `version`, `ayahs`, or any ayah
 * field is missing or has the wrong type.
 */
internal fun parseAyahSeedJson(ayahSeedJson: String): AyahSeed {
    val ayahSeedJsonObject = JSONObject(ayahSeedJson)
    val seedVersion = ayahSeedJsonObject.getInt("version")
    val ayahsJsonArray = ayahSeedJsonObject.getJSONArray("ayahs")
    val ayahs = List(ayahsJsonArray.length()) { index ->
        val ayahJson = ayahsJsonArray.getJSONObject(index)
        parseAyahJson(ayahJson)
    }
    return AyahSeed(
        version = seedVersion,
        ayahs = ayahs,
    )
}

private fun parseAyahJson(ayahJson: JSONObject) = Ayah(
    surahNumber = ayahJson.getInt("surah"),
    ayahNumber = ayahJson.getInt("ayah"),
    surahNameEnglish = ayahJson.getString("surahNameEnglish"),
    surahNameArabic = ayahJson.getString("surahNameArabic"),
    arabicText = ayahJson.getString("arabic"),
    translation = ayahJson.getString("translation"),
)
