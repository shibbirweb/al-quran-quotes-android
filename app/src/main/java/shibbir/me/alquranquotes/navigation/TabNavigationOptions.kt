package shibbir.me.alquranquotes.navigation

import androidx.navigation.NavOptionsBuilder

/**
 * Standard bottom navigation options. Going to a tab pops everything above the graph's start
 * destination ([startDestinationId]) and saves it, so the back stack never grows past one copy
 * of each tab, and a tab opened again comes back where the user left it.
 */
fun NavOptionsBuilder.applyTabNavigationOptions(startDestinationId: Int) {
    popUpTo(startDestinationId) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
