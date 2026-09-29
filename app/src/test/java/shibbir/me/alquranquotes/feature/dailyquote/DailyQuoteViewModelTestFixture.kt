package shibbir.me.alquranquotes.feature.dailyquote

import kotlinx.coroutines.CompletableDeferred
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.testing.FakeAyahRepository
import shibbir.me.alquranquotes.testing.FakeDayChangeSource
import shibbir.me.alquranquotes.testing.FakeEpochDayProvider
import shibbir.me.alquranquotes.testing.ashSharhSampleAyah

/** Fakes, sample ayahs, and builders shared by the [DailyQuoteViewModel] test classes. */
class DailyQuoteViewModelTestFixture {

    val ayahRepository = FakeAyahRepository()

    val epochDayProvider = FakeEpochDayProvider()

    val dayChangeSource = FakeDayChangeSource()

    val ayah = ashSharhSampleAyah()

    val otherAyah = Ayah(
        surahNumber = 13,
        ayahNumber = 28,
        surahNameEnglish = "Ar-Ra'd",
        surahNameArabic = "rad-ar",
        arabicText = "other-arabic-text",
        translation = "other-translation",
    )

    fun createViewModel() = DailyQuoteViewModel(
        ayahRepository = ayahRepository,
        epochDayProvider = epochDayProvider,
        dayChangeSource = dayChangeSource,
    )

    /** Makes the fake repository suspend until the returned gate is completed. */
    fun holdRepositoryResponse(): CompletableDeferred<Unit> {
        val responseGate = CompletableDeferred<Unit>()
        ayahRepository.responseGate = responseGate
        return responseGate
    }
}
