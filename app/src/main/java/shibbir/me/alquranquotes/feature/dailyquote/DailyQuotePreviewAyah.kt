package shibbir.me.alquranquotes.feature.dailyquote

import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteOrigin

/**
 * Sample ayah for Compose previews only. The text is copied from the bundled ayahs.json, and
 * DailyQuotePreviewAyahTest checks that it still matches.
 */
internal val dailyQuotePreviewAyah = Ayah(
    surahNumber = 94,
    ayahNumber = 5,
    surahNameEnglish = "Ash-Sharh",
    surahNameArabic = "سُورَةُ الشَّرۡحِ",
    arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا",
    translation = "For indeed, with hardship [will be] ease.",
)

/** [dailyQuotePreviewAyah] as a bundled quote, copying its values so no text is retyped. */
internal fun dailyQuotePreviewQuote() = Quote.AyahQuote(
    quoteId = 1L,
    origin = QuoteOrigin.BUNDLED,
    surahName = dailyQuotePreviewAyah.surahNameEnglish,
    surahNumber = dailyQuotePreviewAyah.surahNumber,
    ayahNumber = dailyQuotePreviewAyah.ayahNumber,
    arabicText = dailyQuotePreviewAyah.arabicText,
    translation = dailyQuotePreviewAyah.translation,
)
