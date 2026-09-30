package shibbir.me.alquranquotes.feature.quoteeditor

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.ui.components.DetailTopAppBar

/**
 * The editor's native top app bar: back, the Add or Edit title, and a Save action that is
 * enabled when [canSave].
 */
// TopAppBarScrollBehavior is still experimental in Material 3 1.4.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuoteEditorTopAppBar(
    isEditing: Boolean,
    canSave: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    DetailTopAppBar(
        title = editorTitle(isEditing = isEditing),
        backContentDescription = stringResource(R.string.quote_editor_back),
        onBack = onBack,
        scrollBehavior = scrollBehavior,
        actions = {
            TextButton(onClick = onSave, enabled = canSave) {
                Text(text = stringResource(R.string.quote_editor_save))
            }
        },
    )
}

@Composable
private fun editorTitle(isEditing: Boolean): String {
    if (isEditing) {
        return stringResource(R.string.quote_editor_edit_title)
    }
    return stringResource(R.string.quote_editor_add_title)
}
