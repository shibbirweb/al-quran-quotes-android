package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import shibbir.me.alquranquotes.data.seed.AyahSeed
import shibbir.me.alquranquotes.testing.FakeAyahDao
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource
import shibbir.me.alquranquotes.testing.testAyah
import shibbir.me.alquranquotes.testing.testAyahEntity

/** How [OfflineAyahRepository] decides whether to replace the stored ayahs with the seed. */
class OfflineAyahRepositorySeedVersionTest {

    @Test
    fun seedsEmptyDatabaseOnFirstRequest() = runTest {
        val ayahDao = FakeAyahDao()
        val ayahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.firstAyahSeed)
        val repository = createOfflineAyahRepository(ayahDao, ayahSeedSource)

        repository.getDailyAyah(epochDay = 0L)

        assertEquals(3, ayahDao.countAyahs())
        assertEquals(1, ayahDao.storedSeedVersion)
        assertEquals(1, ayahDao.replaceCallCount)
    }

    @Test
    fun doesNotReplaceAyahsWhenStoredVersionMatchesSeed() = runTest {
        val ayahDao = FakeAyahDao(
            initialAyahEntities = SampleAyahSeeds.oldAyahEntities,
            initialSeedVersion = 1,
        )
        val ayahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.firstAyahSeed)
        val repository = createOfflineAyahRepository(ayahDao, ayahSeedSource)

        val dailyAyah = repository.getDailyAyah(epochDay = 0L)

        assertEquals(0, ayahDao.replaceCallCount)
        assertEquals(SampleAyahSeeds.oldAyahEntities, ayahDao.storedAyahEntities)
        assertEquals(testAyah(surahNumber = 1, ayahNumber = 1), dailyAyah)
    }

    @Test
    fun reseedsEmptyTableEvenWhenStoredVersionMatchesSeed() = runTest {
        val ayahDao = FakeAyahDao(
            initialAyahEntities = emptyList(),
            initialSeedVersion = 1,
        )
        val ayahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.firstAyahSeed)
        val repository = createOfflineAyahRepository(ayahDao, ayahSeedSource)

        val dailyAyah = repository.getDailyAyah(epochDay = 0L)

        assertEquals(1, ayahDao.replaceCallCount)
        assertEquals(3, ayahDao.countAyahs())
        assertEquals(testAyah(surahNumber = 2, ayahNumber = 153), dailyAyah)
    }

    @Test
    fun replacesAllAyahsWhenSeedVersionIsNewer() = runTest {
        val staleAyahEntities = listOf(
            testAyahEntity(surahNumber = 1, ayahNumber = 1),
            testAyahEntity(surahNumber = 2, ayahNumber = 153, translation = "old translation"),
        )
        val ayahDao = FakeAyahDao(
            initialAyahEntities = staleAyahEntities,
            initialSeedVersion = 1,
        )
        val newerAyahSeed = AyahSeed(version = 2, ayahs = SampleAyahSeeds.seedAyahs)
        val ayahSeedSource = FakeAyahSeedSource(newerAyahSeed)
        val repository = createOfflineAyahRepository(ayahDao, ayahSeedSource)

        repository.getDailyAyah(epochDay = 0L)

        val storedTranslations = ayahDao.storedAyahEntities.map { it.translation }
        assertEquals(SampleAyahSeeds.seedAyahKeys, ayahDao.storedAyahKeys())
        assertFalse("old translation" in storedTranslations)
        assertEquals(2, ayahDao.storedSeedVersion)
        assertEquals(1, ayahDao.replaceCallCount)
    }

    /** For example after the user installs an older build over a newer one. */
    @Test
    fun replacesAllAyahsWhenStoredVersionIsNewerThanSeed() = runTest {
        val ayahDao = FakeAyahDao(
            initialAyahEntities = SampleAyahSeeds.oldAyahEntities,
            initialSeedVersion = 3,
        )
        val ayahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.firstAyahSeed)
        val repository = createOfflineAyahRepository(ayahDao, ayahSeedSource)

        repository.getDailyAyah(epochDay = 0L)

        assertEquals(SampleAyahSeeds.seedAyahKeys, ayahDao.storedAyahKeys())
        assertEquals(1, ayahDao.storedSeedVersion)
        assertEquals(1, ayahDao.replaceCallCount)
    }
}
