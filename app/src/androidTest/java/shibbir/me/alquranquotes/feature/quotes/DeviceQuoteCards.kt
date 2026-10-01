package shibbir.me.alquranquotes.feature.quotes

// Placeholder text only, so tests never repeat Quran text.

val deviceBundledAyahCard = QuoteListCard.AyahCard(
    quoteId = 1L,
    kind = QuoteListKind.BUNDLED_AYAH,
    arabicText = "bundled-arabic",
    translation = "bundled-translation",
    surahName = "Bundled Surah",
    surahNumber = 94,
    ayahNumber = 5,
)

val deviceEditedAyahCard = QuoteListCard.AyahCard(
    quoteId = 2L,
    kind = QuoteListKind.EDITED_BUNDLED_AYAH,
    arabicText = "edited-arabic",
    translation = "edited-translation",
    surahName = "Edited Surah",
    surahNumber = 13,
    ayahNumber = 28,
)

val deviceUserAyahCard = QuoteListCard.AyahCard(
    quoteId = 3L,
    kind = QuoteListKind.USER_AYAH,
    arabicText = "user-arabic",
    translation = "user-translation",
    surahName = "User Surah",
    surahNumber = 2,
    ayahNumber = 7,
)

val deviceUserFreeTextCard = QuoteListCard.FreeTextCard(
    quoteId = 4L,
    kind = QuoteListKind.USER_FREE_TEXT,
    text = "user-free-text",
    reference = "user-reference",
)

/** Every card the Quotes list shows in practice, in list order. */
val deviceQuoteCards = listOf(
    deviceBundledAyahCard,
    deviceEditedAyahCard,
    deviceUserAyahCard,
    deviceUserFreeTextCard,
)

/** A loaded list with one card of every kind and no delete confirmation showing. */
fun loadedStateWithEveryKind(quoteIdPendingDelete: Long? = null) = QuotesUiState.Loaded(
    quoteCards = deviceQuoteCards,
    quoteIdPendingDelete = quoteIdPendingDelete,
)

/** A loaded list with only [quoteCard]. */
fun loadedStateWithOnly(quoteCard: QuoteListCard) = QuotesUiState.Loaded(
    quoteCards = listOf(quoteCard),
    quoteIdPendingDelete = null,
)
