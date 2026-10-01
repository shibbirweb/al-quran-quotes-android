package shibbir.me.alquranquotes.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Stateless app frame: [content] above a bottom navigation bar that marks [selectedTab] and
 * reports taps through [onTabSelected]. With no [selectedTab] (a full screen page such as the
 * quote editor) the bar is hidden.
 *
 * [content] gets a modifier that pads it clear of the bar and marks that space as consumed, so
 * a screen's own Scaffold does not pad it again. Only the sides are taken from the window
 * insets here (display cutouts and side navigation bars in landscape); each screen's own top
 * app bar handles the status bar.
 */
@Composable
fun QuranQuotesAppScaffold(
    selectedTab: TopLevelTab?,
    onTabSelected: (TopLevelTab) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (contentModifier: Modifier) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { AppBottomBar(selectedTab = selectedTab, onTabSelected = onTabSelected) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
        content(contentModifier)
    }
}

@Composable
private fun AppBottomBar(selectedTab: TopLevelTab?, onTabSelected: (TopLevelTab) -> Unit) {
    if (selectedTab == null) {
        return
    }
    AppNavigationBar(selectedTab = selectedTab, onTabSelected = onTabSelected)
}
