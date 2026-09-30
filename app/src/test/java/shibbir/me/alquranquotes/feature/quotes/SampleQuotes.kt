package shibbir.me.alquranquotes.feature.quotes

import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.model.QuoteOrigin

// Placeholder text only, so tests never repeat Quran text.

fun sampleAyahQuote(
    quoteId: Long = 1L,
    origin: QuoteOrigin = QuoteOrigin.BUNDLED,
) = Quote.AyahQuote(
    quoteId = quoteId,
    origin = origin,
    surahName = "Surah 94",
    surahNumber = 94,
    ayahNumber = 5,
    arabicText = "arabic-94-5",
    translation = "translation-94-5",
)

fun sampleFreeTextQuote(
    quoteId: Long = 2L,
    origin: QuoteOrigin = QuoteOrigin.USER,
    reference: String = "a reference",
) = Quote.FreeTextQuote(
    quoteId = quoteId,
    origin = origin,
    text = "free text",
    reference = reference,
)

fun sampleFreeTextDraft(reference: String = "a reference") = QuoteDraft.FreeTextDraft(
    text = "free text",
    reference = reference,
)
