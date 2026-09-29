package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import shibbir.me.alquranquotes.data.seed.AyahSeed
import shibbir.me.alquranquotes.testing.FakeAyahDao
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource
import shibbir.me.alquranquotes.testing.testAyah

/** Which ayah [OfflineAyahRepository] returns for a day. */
class OfflineAyahRepositoryTest {

    @Test
    fun returnsNullWhenSeedHasNoAyahs() = runTest {
        val emptyAyahSeed = AyahSeed(version = 1, ayahs = emptyList())
        val ayahSeedSource = FakeAyahSeedSource(emptyAyahSeed)
        val repository = createOfflineAyahRepository(FakeAyahDao(), ayahSeedSource)

        val dailyAyah = repository.getDailyAyah(epochDay = 0L)

        assertNull(dailyAyah)
    }

    @Test
    fun picksAyahForDayInSurahAndAyahOrder() = runTest {
        val ayahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.firstAyahSeed)
        val repository = createOfflineAyahRepository(FakeAyahDao(), ayahSeedSource)

        val dayZeroAyah = repository.getDailyAyah(epochDay = 0L)
        val dayOneAyah = repository.getDailyAyah(epochDay = 1L)
        val dayTwoAyah = repository.getDailyAyah(epochDay = 2L)
        val dayThreeAyah = repository.getDailyAyah(epochDay = 3L)

        assertEquals(testAyah(surahNumber = 2, ayahNumber = 153), dayZeroAyah)
        assertEquals(testAyah(surahNumber = 13, ayahNumber = 28), dayOneAyah)
        assertEquals(testAyah(surahNumber = 94, ayahNumber = 5), dayTwoAyah)
        assertEquals(testAyah(surahNumber = 2, ayahNumber = 153), dayThreeAyah)
    }
}
