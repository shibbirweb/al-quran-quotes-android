package shibbir.me.alquranquotes.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import shibbir.me.alquranquotes.model.Quote
import shibbir.me.alquranquotes.model.QuoteDraft
import shibbir.me.alquranquotes.model.QuoteOrigin

/**
 * Any quote: a bundled ayah (edited or not) or one the user added. [type] says which columns are
 * set: the ayah columns for [QuoteType.AYAH], the free text columns for [QuoteType.FREE_TEXT].
 * The others are null. [bundledKey], such as "94:5", is set only on rows a seed stored and never
 * changes, even when the user edits the numbers; the unique index keeps one row per key.
 */
@Entity(
    tableName = "quotes",
    indices = [
        Index(value = ["bundled_key"], unique = true),
    ],
)
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "origin") val origin: QuoteOrigin,
    @ColumnInfo(name = "type") val type: QuoteType,
    @ColumnInfo(name = "bundled_key") val bundledKey: String? = null,
    @ColumnInfo(name = "surah_name") val surahName: String? = null,
    @ColumnInfo(name = "surah_number") val surahNumber: Int? = null,
    @ColumnInfo(name = "ayah_number") val ayahNumber: Int? = null,
    @ColumnInfo(name = "arabic_text") val arabicText: String? = null,
    @ColumnInfo(name = "translation") val translation: String? = null,
    @ColumnInfo(name = "free_text") val freeText: String? = null,
    @ColumnInfo(name = "reference") val reference: String? = null,
)

/** Maps this row to the quote shown in the app. Throws like [toQuoteDraft] on a broken row. */
fun QuoteEntity.toQuote(): Quote {
    val quoteDraft = toQuoteDraft()
    return when (quoteDraft) {
        is QuoteDraft.AyahDraft -> quoteDraft.toAyahQuote(quoteId = id, origin = origin)
        is QuoteDraft.FreeTextDraft -> quoteDraft.toFreeTextQuote(quoteId = id, origin = origin)
    }
}

/** Maps this row back to its draft. Throws when a column its [QuoteType] needs is null. */
fun QuoteEntity.toQuoteDraft(): QuoteDraft {
    return when (type) {
        QuoteType.AYAH -> toAyahDraft()
        QuoteType.FREE_TEXT -> toFreeTextDraft()
    }
}

/** Maps this draft to a new row of [origin]; its id of 0 lets Room generate one on insert. */
fun QuoteDraft.toQuoteEntity(origin: QuoteOrigin): QuoteEntity {
    return when (this) {
        is QuoteDraft.AyahDraft -> toAyahRow(origin)
        is QuoteDraft.FreeTextDraft -> toFreeTextRow(origin)
    }
}

private fun QuoteDraft.AyahDraft.toAyahRow(origin: QuoteOrigin) = QuoteEntity(
    id = 0L,
    origin = origin,
    type = QuoteType.AYAH,
    surahName = surahName,
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    arabicText = arabicText,
    translation = translation,
)

private fun QuoteDraft.FreeTextDraft.toFreeTextRow(origin: QuoteOrigin) = QuoteEntity(
    id = 0L,
    origin = origin,
    type = QuoteType.FREE_TEXT,
    freeText = text,
    reference = reference,
)

private fun QuoteDraft.AyahDraft.toAyahQuote(quoteId: Long, origin: QuoteOrigin) = Quote.AyahQuote(
    quoteId = quoteId,
    origin = origin,
    surahName = surahName,
    surahNumber = surahNumber,
    ayahNumber = ayahNumber,
    arabicText = arabicText,
    translation = translation,
)

private fun QuoteDraft.FreeTextDraft.toFreeTextQuote(
    quoteId: Long,
    origin: QuoteOrigin,
) = Quote.FreeTextQuote(
    quoteId = quoteId,
    origin = origin,
    text = text,
    reference = reference,
)

private fun QuoteEntity.toAyahDraft() = QuoteDraft.AyahDraft(
    surahName = requireColumn(surahName, columnName = "surah_name"),
    surahNumber = requireColumn(surahNumber, columnName = "surah_number"),
    ayahNumber = requireColumn(ayahNumber, columnName = "ayah_number"),
    arabicText = requireColumn(arabicText, columnName = "arabic_text"),
    translation = requireColumn(translation, columnName = "translation"),
)

private fun QuoteEntity.toFreeTextDraft() = QuoteDraft.FreeTextDraft(
    text = requireColumn(freeText, columnName = "free_text"),
    reference = requireColumn(reference, columnName = "reference"),
)

private fun <ColumnValue : Any> QuoteEntity.requireColumn(
    columnValue: ColumnValue?,
    columnName: String,
): ColumnValue {
    return checkNotNull(columnValue) { "$columnName is null in $type quote $id" }
}
