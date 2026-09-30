package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.annotation.StringRes
import shibbir.me.alquranquotes.R

/**
 * The two kinds of user quote the editor can write, with the label of each kind's button and
 * the fields the kind shows, in order.
 */
enum class QuoteKind(
    @param:StringRes val labelResId: Int,
    val editorFields: List<QuoteEditorField>,
) {
    AYAH(
        labelResId = R.string.quote_editor_kind_ayah,
        editorFields = listOf(
            QuoteEditorField.SURAH_NAME,
            QuoteEditorField.SURAH_NUMBER,
            QuoteEditorField.AYAH_NUMBER,
            QuoteEditorField.ARABIC_TEXT,
            QuoteEditorField.TRANSLATION,
        ),
    ),
    FREE_TEXT(
        labelResId = R.string.quote_editor_kind_free_text,
        editorFields = listOf(
            QuoteEditorField.FREE_TEXT,
            QuoteEditorField.REFERENCE,
        ),
    ),
}
