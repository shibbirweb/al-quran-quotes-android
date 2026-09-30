package shibbir.me.alquranquotes.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.testing.createInMemoryQuranDatabase
import shibbir.me.alquranquotes.testing.testUserFreeTextEntity

/** [QuoteDao.observeQuotes] on a real Room database. */
@RunWith(AndroidJUnit4::class)
class QuoteDaoFlowTest {

    private val database = createInMemoryQuranDatabase()

    private val quoteDao = database.quoteDao()

    @After
    fun closeDatabase() {
        database.close()
    }

    /** Waits for each emission before the next change, so Room cannot merge two changes. */
    @Test
    fun observeQuotesEmitsAgainAfterEveryChange() = runTest {
        val observedFreeTexts = Channel<List<String?>>(Channel.UNLIMITED)
        backgroundScope.launch(Dispatchers.Default) {
            quoteDao.observeQuotes().collect { quoteEntities ->
                val freeTexts = quoteEntities.map { it.freeText }
                observedFreeTexts.send(freeTexts)
            }
        }

        val freeTextsBeforeInsert = observedFreeTexts.receive()
        val quoteId = quoteDao.insertQuote(testUserFreeTextEntity(text = "added"))
        val freeTextsAfterInsert = observedFreeTexts.receive()
        val updatedDraft = QuoteDraft.FreeTextDraft(text = "updated", reference = "")
        quoteDao.updateQuote(quoteId, updatedDraft)
        val freeTextsAfterUpdate = observedFreeTexts.receive()
        quoteDao.deleteQuote(quoteId)
        val freeTextsAfterDelete = observedFreeTexts.receive()

        assertEquals(emptyList<String?>(), freeTextsBeforeInsert)
        assertEquals(listOf("added"), freeTextsAfterInsert)
        assertEquals(listOf("updated"), freeTextsAfterUpdate)
        assertEquals(emptyList<String?>(), freeTextsAfterDelete)
    }
}
