package shibbir.me.alquranquotes.navigation

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

private const val PLACEHOLDER_CONTENT_TEXT = "Placeholder content"

/** The stateless app frame: bottom navigation bar, tab selection, and hiding the bar. */
@RunWith(AndroidJUnit4::class)
class QuranQuotesAppScaffoldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun showsTheContent() {
        setAppScaffold(selectedTab = TopLevelTab.HOME)

        composeTestRule.onNodeWithText(PLACEHOLDER_CONTENT_TEXT).assertExists()
    }

    @Test
    fun marksOnlyTheSelectedTabAsSelected() {
        setAppScaffold(selectedTab = TopLevelTab.QUOTES)

        tabNode(TopLevelTab.HOME).assertIsNotSelected()
        tabNode(TopLevelTab.QUOTES).assertIsSelected()
        tabNode(TopLevelTab.SETTINGS).assertIsNotSelected()
    }

    @Test
    fun tappingATabReportsThatTab() {
        var tappedTab: TopLevelTab? = null
        setAppScaffold(
            selectedTab = TopLevelTab.HOME,
            onTabSelected = { tab -> tappedTab = tab },
        )

        tabNode(TopLevelTab.SETTINGS).performClick()

        assertEquals(TopLevelTab.SETTINGS, tappedTab)
    }

    @Test
    fun hidesTheBottomBarWhenNoTabIsSelected() {
        setAppScaffold(selectedTab = null)

        TopLevelTab.entries.forEach { tab -> tabNode(tab).assertDoesNotExist() }
        composeTestRule.onNodeWithText(PLACEHOLDER_CONTENT_TEXT).assertExists()
    }

    private fun setAppScaffold(
        selectedTab: TopLevelTab?,
        onTabSelected: (TopLevelTab) -> Unit = {},
    ) {
        composeTestRule.setContent {
            AlQuranQuotesTheme {
                QuranQuotesAppScaffold(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected,
                ) { contentModifier ->
                    Text(text = PLACEHOLDER_CONTENT_TEXT, modifier = contentModifier)
                }
            }
        }
    }

    private fun tabNode(tab: TopLevelTab): SemanticsNodeInteraction {
        val tabLabel = targetContext.getString(tab.labelResId)
        return composeTestRule.onNode(hasText(tabLabel) and isSelectable())
    }
}
