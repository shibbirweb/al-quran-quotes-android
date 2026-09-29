package shibbir.me.alquranquotes.data.local

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

@RunWith(AndroidJUnit4::class)
class AyahDaoTest {

    private lateinit var database: QuranDatabase
    private lateinit var ayahDao: AyahDao

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, QuranDatabase::class.java).build()
        ayahDao = database.ayahDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun emptyDatabaseHasNoAyahs() = runTest {
        assertEquals(0, ayahDao.count())
        assertNull(ayahDao.getAt(0))
    }

    @Test
    fun insertedAyahsAreCounted() = runTest {
        ayahDao.insertAll(listOf(ayahEntity(2, 153), ayahEntity(94, 5)))

        assertEquals(2, ayahDao.count())
    }

    @Test
    fun getAtReturnsAyahsInSurahAndAyahOrder() = runTest {
        ayahDao.insertAll(listOf(ayahEntity(94, 5), ayahEntity(2, 286), ayahEntity(2, 153)))

        assertEquals(ayahEntity(2, 153), ayahDao.getAt(0))
        assertEquals(ayahEntity(2, 286), ayahDao.getAt(1))
        assertEquals(ayahEntity(94, 5), ayahDao.getAt(2))
        assertNull(ayahDao.getAt(3))
    }

    @Test
    fun insertingSameAyahTwiceKeepsOneRow() = runTest {
        ayahDao.insertAll(listOf(ayahEntity(94, 5)))
        ayahDao.insertAll(listOf(ayahEntity(94, 5)))

        assertEquals(1, ayahDao.count())
    }

    private fun ayahEntity(surah: Int, ayah: Int) = AyahEntity(
        surah = surah,
        ayah = ayah,
        surahNameEnglish = "Surah $surah",
        surahNameArabic = "surah-ar-$surah",
        arabic = "arabic-$surah-$ayah",
        translation = "translation-$surah-$ayah",
    )
}
