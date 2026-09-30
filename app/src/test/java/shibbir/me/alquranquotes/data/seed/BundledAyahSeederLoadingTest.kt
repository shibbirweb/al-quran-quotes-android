package shibbir.me.alquranquotes.data.seed

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource
import shibbir.me.alquranquotes.testing.FakeBundledAyahSeedDao
import shibbir.me.alquranquotes.testing.FakeQuoteTables
import shibbir.me.alquranquotes.testing.testAyah
import java.io.IOException

/** How often [BundledAyahSeeder] loads the seed, and how it recovers from failures. */
@OptIn(ExperimentalCoroutinesApi::class)
class BundledAyahSeederLoadingTest {

    private val quoteTables = FakeQuoteTables()

    private val bundledAyahSeedDao = FakeBundledAyahSeedDao(quoteTables)

    private val ayahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.firstAyahSeed)

    private val bundledAyahSeeder = BundledAyahSeeder(bundledAyahSeedDao, ayahSeedSource)

    @Test
    fun loadsTheSeedOnlyOncePerSeederAcrossCalls() = runTest {
        bundledAyahSeeder.ensureStoredSeedIsCurrent()
        bundledAyahSeeder.ensureStoredSeedIsCurrent()
        bundledAyahSeeder.ensureStoredSeedIsCurrent()

        assertEquals(1, ayahSeedSource.loadCount)
        assertEquals(1, bundledAyahSeedDao.mergeCallCount)
    }

    @Test
    fun concurrentCallersLoadTheSeedOnlyOnce() = runTest {
        val loadGate = CompletableDeferred<Unit>()
        ayahSeedSource.loadGate = loadGate
        val callerCount = 3

        val pendingSeedChecks = List(callerCount) {
            async { bundledAyahSeeder.ensureStoredSeedIsCurrent() }
        }
        runCurrent()
        loadGate.complete(Unit)
        pendingSeedChecks.awaitAll()

        assertEquals(1, ayahSeedSource.loadCount)
        assertEquals(1, bundledAyahSeedDao.mergeCallCount)
        assertEquals(3, quoteTables.storedQuoteEntities.size)
    }

    @Test
    fun retriesAfterAFailedLoad() = runTest {
        ayahSeedSource.nextLoadFailure = IOException("asset unavailable")

        val firstAttempt = runCatching { bundledAyahSeeder.ensureStoredSeedIsCurrent() }
        bundledAyahSeeder.ensureStoredSeedIsCurrent()

        val firstAttemptFailure = firstAttempt.exceptionOrNull()
        assertEquals(IOException::class.java, firstAttemptFailure?.javaClass)
        assertEquals(2, ayahSeedSource.loadCount)
        assertEquals(3, quoteTables.storedQuoteEntities.size)
    }

    /** Both seed ayahs share the key 94:5, so storing them fails and the merge rolls back. */
    @Test
    fun retriesAFailedMergeOnTheNextCall() = runTest {
        val duplicateKeyAyahSeed = AyahSeed(
            version = 1,
            ayahs = listOf(
                testAyah(surahNumber = 94, ayahNumber = 5),
                testAyah(surahNumber = 94, ayahNumber = 5, translation = "duplicate"),
            ),
        )
        val failingSeeder = BundledAyahSeeder(
            bundledAyahSeedDao,
            FakeAyahSeedSource(duplicateKeyAyahSeed),
        )

        val firstAttempt = runCatching { failingSeeder.ensureStoredSeedIsCurrent() }
        val secondAttempt = runCatching { failingSeeder.ensureStoredSeedIsCurrent() }

        assertEquals(IllegalStateException::class.java, firstAttempt.exceptionOrNull()?.javaClass)
        assertEquals(IllegalStateException::class.java, secondAttempt.exceptionOrNull()?.javaClass)
        assertEquals(2, bundledAyahSeedDao.mergeCallCount)
        assertEquals(emptyList<Any>(), quoteTables.storedQuoteEntities)
        assertEquals(null, quoteTables.seedVersion)
    }
}
