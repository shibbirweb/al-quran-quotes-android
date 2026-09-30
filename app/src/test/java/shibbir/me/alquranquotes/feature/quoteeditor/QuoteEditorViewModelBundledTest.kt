package shibbir.me.alquranquotes.feature.quoteeditor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.model.QuoteOrigin
import shibbir.me.alquranquotes.testing.MainDispatcherRule

/** Editing a bundled ayah, which works like editing the user's own quote. */
class QuoteEditorViewModelBundledTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixture = QuoteEditorViewModelTestFixture()

    // Placeholder text only, so tests never repeat Quran text.
    private val bundledAyah = Quote.AyahQuote(
        quoteId = 40L,
        origin = QuoteOrigin.BUNDLED,
        surahName = "Bundled Surah",
        surahNumber = 94,
        ayahNumber = 5,
        arabicText = "bundled-arabic",
        translation = "bundled-translation",
    )

    @Test
    fun loadsABundledAyahByItsQuoteId() {
        fixture.quoteRepository.storeQuote(bundledAyah)

        val viewModel = fixture.createViewModel(quoteId = bundledAyah.quoteId)

        val expectedForm = QuoteEditorForm(
            quoteKind = QuoteKind.AYAH,
            surahName = "Bundled Surah",
            surahNumber = "94",
            ayahNumber = "5",
            arabicText = "bundled-arabic",
            translation = "bundled-translation",
        )
        assertEquals(expectedForm, viewModel.uiState.value.form)
        assertEquals(QuoteEditorStatus.READY, viewModel.uiState.value.status)
        assertTrue(viewModel.uiState.value.isEditing)
    }

    @Test
    fun savingAnEditedBundledAyahUpdatesItByItsQuoteId() {
        fixture.quoteRepository.storeQuote(bundledAyah)
        val viewModel = fixture.createViewModel(quoteId = bundledAyah.quoteId)
        viewModel.updateField(QuoteEditorField.TRANSLATION, "edited translation")

        viewModel.save()

        val editedDraft = QuoteDraft.AyahDraft(
            surahName = "Bundled Surah",
            surahNumber = 94,
            ayahNumber = 5,
            arabicText = "bundled-arabic",
            translation = "edited translation",
        )
        val expectedUpdates = listOf(bundledAyah.quoteId to editedDraft)
        assertEquals(expectedUpdates, fixture.quoteRepository.updatedQuoteDrafts)
        assertEquals(emptyList<QuoteDraft>(), fixture.quoteRepository.addedQuoteDrafts)
        assertTrue(viewModel.uiState.value.isSaved)
    }
}
