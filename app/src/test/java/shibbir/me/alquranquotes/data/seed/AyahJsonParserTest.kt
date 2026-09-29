package shibbir.me.alquranquotes.data.seed

import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import shibbir.me.alquranquotes.testing.ashSharhSampleAyah
import shibbir.me.alquranquotes.testing.readBundledAyahSeedJson

class AyahJsonParserTest {

    /** Holds exactly the fields of [ashSharhSampleAyah]. */
    private val singleAyahSeedJson = """
        {
          "version": 3,
          "ayahs": [
            {
              "surah": 94,
              "ayah": 5,
              "surahNameEnglish": "Ash-Sharh",
              "surahNameArabic": "surah-name-arabic",
              "arabic": "arabic-text",
              "translation": "translation-text"
            }
          ]
        }
    """.trimIndent()

    @Test
    fun parsesAyahFieldsAndVersion() {
        val ayahSeed = parseAyahSeedJson(singleAyahSeedJson)

        val expectedAyahSeed = AyahSeed(version = 3, ayahs = listOf(ashSharhSampleAyah()))
        assertEquals(expectedAyahSeed, ayahSeed)
    }

    @Test
    fun emptyAyahListParsesToEmptyList() {
        val ayahSeed = parseAyahSeedJson("""{ "version": 1, "ayahs": [] }""")

        assertTrue(ayahSeed.ayahs.isEmpty())
    }

    @Test
    fun missingAyahFieldThrowsJsonException() {
        val ayahSeedJson = """{ "version": 1, "ayahs": [ { "surah": 1 } ] }"""

        assertThrows(JSONException::class.java) {
            parseAyahSeedJson(ayahSeedJson)
        }
    }

    @Test
    fun wrongAyahFieldTypeThrowsJsonException() {
        val ayahSeedJson = singleAyahSeedJson.replace("\"surah\": 94", "\"surah\": \"x\"")

        assertThrows(JSONException::class.java) {
            parseAyahSeedJson(ayahSeedJson)
        }
    }

    @Test
    fun missingVersionThrowsJsonException() {
        val ayahSeedJson = """{ "ayahs": [] }"""

        assertThrows(JSONException::class.java) {
            parseAyahSeedJson(ayahSeedJson)
        }
    }

    @Test
    fun bundledAssetIsValid() {
        val bundledAyahSeedJson = readBundledAyahSeedJson()

        val ayahSeed = parseAyahSeedJson(bundledAyahSeedJson)

        val ayahs = ayahSeed.ayahs
        val uniqueAyahKeys = ayahs.map { it.surahNumber to it.ayahNumber }.toSet()
        assertTrue(ayahs.isNotEmpty())
        assertTrue(ayahSeed.version >= 1)
        assertEquals(ayahs.size, uniqueAyahKeys.size)
        ayahs.forEach { ayah ->
            assertTrue(ayah.surahNumber in 1..114)
            assertTrue(ayah.ayahNumber >= 1)
            assertFalse(ayah.surahNameEnglish.isBlank())
            assertFalse(ayah.surahNameArabic.isBlank())
            assertFalse(ayah.arabicText.isBlank())
            assertFalse(ayah.translation.isBlank())
        }
    }
}
