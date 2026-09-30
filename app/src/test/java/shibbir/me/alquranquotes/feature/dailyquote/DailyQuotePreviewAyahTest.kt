package shibbir.me.alquranquotes.feature.dailyquote

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.data.seed.AYAHS_ASSET_NAME
import shibbir.me.alquranquotes.data.seed.parseAyahSeedJson
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.readBundledAyahSeedJson

/** Guards the never-retype rule: the preview samples must be exact copies of the bundled ayah. */
class DailyQuotePreviewAyahTest {

    @Test
    fun previewAyahIsAnExactCopyOfTheBundledAyah() {
        val assetAyah = findAssetAyah(surahNumber = 94, ayahNumber = 5)

        assertEquals(assetAyah, dailyQuotePreviewAyah)
    }

    @Test
    fun previewQuoteIsABundledAyahQuoteCopiedFromThePreviewAyah() {
        val assetAyah = findAssetAyah(surahNumber = 94, ayahNumber = 5)

        val previewQuote = dailyQuotePreviewQuote()

        val expectedQuote = Quote.AyahQuote(
            quoteId = previewQuote.quoteId,
            origin = QuoteOrigin.BUNDLED,
            surahName = assetAyah.surahNameEnglish,
            surahNumber = assetAyah.surahNumber,
            ayahNumber = assetAyah.ayahNumber,
            arabicText = assetAyah.arabicText,
            translation = assetAyah.translation,
        )
        assertEquals(expectedQuote, previewQuote)
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
