package shibbir.me.alquranquotes.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

private const val DETAIL_TITLE = "Detail title"

private const val BACK_DESCRIPTION = "Go back"

private const val ACTION_LABEL = "Action"

/** The top app bar of a screen opened from a tab: title, back arrow, and actions. */
@RunWith(AndroidJUnit4::class)
class DetailTopAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var backCount = 0

    private var actionCount = 0

    @OptIn(ExperimentalMaterial3Api::class)
    @Before
    fun showDetailTopAppBar() {
        composeTestRule.setContent {
            AlQuranQuotesTheme {
                DetailTopAppBar(
                    title = DETAIL_TITLE,
                    backContentDescription = BACK_DESCRIPTION,
                    onBack = { backCount += 1 },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                    actions = {
                        TextButton(onClick = { actionCount += 1 }) {
                            Text(text = ACTION_LABEL)
                        }
                    },
                )
            }
        }
    }

    @Test
    fun marksTheTitleAsAHeading() {
        composeTestRule.onNode(hasText(DETAIL_TITLE) and isHeading()).assertExists()
    }

    @Test
    fun backButtonHasItsContentDescriptionAndCallsOnBack() {
        composeTestRule.onNodeWithContentDescription(BACK_DESCRIPTION).performClick()

        assertEquals(1, backCount)
    }

    @Test
    fun showsTheActions() {
        composeTestRule.onNodeWithText(ACTION_LABEL).performClick()

        assertEquals(1, actionCount)
        assertEquals(0, backCount)
    }
}
