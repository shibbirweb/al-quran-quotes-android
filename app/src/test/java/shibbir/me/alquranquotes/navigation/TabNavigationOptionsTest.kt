package shibbir.me.alquranquotes.navigation

import androidx.navigation.navOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The standard bottom navigation options: one copy of each tab, each tab keeps its state. */
class TabNavigationOptionsTest {

    private val startDestinationId = 42

    private val tabNavigationOptions = navOptions {
        applyTabNavigationOptions(startDestinationId = startDestinationId)
    }

    @Test
    fun popsUpToTheStartDestination() {
        assertEquals(startDestinationId, tabNavigationOptions.popUpToId)
    }

    @Test
    fun savesTheStateOfTheTabItLeaves() {
        assertTrue(tabNavigationOptions.shouldPopUpToSaveState())
    }

    @Test
    fun launchesEachTabAsASingleCopy() {
        assertTrue(tabNavigationOptions.shouldLaunchSingleTop())
    }

    @Test
    fun restoresTheSavedStateOfTheTabItOpens() {
        assertTrue(tabNavigationOptions.shouldRestoreState())
    }
}
