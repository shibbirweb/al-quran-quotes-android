package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.local.QuoteEntity
import shibbir.me.alquranquotes.data.local.QuoteType
import shibbir.me.alquranquotes.model.Ayah
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.model.QuoteOrigin

fun testAyah(
    surahNumber: Int,
    ayahNumber: Int,
    translation: String = "translation-$surahNumber-$ayahNumber",
) = Ayah(
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    surahNameEnglish = "Surah $surahNumber",
    surahNameArabic = "surah-ar-$surahNumber",
    arabicText = "arabic-$surahNumber-$ayahNumber",
    translation = translation,
)

/**
 * Ayah 94:5 of Ash-Sharh with placeholder text, for tests that need one complete, fixed ayah.
 * The Arabic and translation are deliberately fake, so tests never repeat Quran text.
 */
fun ashSharhSampleAyah() = Ayah(
    surahNumber = 94,
    ayahNumber = 5,
    surahNameEnglish = "Ash-Sharh",
    surahNameArabic = "surah-name-arabic",
    arabicText = "arabic-text",
    translation = "translation-text",
)

/** A user ayah draft with placeholder text, so tests never repeat Quran text. */
fun testAyahDraft(
    surahNumber: Int = 94,
    ayahNumber: Int = 5,
) = QuoteDraft.AyahDraft(
    surahName = "Surah $surahNumber",
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    arabicText = "user-arabic-$surahNumber-$ayahNumber",
    translation = "user-translation-$surahNumber-$ayahNumber",
)

fun testFreeTextDraft(
    text: String = "free text",
    reference: String = "a reference",
) = QuoteDraft.FreeTextDraft(
    text = text,
    reference = reference,
)

/** A stored bundled ayah row as the seeder stores [testAyah], keyed "surah:ayah". */
fun testBundledQuoteEntity(
    quoteId: Long,
    surahNumber: Int,
    ayahNumber: Int,
    origin: QuoteOrigin = QuoteOrigin.BUNDLED,
    translation: String = "translation-$surahNumber-$ayahNumber",
) = QuoteEntity(
    id = quoteId,
    origin = origin,
    type = QuoteType.AYAH,
    bundledKey = "$surahNumber:$ayahNumber",
    surahName = "Surah $surahNumber",
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    arabicText = "arabic-$surahNumber-$ayahNumber",
    translation = translation,
)

/** A stored ayah row the user added, with placeholder text. */
fun testUserAyahEntity(
    quoteId: Long,
    surahNumber: Int,
    ayahNumber: Int,
) = QuoteEntity(
    id = quoteId,
    origin = QuoteOrigin.USER,
    type = QuoteType.AYAH,
    surahName = "Surah $surahNumber",
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    arabicText = "user-arabic-$surahNumber-$ayahNumber",
    translation = "user-translation-$surahNumber-$ayahNumber",
)

/** A stored free text row the user added. */
fun testUserFreeTextEntity(
    quoteId: Long,
    text: String = "free text",
) = QuoteEntity(
    id = quoteId,
    origin = QuoteOrigin.USER,
    type = QuoteType.FREE_TEXT,
    freeText = text,
    reference = "a reference",
)
