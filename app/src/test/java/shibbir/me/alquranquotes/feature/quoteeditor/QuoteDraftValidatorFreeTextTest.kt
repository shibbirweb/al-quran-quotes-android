package shibbir.me.alquranquotes.feature.quoteeditor

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.model.QuoteDraft

/** Free text forms. Ayah forms are in [QuoteDraftValidatorAyahTest]. */
class QuoteDraftValidatorFreeTextTest {

    private val quoteDraftValidator = QuoteDraftValidator()

    @Test
    fun validFreeTextFormBecomesTrimmedFreeTextDraft() {
        val freeTextForm = QuoteEditorForm(
            quoteKind = QuoteKind.FREE_TEXT,
            freeText = " free text ",
            reference = " a reference ",
        )

        val validationResult = quoteDraftValidator.validate(freeTextForm)

        val expectedDraft = QuoteDraft.FreeTextDraft(
            text = "free text",
            reference = "a reference",
        )
        assertEquals(QuoteValidationResult.Valid(expectedDraft), validationResult)
    }

    @Test
    fun blankTextIsRequired() {
        val freeTextForm = QuoteEditorForm(
            quoteKind = QuoteKind.FREE_TEXT,
            freeText = "   ",
            reference = "a reference",
        )

        val validationResult = quoteDraftValidator.validate(freeTextForm)

        val expectedErrors = mapOf(QuoteEditorField.FREE_TEXT to QuoteFieldError.REQUIRED)
        assertEquals(QuoteValidationResult.Invalid(expectedErrors), validationResult)
    }

    @Test
    fun blankReferenceIsAllowedAndSavedEmpty() {
        val freeTextForm = QuoteEditorForm(
            quoteKind = QuoteKind.FREE_TEXT,
            freeText = "free text",
            reference = "  ",
        )

        val validationResult = quoteDraftValidator.validate(freeTextForm)

        val expectedDraft = QuoteDraft.FreeTextDraft(text = "free text", reference = "")
        assertEquals(QuoteValidationResult.Valid(expectedDraft), validationResult)
    }
}
