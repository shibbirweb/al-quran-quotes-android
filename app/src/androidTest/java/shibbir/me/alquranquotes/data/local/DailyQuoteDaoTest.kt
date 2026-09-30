package shibbir.me.alquranquotes.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.createInMemoryQuranDatabase
import shibbir.me.alquranquotes.testing.testBundledQuoteEntity
import shibbir.me.alquranquotes.testing.testUserAyahEntity
import shibbir.me.alquranquotes.testing.testUserFreeTextEntity

/** The transactional daily lookup of [DailyQuoteDao] on a real Room database. */
@RunWith(AndroidJUnit4::class)
class DailyQuoteDaoTest {

    private val database = createInMemoryQuranDatabase()

    private val dailyQuoteDao = database.dailyQuoteDao()

    private val quoteDao = database.quoteDao()

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun emptyDatabaseHasNoDailyQuote() = runTest {
        assertNull(dailyQuoteDao.getDailyQuote(epochDay = 0L))
    }

    /**
     * In order the quotes are bundled 2:153 (edited), bundled 94:5 (days 0 and 1), then the user
     * quotes by id whatever their numbers (days 2 and 3), and day 4 starts the next cycle.
     */
    @Test
    fun rotatesOverBundledAyahsThenUserQuotesOldestFirst() = runTest {
        val untouchedAyah = testBundledQuoteEntity(surahNumber = 94, ayahNumber = 5)
        val untouchedQuoteId = quoteDao.insertQuote(untouchedAyah)
        val editedAyah = testBundledQuoteEntity(
            surahNumber = 2,
            ayahNumber = 153,
            origin = QuoteOrigin.EDITED_BUNDLED,
        )
        val editedQuoteId = quoteDao.insertQuote(editedAyah)
        val userAyahQuoteId = quoteDao.insertQuote(testUserAyahEntity(surahNumber = 1))
        val userFreeTextQuoteId = quoteDao.insertQuote(testUserFreeTextEntity())

        val dailyQuotes = (0L..4L).map { epochDay -> dailyQuoteDao.getDailyQuote(epochDay) }

        val expectedDailyQuotes = listOf(
            editedAyah.copy(id = editedQuoteId).toQuote(),
            untouchedAyah.copy(id = untouchedQuoteId).toQuote(),
            testUserAyahEntity(surahNumber = 1).copy(id = userAyahQuoteId).toQuote(),
            testUserFreeTextEntity().copy(id = userFreeTextQuoteId).toQuote(),
            editedAyah.copy(id = editedQuoteId).toQuote(),
        )
        assertEquals(expectedDailyQuotes, dailyQuotes)
    }
}
