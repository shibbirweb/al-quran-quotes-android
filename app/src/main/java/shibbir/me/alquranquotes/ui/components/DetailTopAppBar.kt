package shibbir.me.alquranquotes.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics

/**
 * The native Material 3 top app bar for a screen opened from a tab (such as the quote editor):
 * a back arrow, a title, and optional [actions], with the standard Material 3 colors that tint
 * when content scrolls under the bar.
 *
 * Pair it with `TopAppBarDefaults.pinnedScrollBehavior()` and add
 * `Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)` to the screen's Scaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopAppBar(
    title: String,
    backContentDescription: String,
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = title, modifier = Modifier.semantics { heading() }) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = backContentDescription,
                )
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
    )
}
