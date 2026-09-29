package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.testing.FakeAyahDao
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource
import shibbir.me.alquranquotes.testing.testAyahEntity

class OfflineAyahRepositoryTest {

    private val seedAyahs = listOf(
        testAyahEntity(surah = 94, ayah = 5),
        testAyahEntity(surah = 2, ayah = 153),
        testAyahEntity(surah = 13, ayah = 28),
    )

    @Test
    fun seedsDatabaseOnFirstRequest() = runTest {
        val ayahDao = FakeAyahDao()
        val repository = repository(ayahDao, FakeAyahSeedSource(seedAyahs))

        repository.getDailyAyah(epochDay = 0L)

        assertEquals(3, ayahDao.count())
    }

    @Test
    fun doesNotSeedAgainWhenDatabaseHasAyahs() = runTest {
        val seedSource = FakeAyahSeedSource(seedAyahs)
        val repository = repository(FakeAyahDao(), seedSource)

        repository.getDailyAyah(epochDay = 0L)
        repository.getDailyAyah(epochDay = 1L)

        assertEquals(1, seedSource.loadCount)
    }

    @Test
    fun picksAyahForDayInSurahAndAyahOrder() = runTest {
        val repository = repository(FakeAyahDao(), FakeAyahSeedSource(seedAyahs))

        assertEquals(ayah(2, 153), repository.getDailyAyah(epochDay = 0L))
        assertEquals(ayah(13, 28), repository.getDailyAyah(epochDay = 1L))
        assertEquals(ayah(94, 5), repository.getDailyAyah(epochDay = 2L))
        assertEquals(ayah(2, 153), repository.getDailyAyah(epochDay = 3L))
    }

    @Test
    fun returnsNullWhenNoAyahsAreAvailable() = runTest {
        val repository = repository(FakeAyahDao(), FakeAyahSeedSource(emptyList()))

        assertNull(repository.getDailyAyah(epochDay = 0L))
    }

    private fun TestScope.repository(
        ayahDao: FakeAyahDao,
        seedSource: FakeAyahSeedSource,
    ) = OfflineAyahRepository(
        ayahDao = ayahDao,
        seedSource = seedSource,
        ioDispatcher = StandardTestDispatcher(testScheduler),
    )

    private fun ayah(surah: Int, ayah: Int) = Ayah(
        surahNumber = surah,
        ayahNumber = ayah,
        surahNameEnglish = "Surah $surah",
        surahNameArabic = "surah-ar-$surah",
        arabicText = "arabic-$surah-$ayah",
        translation = "translation-$surah-$ayah",
    )
}
