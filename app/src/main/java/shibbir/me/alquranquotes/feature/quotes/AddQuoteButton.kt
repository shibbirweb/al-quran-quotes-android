package shibbir.me.alquranquotes.feature.quotes

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import shibbir.me.alquranquotes.R

/**
 * The Material 3 extended Add button: an icon and "Add quote" while the list is at its top,
 * shrinking to the icon alone once the list has scrolled, like the compose button in Gmail.
 * The button keeps its label for TalkBack while only the icon shows.
 */
@Composable
internal fun AddQuoteButton(
    listState: LazyListState,
    onAddQuote: () -> Unit,
) {
    val addLabel = stringResource(R.string.quotes_add)
    val isAtTopOfList by remember(listState) {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }
    ExtendedFloatingActionButton(
        text = { Text(text = addLabel) },
        // The button's own label below describes it, so the icon is decorative.
        icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
        onClick = onAddQuote,
        modifier = Modifier.semantics { contentDescription = addLabel },
        expanded = isAtTopOfList,
    )
}
