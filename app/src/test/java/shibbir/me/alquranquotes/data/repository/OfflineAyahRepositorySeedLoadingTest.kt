package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.testing.FakeAyahDao
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource
import shibbir.me.alquranquotes.testing.testAyah
import java.io.IOException

/** How often [OfflineAyahRepository] loads the seed, and how it recovers from failures. */
@OptIn(ExperimentalCoroutinesApi::class)
class OfflineAyahRepositorySeedLoadingTest {

    @Test
    fun loadsSeedOnlyOncePerRepositoryAcrossCalls() = runTest {
        val ayahDao = FakeAyahDao()
        val ayahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.firstAyahSeed)
        val repository = createOfflineAyahRepository(ayahDao, ayahSeedSource)

        repository.getDailyAyah(epochDay = 0L)
        repository.getDailyAyah(epochDay = 1L)
        repository.getDailyAyah(epochDay = 2L)

        assertEquals(1, ayahSeedSource.loadCount)
        assertEquals(1, ayahDao.replaceCallCount)
    }

    @Test
    fun concurrentCallersLoadSeedOnlyOnce() = runTest {
        val ayahDao = FakeAyahDao()
        val ayahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.firstAyahSeed)
        val loadGate = CompletableDeferred<Unit>()
        ayahSeedSource.loadGate = loadGate
        val repository = createOfflineAyahRepository(ayahDao, ayahSeedSource)
        val callerCount = 3

        val pendingDailyAyahs = List(callerCount) {
            async { repository.getDailyAyah(epochDay = 0L) }
        }
        runCurrent()
        loadGate.complete(Unit)
        val dailyAyahs = pendingDailyAyahs.awaitAll()

        val expectedDailyAyah = testAyah(surahNumber = 2, ayahNumber = 153)
        assertEquals(1, ayahSeedSource.loadCount)
        assertEquals(1, ayahDao.replaceCallCount)
        assertEquals(List(callerCount) { expectedDailyAyah }, dailyAyahs)
    }

    @Test
    fun retriesSeedingAfterFailedLoad() = runTest {
        val ayahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.firstAyahSeed)
        ayahSeedSource.nextLoadFailure = IOException("asset unavailable")
        val repository = createOfflineAyahRepository(FakeAyahDao(), ayahSeedSource)

        val firstAttempt = runCatching { repository.getDailyAyah(epochDay = 0L) }
        val dailyAyah = repository.getDailyAyah(epochDay = 0L)

        val firstAttemptFailure = firstAttempt.exceptionOrNull()
        assertEquals(IOException::class.java, firstAttemptFailure?.javaClass)
        assertEquals(2, ayahSeedSource.loadCount)
        assertEquals(testAyah(surahNumber = 2, ayahNumber = 153), dailyAyah)
    }

    @Test
    fun failedReplaceKeepsOldAyahsAndIsRetriedOnNextCall() = runTest {
        val ayahDao = FakeAyahDao(
            initialAyahEntities = SampleAyahSeeds.oldAyahEntities,
            initialSeedVersion = 1,
        )
        val ayahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.duplicateKeyAyahSeed)
        val repository = createOfflineAyahRepository(ayahDao, ayahSeedSource)

        val firstAttempt = runCatching { repository.getDailyAyah(epochDay = 0L) }
        val secondAttempt = runCatching { repository.getDailyAyah(epochDay = 0L) }

        val firstAttemptFailure = firstAttempt.exceptionOrNull()
        val secondAttemptFailure = secondAttempt.exceptionOrNull()
        assertEquals(IllegalStateException::class.java, firstAttemptFailure?.javaClass)
        assertEquals(IllegalStateException::class.java, secondAttemptFailure?.javaClass)
        assertEquals(2, ayahDao.replaceCallCount)
        assertEquals(SampleAyahSeeds.oldAyahEntities, ayahDao.storedAyahEntities)
        assertEquals(1, ayahDao.storedSeedVersion)
    }
}
