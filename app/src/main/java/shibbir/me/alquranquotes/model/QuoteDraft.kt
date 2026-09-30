package shibbir.me.alquranquotes.model

/** The fields of a user quote before it is saved: what the quote editor adds or updates. */
sealed interface QuoteDraft {

    data class AyahDraft(
        val surahName: String,
        val surahNumber: Int,
        val ayahNumber: Int,
        val arabicText: String,
        val translation: String,
    ) : QuoteDraft

    /** [reference] may be blank. */
    data class FreeTextDraft(
        val text: String,
        val reference: String,
    ) : QuoteDraft
}
