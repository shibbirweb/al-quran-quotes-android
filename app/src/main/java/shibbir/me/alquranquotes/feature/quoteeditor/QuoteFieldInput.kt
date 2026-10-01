package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.compose.ui.text.input.KeyboardType

/** What a quote editor field takes, which decides its keyboard, lines, and text direction. */
enum class QuoteFieldInput(
    val keyboardType: KeyboardType,
    val isSingleLine: Boolean,
    val isRightToLeft: Boolean,
) {
    /** A short name or reference on one line. */
    TEXT_LINE(keyboardType = KeyboardType.Text, isSingleLine = true, isRightToLeft = false),

    /** A whole number, typed on a number keyboard. */
    NUMBER(keyboardType = KeyboardType.Number, isSingleLine = true, isRightToLeft = false),

    /** Quote text in any language that can span many lines. */
    TEXT_BLOCK(keyboardType = KeyboardType.Text, isSingleLine = false, isRightToLeft = false),

    /** Arabic text written right to left, which can span many lines. */
    ARABIC_TEXT_BLOCK(
        keyboardType = KeyboardType.Text,
        isSingleLine = false,
        isRightToLeft = true,
    ),
}
