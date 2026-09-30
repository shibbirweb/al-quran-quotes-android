package shibbir.me.alquranquotes.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics

/**
 * The native Material 3 top app bar for a bottom navigation tab: a large title that collapses
 * into a small bar as the content scrolls, using the standard Material 3 colors (the surface
 * color, tinted when content scrolls under it), like the Android Settings app.
 *
 * Pair it with `TopAppBarDefaults.exitUntilCollapsedScrollBehavior()` and add
 * `Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)` to the screen's Scaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopLevelTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    LargeTopAppBar(
        title = { Text(text = title, modifier = Modifier.semantics { heading() }) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
    )
}
