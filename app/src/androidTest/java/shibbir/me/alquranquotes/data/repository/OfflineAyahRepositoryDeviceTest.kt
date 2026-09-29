package shibbir.me.alquranquotes.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.data.local.QuranDatabase
import shibbir.me.alquranquotes.data.seed.AssetAyahSeedSource
import shibbir.me.alquranquotes.model.Ayah

/**
 * Checks the real bundled asset flows through Room into the daily ayah. Expectations come from
 * the asset itself, so the test never repeats Quran text.
 */
@RunWith(AndroidJUnit4::class)
class OfflineAyahRepositoryDeviceTest {

    private lateinit var targetContext: Context
    private lateinit var database: QuranDatabase

    @Before
    fun createDatabase() {
        targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val databaseBuilder = Room.inMemoryDatabaseBuilder(targetContext, QuranDatabase::class.java)
        database = databaseBuilder.build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun everyDayOfTheCycleGivesTheBundledAyahAtThatPosition() = runTest {
        val ayahSeedSource = AssetAyahSeedSource(
            context = targetContext,
            ioDispatcher = Dispatchers.IO,
        )
        val repository = OfflineAyahRepository(
            ayahDao = database.ayahDao(),
            ayahSeedSource = ayahSeedSource,
        )
        val bundledAyahSeed = ayahSeedSource.load()
        val orderedBundledAyahs = orderedBySurahThenAyah(bundledAyahSeed.ayahs)

        val dailyAyahs = orderedBundledAyahs.indices.map { position ->
            repository.getDailyAyah(epochDay = position.toLong())
        }

        val storedAyahCount = database.ayahDao().countAyahs()
        assertTrue(orderedBundledAyahs.isNotEmpty())
        assertEquals(orderedBundledAyahs.size, storedAyahCount)
        assertEquals(orderedBundledAyahs, dailyAyahs)
    }

    private fun orderedBySurahThenAyah(ayahs: List<Ayah>): List<Ayah> {
        val surahThenAyahOrder = compareBy<Ayah>({ it.surahNumber }, { it.ayahNumber })
        return ayahs.sortedWith(surahThenAyahOrder)
    }
}
