package shibbir.me.alquranquotes.data.seed

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.data.local.QuoteEntity
import shibbir.me.alquranquotes.data.local.QuoteType
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.ashSharhSampleAyah

/** How a bundled seed ayah maps to a new, untouched quote row. */
class BundledQuoteEntityMappingTest {

    @Test
    fun seedAyahMapsToNewBundledAyahRowKeyedBySurahAndAyah() {
        val quoteEntity = ashSharhSampleAyah().toBundledQuoteEntity()

        val expectedQuoteEntity = QuoteEntity(
            id = 0L,
            origin = QuoteOrigin.BUNDLED,
            type = QuoteType.AYAH,
            bundledKey = "94:5",
            surahName = "Ash-Sharh",
            surahNumber = 94,
            ayahNumber = 5,
            arabicText = "arabic-text",
            translation = "translation-text",
        )
        assertEquals(expectedQuoteEntity, quoteEntity)
    }
}
