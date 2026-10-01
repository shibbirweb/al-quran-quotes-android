package shibbir.me.alquranquotes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * The whole app: the screens of [AppNavHost] inside [QuranQuotesAppScaffold]. The bottom bar
 * marks the tab on top of the back stack (see [TopLevelTab.selectedBy]) and is hidden on
 * screens that are not a tab. Tabs open with [applyTabNavigationOptions].
 */
@Composable
fun QuranQuotesAppShell(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val selectedTab = TopLevelTab.selectedBy { routeClass ->
        currentDestination?.hasRoute(routeClass) ?: false
    }
    QuranQuotesAppScaffold(
        selectedTab = selectedTab,
        onTabSelected = { tab ->
            val startDestinationId = navController.graph.findStartDestination().id
            navController.navigate(tab.destination) {
                applyTabNavigationOptions(startDestinationId = startDestinationId)
            }
        },
        modifier = modifier,
    ) { contentModifier ->
        AppNavHost(navController = navController, modifier = contentModifier)
    }
}
