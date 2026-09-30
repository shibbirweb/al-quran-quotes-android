package shibbir.me.alquranquotes.data.repository

import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.FakeQuoteTables
import shibbir.me.alquranquotes.testing.testBundledQuoteEntity
import shibbir.me.alquranquotes.testing.testUserFreeTextEntity

/**
 * Tables that already hold the current seed (version 1), so the repository does not merge it
 * again: bundled 94:5 (id 1, untouched), bundled 13:28 (id 2, edited), and a user free text
 * quote (id 3).
 */
fun storedSampleTables(): FakeQuoteTables {
    val storedQuoteEntities = listOf(
        testBundledQuoteEntity(quoteId = 1L, surahNumber = 94, ayahNumber = 5),
        testBundledQuoteEntity(
            quoteId = 2L,
            surahNumber = 13,
            ayahNumber = 28,
            origin = QuoteOrigin.EDITED_BUNDLED,
            translation = "edited translation",
        ),
        testUserFreeTextEntity(quoteId = 3L),
    )
    return FakeQuoteTables(
        initialQuoteEntities = storedQuoteEntities,
        initialSeededBundledKeys = setOf("94:5", "2:153", "13:28"),
        initialSeedVersion = 1,
    )
}
