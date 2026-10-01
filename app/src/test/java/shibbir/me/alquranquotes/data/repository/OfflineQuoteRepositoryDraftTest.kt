package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteDraft

/** What [OfflineQuoteRepository.getQuoteDraft] returns for every origin. */
class OfflineQuoteRepositoryDraftTest {

    private val fixture = OfflineQuoteRepositoryTestFixture(storedSampleTables())

    private val repository = fixture.createRepository()

    @Test
    fun untouchedBundledAyahGivesItsAyahDraft() = runTest {
        val quoteDraft = repository.getQuoteDraft(1L)

        val expectedQuoteDraft = QuoteDraft.AyahDraft(
            surahName = "Surah 94",
            surahNumber = 94,
            ayahNumber = 5,
            arabicText = "arabic-94-5",
            translation = "translation-94-5",
        )
        assertEquals(expectedQuoteDraft, quoteDraft)
    }

    @Test
    fun editedBundledAyahGivesItsEditedAyahDraft() = runTest {
        val quoteDraft = repository.getQuoteDraft(2L)

        val expectedQuoteDraft = QuoteDraft.AyahDraft(
            surahName = "Surah 13",
            surahNumber = 13,
            ayahNumber = 28,
            arabicText = "arabic-13-28",
            translation = "edited translation",
        )
        assertEquals(expectedQuoteDraft, quoteDraft)
    }

    @Test
    fun userFreeTextQuoteGivesItsFreeTextDraft() = runTest {
        val quoteDraft = repository.getQuoteDraft(3L)

        val expectedQuoteDraft = QuoteDraft.FreeTextDraft(
            text = "free text",
            reference = "a reference",
        )
        assertEquals(expectedQuoteDraft, quoteDraft)
    }

    @Test
    fun missingQuoteGivesNull() = runTest {
        val quoteDraft = repository.getQuoteDraft(99L)

        assertNull(quoteDraft)
    }
}
