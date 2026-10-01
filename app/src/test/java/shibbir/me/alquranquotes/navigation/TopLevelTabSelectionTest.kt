package shibbir.me.alquranquotes.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Which tab the bottom bar marks as selected for the route on top of the back stack. */
class TopLevelTabSelectionTest {

    @Test
    fun quotesRouteSelectsTheQuotesTab() {
        val selectedTab = TopLevelTab.selectedBy { routeClass ->
            routeClass == AppDestination.Quotes::class
        }

        assertEquals(TopLevelTab.QUOTES, selectedTab)
    }

    @Test
    fun addQuoteRouteSelectsNoTab() {
        val selectedTab = TopLevelTab.selectedBy { routeClass ->
            routeClass == AppDestination.AddQuote::class
        }

        assertNull(selectedTab)
    }

    @Test
    fun editQuoteRouteSelectsNoTab() {
        val selectedTab = TopLevelTab.selectedBy { routeClass ->
            routeClass == AppDestination.EditQuote::class
        }

        assertNull(selectedTab)
    }

    @Test
    fun noRouteYetSelectsNoTab() {
        val selectedTab = TopLevelTab.selectedBy { false }

        assertNull(selectedTab)
    }
}
