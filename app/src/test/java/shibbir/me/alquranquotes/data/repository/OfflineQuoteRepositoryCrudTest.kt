package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.data.local.toQuoteEntity
import shibbir.me.alquranquotes.data.seed.AyahSeed
import shibbir.me.alquranquotes.data.seed.SampleAyahSeeds
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.testAyahDraft
import shibbir.me.alquranquotes.testing.testFreeTextDraft

/** How [OfflineQuoteRepository] adds and deletes quotes of every origin. */
class OfflineQuoteRepositoryCrudTest {

    private val fixture = OfflineQuoteRepositoryTestFixture(storedSampleTables())

    private val quoteTables = fixture.quoteTables

    private val repository = fixture.createRepository()

    @Test
    fun addQuoteStoresNewUserQuotesAndReturnsTheirIds() = runTest {
        val ayahDraft = testAyahDraft()
        val freeTextDraft = testFreeTextDraft()

        val ayahQuoteId = repository.addQuote(ayahDraft)
        val freeTextQuoteId = repository.addQuote(freeTextDraft)

        val expectedAddedRows = listOf(
            ayahDraft.toQuoteEntity(origin = QuoteOrigin.USER).copy(id = 4L),
            freeTextDraft.toQuoteEntity(origin = QuoteOrigin.USER).copy(id = 5L),
        )
        val addedRows = quoteTables.storedQuoteEntities.drop(3)
        assertEquals(4L, ayahQuoteId)
        assertEquals(5L, freeTextQuoteId)
        assertEquals(expectedAddedRows, addedRows)
    }

    @Test
    fun deleteQuoteRemovesAUserQuote() = runTest {
        repository.deleteQuote(3L)

        val remainingQuoteIds = quoteTables.storedQuoteEntities.map { it.id }
        assertEquals(listOf(1L, 2L), remainingQuoteIds)
    }

    /** A later app start runs a newer seed that still contains 94:5. */
    @Test
    fun deletedBundledAyahStaysDeletedAfterANewerSeed() = runTest {
        repository.deleteQuote(1L)
        val newerAyahSeed = AyahSeed(version = 2, ayahs = SampleAyahSeeds.seedAyahs)
        val laterFixture = OfflineQuoteRepositoryTestFixture(
            quoteTables = quoteTables,
            ayahSeedSource = FakeAyahSeedSource(newerAyahSeed),
        )

        val quotesAfterNewerSeed = laterFixture.createRepository().observeQuotes().first()

        val quoteIdsAfterNewerSeed = quotesAfterNewerSeed.map { quote -> quote.quoteId }
        assertEquals(listOf(2L, 3L), quoteIdsAfterNewerSeed)
    }
}
