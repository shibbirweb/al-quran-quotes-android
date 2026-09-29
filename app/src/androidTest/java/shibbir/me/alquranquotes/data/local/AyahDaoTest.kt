package shibbir.me.alquranquotes.data.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.testing.testAyahEntity

@RunWith(AndroidJUnit4::class)
class AyahDaoTest {

    private lateinit var database: QuranDatabase
    private lateinit var ayahDao: AyahDao

    @Before
    fun createDatabase() {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val databaseBuilder = Room.inMemoryDatabaseBuilder(targetContext, QuranDatabase::class.java)
        database = databaseBuilder.build()
        ayahDao = database.ayahDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun emptyDatabaseHasNoAyahsAndNoSeedVersion() = runTest {
        assertEquals(0, ayahDao.countAyahs())
        assertNull(ayahDao.getAyahAtPosition(0))
        assertNull(ayahDao.getAyahForDay(epochDay = 0L))
        assertNull(ayahDao.getSeedVersion())
    }

    @Test
    fun replaceAllAyahsStoresAyahsInSurahAndAyahOrderWithVersion() = runTest {
        val ayahEntities = listOf(
            testAyahEntity(surahNumber = 94, ayahNumber = 5),
            testAyahEntity(surahNumber = 2, ayahNumber = 286),
            testAyahEntity(surahNumber = 2, ayahNumber = 153),
        )

        ayahDao.replaceAllAyahs(ayahEntities, seedVersion = 1)

        val firstAyahEntity = ayahDao.getAyahAtPosition(0)
        val secondAyahEntity = ayahDao.getAyahAtPosition(1)
        val thirdAyahEntity = ayahDao.getAyahAtPosition(2)
        assertEquals(3, ayahDao.countAyahs())
        assertEquals(testAyahEntity(surahNumber = 2, ayahNumber = 153), firstAyahEntity)
        assertEquals(testAyahEntity(surahNumber = 2, ayahNumber = 286), secondAyahEntity)
        assertEquals(testAyahEntity(surahNumber = 94, ayahNumber = 5), thirdAyahEntity)
        assertNull(ayahDao.getAyahAtPosition(3))
        assertEquals(1, ayahDao.getSeedVersion())
    }

    /** In surah and ayah order the ayahs are 2:153, 13:28, 94:5, so day 1 is 13:28. */
    @Test
    fun getAyahForDayPicksTheDailyPositionInSurahAndAyahOrder() = runTest {
        val ayahEntities = listOf(
            testAyahEntity(surahNumber = 94, ayahNumber = 5),
            testAyahEntity(surahNumber = 2, ayahNumber = 153),
            testAyahEntity(surahNumber = 13, ayahNumber = 28),
        )
        ayahDao.replaceAllAyahs(ayahEntities, seedVersion = 1)

        val dayOneAyahEntity = ayahDao.getAyahForDay(epochDay = 1L)
        val dayThreeAyahEntity = ayahDao.getAyahForDay(epochDay = 3L)

        assertEquals(testAyahEntity(surahNumber = 13, ayahNumber = 28), dayOneAyahEntity)
        assertEquals(testAyahEntity(surahNumber = 2, ayahNumber = 153), dayThreeAyahEntity)
    }

    @Test
    fun secondReplaceKeepsOnlyNewAyahsAndNewVersion() = runTest {
        val firstAyahEntities = listOf(
            testAyahEntity(surahNumber = 1, ayahNumber = 1),
            testAyahEntity(surahNumber = 2, ayahNumber = 153),
        )
        val secondAyahEntities = listOf(
            testAyahEntity(surahNumber = 94, ayahNumber = 5),
        )

        ayahDao.replaceAllAyahs(firstAyahEntities, seedVersion = 1)
        ayahDao.replaceAllAyahs(secondAyahEntities, seedVersion = 2)

        val onlyAyahEntity = ayahDao.getAyahAtPosition(0)
        assertEquals(1, ayahDao.countAyahs())
        assertEquals(testAyahEntity(surahNumber = 94, ayahNumber = 5), onlyAyahEntity)
        assertEquals(2, ayahDao.getSeedVersion())
    }

    @Test
    fun replaceWithSameKeyReturnsNewTranslation() = runTest {
        val originalAyahEntity = testAyahEntity(
            surahNumber = 94,
            ayahNumber = 5,
            translation = "original translation",
        )
        val updatedAyahEntity = testAyahEntity(
            surahNumber = 94,
            ayahNumber = 5,
            translation = "updated translation",
        )

        ayahDao.replaceAllAyahs(listOf(originalAyahEntity), seedVersion = 1)
        ayahDao.replaceAllAyahs(listOf(updatedAyahEntity), seedVersion = 2)

        val storedAyahEntity = ayahDao.getAyahAtPosition(0)
        assertEquals(1, ayahDao.countAyahs())
        assertEquals("updated translation", storedAyahEntity?.translation)
    }

    @Test
    fun failedReplaceRollsBackToPreviousAyahsAndVersion() = runTest {
        val firstAyahEntities = listOf(
            testAyahEntity(surahNumber = 1, ayahNumber = 1),
            testAyahEntity(surahNumber = 2, ayahNumber = 153),
        )
        val duplicateKeyAyahEntities = listOf(
            testAyahEntity(surahNumber = 94, ayahNumber = 5),
            testAyahEntity(surahNumber = 94, ayahNumber = 5, translation = "duplicate"),
        )
        ayahDao.replaceAllAyahs(firstAyahEntities, seedVersion = 1)

        val failedReplace = runCatching {
            ayahDao.replaceAllAyahs(duplicateKeyAyahEntities, seedVersion = 2)
        }

        val replaceError = failedReplace.exceptionOrNull()
        val firstAyahEntity = ayahDao.getAyahAtPosition(0)
        assertEquals(SQLiteConstraintException::class.java, replaceError?.javaClass)
        assertEquals(2, ayahDao.countAyahs())
        assertEquals(testAyahEntity(surahNumber = 1, ayahNumber = 1), firstAyahEntity)
        assertEquals(1, ayahDao.getSeedVersion())
    }
}
