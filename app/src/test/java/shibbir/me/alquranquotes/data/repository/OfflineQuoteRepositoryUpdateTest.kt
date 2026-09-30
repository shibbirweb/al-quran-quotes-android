package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.data.local.QuoteEntity
import shibbir.me.alquranquotes.data.local.QuoteType
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.testAyahDraft
import shibbir.me.alquranquotes.testing.testFreeTextDraft

/** How [OfflineQuoteRepository.updateQuote] saves new fields for quotes of every origin. */
class OfflineQuoteRepositoryUpdateTest {

    private val fixture = OfflineQuoteRepositoryTestFixture(storedSampleTables())

    private val quoteTables = fixture.quoteTables

    private val repository = fixture.createRepository()

    @Test
    fun updatingAnUntouchedBundledAyahMakesItEditedBundledKeepingItsKey() = runTest {
        repository.updateQuote(1L, testAyahDraft(surahNumber = 94, ayahNumber = 6))

        val expectedEditedRow = QuoteEntity(
            id = 1L,
            origin = QuoteOrigin.EDITED_BUNDLED,
            type = QuoteType.AYAH,
            bundledKey = "94:5",
            surahName = "Surah 94",
            surahNumber = 94,
            ayahNumber = 6,
            arabicText = "user-arabic-94-6",
            translation = "user-translation-94-6",
        )
        assertEquals(expectedEditedRow, storedQuote(1L))
    }

    @Test
    fun updatingAUserQuoteKeepsItAUserQuote() = runTest {
        repository.updateQuote(3L, testFreeTextDraft(text = "updated"))

        val expectedEditedRow = QuoteEntity(
            id = 3L,
            origin = QuoteOrigin.USER,
            type = QuoteType.FREE_TEXT,
            freeText = "updated",
            reference = "a reference",
        )
        assertEquals(expectedEditedRow, storedQuote(3L))
    }

    @Test
    fun updatingAnEditedBundledAyahKeepsItEditedBundled() = runTest {
        repository.updateQuote(2L, testAyahDraft(surahNumber = 13, ayahNumber = 28))

        val editedRow = storedQuote(2L)
        assertEquals(QuoteOrigin.EDITED_BUNDLED, editedRow.origin)
        assertEquals("user-translation-13-28", editedRow.translation)
        assertEquals("13:28", editedRow.bundledKey)
    }

    /** Editing keeps a quote's kind, so a draft of the other kind is rejected. */
    @Test
    fun updatingWithADraftOfTheOtherKindFailsAndKeepsTheQuote() = runTest {
        val storedRowsBeforeUpdate = quoteTables.storedQuoteEntities

        val freeTextDraftForAnAyah = testFreeTextDraft(text = "not an ayah")
        val updateAttempt = runCatching { repository.updateQuote(1L, freeTextDraftForAnAyah) }

        val updateFailure = updateAttempt.exceptionOrNull()
        assertEquals(IllegalArgumentException::class.java, updateFailure?.javaClass)
        assertEquals(storedRowsBeforeUpdate, quoteTables.storedQuoteEntities)
    }

    @Test
    fun updatingAMissingQuoteChangesNothing() = runTest {
        val storedRowsBeforeUpdate = quoteTables.storedQuoteEntities

        repository.updateQuote(99L, testFreeTextDraft(text = "nowhere"))

        assertEquals(storedRowsBeforeUpdate, quoteTables.storedQuoteEntities)
    }

    private fun storedQuote(quoteId: Long): QuoteEntity {
        return quoteTables.storedQuoteEntities.single { it.id == quoteId }
    }
}
