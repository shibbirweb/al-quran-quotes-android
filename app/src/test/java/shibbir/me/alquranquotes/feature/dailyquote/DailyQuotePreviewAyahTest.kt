package shibbir.me.alquranquotes.feature.dailyquote

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.data.seed.AYAHS_ASSET_NAME
import shibbir.me.alquranquotes.data.seed.parseAyahSeedJson
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.testing.readBundledAyahSeedJson

/** Guards the never-retype rule: the preview sample must be an exact copy of the bundled ayah. */
class DailyQuotePreviewAyahTest {

    @Test
    fun previewAyahIsAnExactCopyOfTheBundledAyah() {
        val assetAyah = findAssetAyah(surahNumber = 94, ayahNumber = 5)

        assertEquals(assetAyah, dailyQuotePreviewAyah)
    }

    private fun findAssetAyah(surahNumber: Int, ayahNumber: Int): Ayah {
        val assetJson = readBundledAyahSeedJson()
        val assetAyahSeed = parseAyahSeedJson(assetJson)
        val assetAyah = assetAyahSeed.ayahs.firstOrNull { ayah ->
            ayah.surahNumber == surahNumber && ayah.ayahNumber == ayahNumber
        }
        return checkNotNull(assetAyah) {
            "$AYAHS_ASSET_NAME has no ayah $surahNumber:$ayahNumber"
        }
    }
}
