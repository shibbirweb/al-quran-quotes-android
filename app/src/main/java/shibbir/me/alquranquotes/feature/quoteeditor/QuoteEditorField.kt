package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.annotation.StringRes
import shibbir.me.alquranquotes.R

/** Every text field of the quote editor, with the label it shows and the input it takes. */
enum class QuoteEditorField(
    @param:StringRes val labelResId: Int,
    val fieldInput: QuoteFieldInput,
) {
    SURAH_NAME(R.string.quote_editor_surah_name, QuoteFieldInput.TEXT_LINE),
    SURAH_NUMBER(R.string.quote_editor_surah_number, QuoteFieldInput.NUMBER),
    AYAH_NUMBER(R.string.quote_editor_ayah_number, QuoteFieldInput.NUMBER),
    ARABIC_TEXT(R.string.quote_editor_arabic_text, QuoteFieldInput.ARABIC_TEXT_BLOCK),
    TRANSLATION(R.string.quote_editor_translation, QuoteFieldInput.TEXT_BLOCK),
    FREE_TEXT(R.string.quote_editor_free_text, QuoteFieldInput.TEXT_BLOCK),
    REFERENCE(R.string.quote_editor_reference, QuoteFieldInput.TEXT_LINE),
}
