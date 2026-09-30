package shibbir.me.alquranquotes.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

/** The Material 3 bottom navigation bar: one item per [TopLevelTab], [selectedTab] marked. */
@Composable
fun AppNavigationBar(
    selectedTab: TopLevelTab,
    onTabSelected: (TopLevelTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        TopLevelTab.entries.forEach { tab ->
            TabItem(
                tab = tab,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@Composable
private fun RowScope.TabItem(tab: TopLevelTab, isSelected: Boolean, onClick: () -> Unit) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        // The label below already names the tab, so the icon is decorative.
        icon = { Icon(imageVector = tab.icon, contentDescription = null) },
        label = { Text(text = stringResource(tab.labelResId)) },
    )
}
