package shibbir.me.alquranquotes.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.R

class TopLevelTabTest {

    @Test
    fun tabsAreHomeQuotesSettingsInThatOrder() {
        val tabDestinations = TopLevelTab.entries.map { it.destination }

        val expectedDestinations = listOf(
            AppDestination.Home,
            AppDestination.Quotes,
            AppDestination.Settings,
        )
        assertEquals(expectedDestinations, tabDestinations)
    }

    @Test
    fun eachTabHasItsOwnLabel() {
        val tabLabels = TopLevelTab.entries.map { it.labelResId }

        val expectedLabels = listOf(
            R.string.navigation_tab_home,
            R.string.navigation_tab_quotes,
            R.string.navigation_tab_settings,
        )
        assertEquals(expectedLabels, tabLabels)
    }

    @Test
    fun eachTabHasItsOwnIcon() {
        val tabIcons = TopLevelTab.entries.map { it.icon }

        val expectedIcons = listOf(
            Icons.Default.Home,
            Icons.AutoMirrored.Filled.List,
            Icons.Default.Settings,
        )
        assertEquals(expectedIcons, tabIcons)
    }
}
