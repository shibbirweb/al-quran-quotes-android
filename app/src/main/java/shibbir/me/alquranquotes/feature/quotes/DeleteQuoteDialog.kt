package shibbir.me.alquranquotes.feature.quotes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import shibbir.me.alquranquotes.R

/** Asks the user to confirm deleting one of their quotes. */
@Composable
internal fun DeleteQuoteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.quotes_delete_dialog_title)) },
        text = { Text(text = stringResource(R.string.quotes_delete_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.quotes_delete_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.quotes_delete_cancel))
            }
        },
    )
}
