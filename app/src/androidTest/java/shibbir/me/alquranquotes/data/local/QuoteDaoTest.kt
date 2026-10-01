package shibbir.me.alquranquotes.data.local

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.createInMemoryQuranDatabase
import shibbir.me.alquranquotes.testing.testBundledQuoteEntity
import shibbir.me.alquranquotes.testing.testUserAyahEntity
import shibbir.me.alquranquotes.testing.testUserFreeTextEntity

/** Create, read, update, and delete of [QuoteDao] on a real Room database. */
@RunWith(AndroidJUnit4::class)
class QuoteDaoTest {

    private val database = createInMemoryQuranDatabase()

    private val quoteDao = database.quoteDao()

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertGeneratesAscendingIdsAndStoresEveryOriginAndKind() = runTest {
        val bundledAyahRow = testBundledQuoteEntity(surahNumber = 94, ayahNumber = 5)
        val bundledQuoteId = quoteDao.insertQuote(bundledAyahRow)
        val userAyahQuoteId = quoteDao.insertQuote(testUserAyahEntity())
        val userFreeTextQuoteId = quoteDao.insertQuote(testUserFreeTextEntity())

        val storedQuoteEntities = quoteDao.observeQuotes().first()

        val expectedQuoteEntities = listOf(
            bundledAyahRow.copy(id = bundledQuoteId),
            testUserAyahEntity().copy(id = userAyahQuoteId),
            testUserFreeTextEntity().copy(id = userFreeTextQuoteId),
        )
        assertTrue(userAyahQuoteId > bundledQuoteId)
        assertTrue(userFreeTextQuoteId > userAyahQuoteId)
        assertEquals(expectedQuoteEntities, storedQuoteEntities.sortedBy { it.id })
    }

    @Test
    fun getQuoteReturnsTheRowOrNullWhenMissing() = runTest {
        val freeTextQuoteId = quoteDao.insertQuote(testUserFreeTextEntity())

        val storedQuoteEntity = quoteDao.getQuote(freeTextQuoteId)
        val missingQuoteEntity = quoteDao.getQuote(freeTextQuoteId + 1)

        assertEquals(testUserFreeTextEntity().copy(id = freeTextQuoteId), storedQuoteEntity)
        assertNull(missingQuoteEntity)
    }

    @Test
    fun secondRowWithTheSameBundledKeyIsRejected() = runTest {
        val bundledAyahRow = testBundledQuoteEntity(surahNumber = 94, ayahNumber = 5)
        quoteDao.insertQuote(bundledAyahRow)

        val secondInsert = runCatching { quoteDao.insertQuote(bundledAyahRow) }

        val insertFailure = secondInsert.exceptionOrNull()
        assertEquals(SQLiteConstraintException::class.java, insertFailure?.javaClass)
    }

    @Test
    fun updateOfABundledAyahMakesItEditedAndKeepsItsKey() = runTest {
        val bundledAyahRow = testBundledQuoteEntity(surahNumber = 94, ayahNumber = 5)
        val bundledQuoteId = quoteDao.insertQuote(bundledAyahRow)
        val ayahDraft = QuoteDraft.AyahDraft(
            surahName = "edited name",
            surahNumber = 94,
            ayahNumber = 6,
            arabicText = "edited-arabic",
            translation = "edited-translation",
        )

        quoteDao.updateQuote(bundledQuoteId, ayahDraft)

        val editedQuoteEntity = quoteDao.getQuote(bundledQuoteId)
        assertEquals(QuoteOrigin.EDITED_BUNDLED, editedQuoteEntity?.origin)
        assertEquals("94:5", editedQuoteEntity?.bundledKey)
        assertEquals(ayahDraft, editedQuoteEntity?.toQuoteDraft())
    }

    @Test
    fun updateWithADraftOfTheOtherKindRollsBack() = runTest {
        val userAyahQuoteId = quoteDao.insertQuote(testUserAyahEntity())
        val freeTextDraft = QuoteDraft.FreeTextDraft(text = "not an ayah", reference = "")

        val failedUpdate = runCatching { quoteDao.updateQuote(userAyahQuoteId, freeTextDraft) }

        val updateFailure = failedUpdate.exceptionOrNull()
        val storedQuoteEntity = quoteDao.getQuote(userAyahQuoteId)
        assertEquals(IllegalArgumentException::class.java, updateFailure?.javaClass)
        assertEquals(testUserAyahEntity().copy(id = userAyahQuoteId), storedQuoteEntity)
    }

    @Test
    fun deleteRemovesOnlyThatRowAndIdsAreNotReused() = runTest {
        val firstQuoteId = quoteDao.insertQuote(testUserFreeTextEntity(text = "first"))
        val secondQuoteId = quoteDao.insertQuote(testUserFreeTextEntity(text = "second"))

        quoteDao.deleteQuote(secondQuoteId)
        val thirdQuoteId = quoteDao.insertQuote(testUserFreeTextEntity(text = "third"))

        val storedQuoteIds = quoteDao.observeQuotes().first().map { it.id }.sorted()
        assertEquals(listOf(firstQuoteId, thirdQuoteId), storedQuoteIds)
        assertTrue(thirdQuoteId > secondQuoteId)
    }
}
