package shibbir.me.alquranquotes.feature.dailyquote

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.testing.FakeAyahRepository
import shibbir.me.alquranquotes.testing.MainDispatcherRule
import java.io.IOException

class DailyQuoteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val ayahRepository = FakeAyahRepository()

    private val ayah = Ayah(
        surahNumber = 94,
        ayahNumber = 5,
        surahNameEnglish = "Ash-Sharh",
        surahNameArabic = "sharh-ar",
        arabicText = "arabic-text",
        translation = "For indeed, with hardship [will be] ease.",
    )

    @Test
    fun showsTodaysAyah() {
        ayahRepository.dailyAyah = ayah

        val viewModel = viewModel(today = 20_000L)

        assertEquals(DailyQuoteUiState.Success(ayah), viewModel.uiState.value)
        assertEquals(listOf(20_000L), ayahRepository.requestedEpochDays)
    }

    @Test
    fun showsErrorWhenNoAyahIsAvailable() {
        ayahRepository.dailyAyah = null

        val viewModel = viewModel()

        assertEquals(DailyQuoteUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun showsErrorWhenLoadingFails() {
        ayahRepository.error = IOException("asset missing")

        val viewModel = viewModel()

        assertEquals(DailyQuoteUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun retryAfterErrorShowsAyah() {
        ayahRepository.error = IOException("asset missing")
        val viewModel = viewModel()

        ayahRepository.error = null
        ayahRepository.dailyAyah = ayah
        viewModel.loadDailyQuote()

        assertEquals(DailyQuoteUiState.Success(ayah), viewModel.uiState.value)
    }

    private fun viewModel(today: Long = 0L) = DailyQuoteViewModel(
        ayahRepository = ayahRepository,
        epochDayProvider = { today },
    )
}
