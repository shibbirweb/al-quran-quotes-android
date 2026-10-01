package shibbir.me.alquranquotes.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

private const val TAB_TITLE = "Tab title"

/** The large, collapsing top app bar of a bottom navigation tab. */
@RunWith(AndroidJUnit4::class)
class TopLevelTopAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalMaterial3Api::class)
    @Before
    fun showTopLevelTopAppBar() {
        composeTestRule.setContent {
            AlQuranQuotesTheme {
                TopLevelTopAppBar(
                    title = TAB_TITLE,
                    scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
                )
            }
        }
    }

    @Test
    fun marksTheTitleAsAHeading() {
        composeTestRule.onNode(hasText(TAB_TITLE) and isHeading()).assertExists()
    }

    /** The large bar draws its title twice (expanded and collapsed); TalkBack must hear one. */
    @Test
    fun exposesTheTitleToAccessibilityOnce() {
        composeTestRule.onAllNodesWithText(TAB_TITLE).assertCountEquals(1)
    }
}
