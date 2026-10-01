package shibbir.me.alquranquotes.data.seed

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource
import shibbir.me.alquranquotes.testing.FakeBundledAyahSeedDao
import shibbir.me.alquranquotes.testing.FakeQuoteTables
import shibbir.me.alquranquotes.testing.testBundledQuoteEntity

/** When [BundledAyahSeeder] merges the bundled seed, and which version it records. */
class BundledAyahSeederVersionTest {

    @Test
    fun doesNotMergeWhenStoredVersionMatchesTheSeed() = runTest {
        val storedAyahRow = testBundledQuoteEntity(quoteId = 1L, surahNumber = 1, ayahNumber = 1)
        val quoteTables = FakeQuoteTables(
            initialQuoteEntities = listOf(storedAyahRow),
            initialSeededBundledKeys = setOf("1:1"),
            initialSeedVersion = 1,
        )
        val bundledAyahSeedDao = FakeBundledAyahSeedDao(quoteTables)

        checkStoredSeed(bundledAyahSeedDao, SampleAyahSeeds.firstAyahSeed)

        assertEquals(0, bundledAyahSeedDao.mergeCallCount)
        assertEquals(listOf(storedAyahRow), quoteTables.storedQuoteEntities)
    }

    /** For example when the table was lost but the version row survived. */
    @Test
    fun reseedsAnEmptyQuotesTableEvenWhenStoredVersionMatches() = runTest {
        val quoteTables = FakeQuoteTables(initialSeedVersion = 1)
        val bundledAyahSeedDao = FakeBundledAyahSeedDao(quoteTables)

        checkStoredSeed(bundledAyahSeedDao, SampleAyahSeeds.firstAyahSeed)

        assertEquals(1, bundledAyahSeedDao.mergeCallCount)
        assertEquals(3, quoteTables.storedQuoteEntities.size)
    }

    @Test
    fun newerSeedRecordsItsVersion() = runTest {
        val quoteTables = FakeQuoteTables(initialSeedVersion = 1)
        val newerAyahSeed = AyahSeed(version = 2, ayahs = SampleAyahSeeds.seedAyahs)

        checkStoredSeed(FakeBundledAyahSeedDao(quoteTables), newerAyahSeed)

        assertEquals(2, quoteTables.seedVersion)
    }

    /** For example after the user installs an older build over a newer one. */
    @Test
    fun mergesWhenStoredVersionIsNewerThanTheSeed() = runTest {
        val storedAyahRow = testBundledQuoteEntity(quoteId = 1L, surahNumber = 1, ayahNumber = 1)
        val quoteTables = FakeQuoteTables(
            initialQuoteEntities = listOf(storedAyahRow),
            initialSeededBundledKeys = setOf("1:1"),
            initialSeedVersion = 3,
        )

        checkStoredSeed(FakeBundledAyahSeedDao(quoteTables), SampleAyahSeeds.firstAyahSeed)

        val storedBundledKeys = quoteTables.storedQuoteEntities.map { it.bundledKey }
        assertEquals(listOf("94:5", "2:153", "13:28"), storedBundledKeys)
        assertEquals(1, quoteTables.seedVersion)
    }

    private suspend fun checkStoredSeed(
        bundledAyahSeedDao: FakeBundledAyahSeedDao,
        ayahSeed: AyahSeed,
    ) {
        val bundledAyahSeeder = BundledAyahSeeder(bundledAyahSeedDao, FakeAyahSeedSource(ayahSeed))
        bundledAyahSeeder.ensureStoredSeedIsCurrent()
    }
}
