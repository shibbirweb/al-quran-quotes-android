package shibbir.me.alquranquotes.data.seed

import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import shibbir.me.alquranquotes.data.local.AyahEntity
import java.io.File

class AyahJsonParserTest {

    @Test
    fun parsesAyahFields() {
        val json = """
            {
              "ayahs": [
                {
                  "surah": 94,
                  "ayah": 5,
                  "surahNameEnglish": "Ash-Sharh",
                  "surahNameArabic": "sharh-ar",
                  "arabic": "arabic-text",
                  "translation": "For indeed, with hardship [will be] ease."
                }
              ]
            }
        """.trimIndent()

        val ayahs = parseAyahsJson(json)

        assertEquals(
            listOf(
                AyahEntity(
                    surah = 94,
                    ayah = 5,
                    surahNameEnglish = "Ash-Sharh",
                    surahNameArabic = "sharh-ar",
                    arabic = "arabic-text",
                    translation = "For indeed, with hardship [will be] ease.",
                ),
            ),
            ayahs,
        )
    }

    @Test
    fun emptyAyahListParsesToEmptyList() {
        assertTrue(parseAyahsJson("""{ "ayahs": [] }""").isEmpty())
    }

    @Test
    fun missingFieldFails() {
        val json = """{ "ayahs": [ { "surah": 1 } ] }"""

        assertThrows(JSONException::class.java) {
            parseAyahsJson(json)
        }
    }

    @Test
    fun bundledAssetIsValid() {
        val ayahs = parseAyahsJson(File("src/main/assets/$AYAHS_ASSET_NAME").readText())

        assertTrue(ayahs.isNotEmpty())
        assertEquals(ayahs.size, ayahs.map { it.surah to it.ayah }.toSet().size)
        ayahs.forEach { ayah ->
            assertTrue(ayah.surah in 1..114)
            assertTrue(ayah.ayah >= 1)
            assertFalse(ayah.surahNameEnglish.isBlank())
            assertFalse(ayah.surahNameArabic.isBlank())
            assertFalse(ayah.arabic.isBlank())
            assertFalse(ayah.translation.isBlank())
        }
    }
}
