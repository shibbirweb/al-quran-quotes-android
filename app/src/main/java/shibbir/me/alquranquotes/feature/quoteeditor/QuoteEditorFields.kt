package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection

/** The text fields of the form's kind, each with its error under it when it has one. */
@Composable
internal fun QuoteEditorFields(
    form: QuoteEditorForm,
    fieldErrors: Map<QuoteEditorField, QuoteFieldError>,
    onFieldChange: (QuoteEditorField, String) -> Unit,
) {
    for (quoteEditorField in form.quoteKind.editorFields) {
        QuoteEditorTextField(
            quoteEditorField = quoteEditorField,
            fieldText = form.fieldText(quoteEditorField),
            quoteFieldError = fieldErrors[quoteEditorField],
            onFieldChange = onFieldChange,
        )
    }
}

/** One outlined field, with the keyboard, lines, and text direction its input needs. */
@Composable
private fun QuoteEditorTextField(
    quoteEditorField: QuoteEditorField,
    fieldText: String,
    quoteFieldError: QuoteFieldError?,
    onFieldChange: (QuoteEditorField, String) -> Unit,
) {
    val fieldInput = quoteEditorField.fieldInput
    var supportingText: (@Composable () -> Unit)? = null
    if (quoteFieldError != null) {
        supportingText = { Text(text = stringResource(quoteFieldError.messageResId)) }
    }
    OutlinedTextField(
        value = fieldText,
        onValueChange = { changedText -> onFieldChange(quoteEditorField, changedText) },
        modifier = Modifier.fillMaxWidth(),
        textStyle = fieldTextStyle(isRightToLeft = fieldInput.isRightToLeft),
        label = { Text(text = stringResource(quoteEditorField.labelResId)) },
        supportingText = supportingText,
        isError = quoteFieldError != null,
        keyboardOptions = KeyboardOptions(keyboardType = fieldInput.keyboardType),
        singleLine = fieldInput.isSingleLine,
    )
}

@Composable
private fun fieldTextStyle(isRightToLeft: Boolean): TextStyle {
    val defaultTextStyle = LocalTextStyle.current
    if (isRightToLeft) {
        return defaultTextStyle.copy(textDirection = TextDirection.Rtl)
    }
    return defaultTextStyle
}
