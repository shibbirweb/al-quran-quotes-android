package shibbir.me.alquranquotes.feature.quoteeditor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteDraft

/** Ayah forms. Free text forms are in [QuoteDraftValidatorFreeTextTest]. */
class QuoteDraftValidatorAyahTest {

    private val quoteDraftValidator = QuoteDraftValidator()

    private val validAyahForm = QuoteEditorForm(
        quoteKind = QuoteKind.AYAH,
        surahName = "User Surah",
        surahNumber = "2",
        ayahNumber = "7",
        arabicText = "user-arabic",
        translation = "user-translation",
    )

    @Test
    fun validAyahFormBecomesTrimmedAyahDraft() {
        val ayahForm = QuoteEditorForm(
            quoteKind = QuoteKind.AYAH,
            surahName = " User Surah ",
            surahNumber = " 2 ",
            ayahNumber = "7 ",
            arabicText = " user-arabic",
            translation = "user-translation ",
        )

        val validationResult = quoteDraftValidator.validate(ayahForm)

        val expectedDraft = QuoteDraft.AyahDraft(
            surahName = "User Surah",
            surahNumber = 2,
            ayahNumber = 7,
            arabicText = "user-arabic",
            translation = "user-translation",
        )
        assertEquals(QuoteValidationResult.Valid(expectedDraft), validationResult)
    }

    @Test
    fun blankTextFieldsAreRequired() {
        val ayahForm = QuoteEditorForm(
            quoteKind = QuoteKind.AYAH,
            surahName = " ",
            surahNumber = "2",
            ayahNumber = "7",
            arabicText = "",
            translation = "\t",
        )

        val validationResult = quoteDraftValidator.validate(ayahForm)

        val expectedErrors = mapOf(
            QuoteEditorField.SURAH_NAME to QuoteFieldError.REQUIRED,
            QuoteEditorField.ARABIC_TEXT to QuoteFieldError.REQUIRED,
            QuoteEditorField.TRANSLATION to QuoteFieldError.REQUIRED,
        )
        assertEquals(QuoteValidationResult.Invalid(expectedErrors), validationResult)
    }

    @Test
    fun blankNumbersAreRequired() {
        val ayahForm = validAyahForm.copy(surahNumber = " ", ayahNumber = "")

        val validationResult = quoteDraftValidator.validate(ayahForm)

        val expectedErrors = mapOf(
            QuoteEditorField.SURAH_NUMBER to QuoteFieldError.REQUIRED,
            QuoteEditorField.AYAH_NUMBER to QuoteFieldError.REQUIRED,
        )
        assertEquals(QuoteValidationResult.Invalid(expectedErrors), validationResult)
    }

    @Test
    fun numbersThatDoNotParseAreNotANumber() {
        val ayahForm = validAyahForm.copy(surahNumber = "2.5", ayahNumber = "99999999999")

        val validationResult = quoteDraftValidator.validate(ayahForm)

        val expectedErrors = mapOf(
            QuoteEditorField.SURAH_NUMBER to QuoteFieldError.NOT_A_NUMBER,
            QuoteEditorField.AYAH_NUMBER to QuoteFieldError.NOT_A_NUMBER,
        )
        assertEquals(QuoteValidationResult.Invalid(expectedErrors), validationResult)
    }

    @Test
    fun surahNumberBelowOneIsOutOfRange() {
        val ayahForm = validAyahForm.copy(surahNumber = "0")

        val validationResult = quoteDraftValidator.validate(ayahForm)

        val expectedErrors = mapOf(
            QuoteEditorField.SURAH_NUMBER to QuoteFieldError.SURAH_NUMBER_OUT_OF_RANGE,
        )
        assertEquals(QuoteValidationResult.Invalid(expectedErrors), validationResult)
    }

    @Test
    fun surahNumberAbove114IsOutOfRange() {
        val ayahForm = validAyahForm.copy(surahNumber = "115")

        val validationResult = quoteDraftValidator.validate(ayahForm)

        val expectedErrors = mapOf(
            QuoteEditorField.SURAH_NUMBER to QuoteFieldError.SURAH_NUMBER_OUT_OF_RANGE,
        )
        assertEquals(QuoteValidationResult.Invalid(expectedErrors), validationResult)
    }

    @Test
    fun ayahNumberBelowOneIsTooSmall() {
        val ayahForm = validAyahForm.copy(ayahNumber = "0")

        val validationResult = quoteDraftValidator.validate(ayahForm)

        val expectedErrors = mapOf(
            QuoteEditorField.AYAH_NUMBER to QuoteFieldError.AYAH_NUMBER_TOO_SMALL,
        )
        assertEquals(QuoteValidationResult.Invalid(expectedErrors), validationResult)
    }

    @Test
    fun firstAndLastSurahAndFirstAyahAreValid() {
        val firstSurahForm = validAyahForm.copy(surahNumber = "1", ayahNumber = "1")
        val lastSurahForm = validAyahForm.copy(surahNumber = "114", ayahNumber = "1")

        val firstSurahResult = quoteDraftValidator.validate(firstSurahForm)
        val lastSurahResult = quoteDraftValidator.validate(lastSurahForm)

        assertTrue(firstSurahResult is QuoteValidationResult.Valid)
        assertTrue(lastSurahResult is QuoteValidationResult.Valid)
    }
}
