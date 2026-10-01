package shibbir.me.alquranquotes.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.reflect.KClass
import shibbir.me.alquranquotes.R

/** The tabs of the bottom navigation bar, in the order they are shown. */
enum class TopLevelTab(
    @param:StringRes val labelResId: Int,
    val icon: ImageVector,
    val destination: AppDestination,
) {
    HOME(
        labelResId = R.string.navigation_tab_home,
        icon = Icons.Default.Home,
        destination = AppDestination.Home,
    ),
    QUOTES(
        labelResId = R.string.navigation_tab_quotes,
        icon = Icons.AutoMirrored.Filled.List,
        destination = AppDestination.Quotes,
    ),
    SETTINGS(
        labelResId = R.string.navigation_tab_settings,
        icon = Icons.Default.Settings,
        destination = AppDestination.Settings,
    ),
    ;

    companion object {
        /** The tab whose route [isCurrentRoute] matches, or null when no tab is on screen. */
        fun selectedBy(isCurrentRoute: (KClass<out AppDestination>) -> Boolean): TopLevelTab? {
            return entries.firstOrNull { tab -> isCurrentRoute(tab.destination::class) }
        }
    }
}
