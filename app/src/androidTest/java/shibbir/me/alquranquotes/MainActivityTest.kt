package shibbir.me.alquranquotes

import android.content.Context
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.di.QURAN_DATABASE_NAME
import shibbir.me.alquranquotes.navigation.TopLevelTab

private const val DAILY_AYAH_TIMEOUT_MILLIS = 30_000L

/**
 * Smoke test for the real app: Hilt graph, Room, the bundled ayah asset, and the bottom
 * navigation. It waits for the daily ayah title, which only appears once a real ayah has loaded,
 * then moves between the tabs. It only checks things the app shell owns (tab labels, tab
 * selection, and the Settings title), so changes inside the Quotes screen do not break it.
 *
 * This class must hold exactly one test. It runs the real Hilt graph, whose singleton database
 * stays open for the whole test process, so a second test would delete the database file under
 * that open singleton.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    /** Starts from an empty database, so a stale one from an older build cannot break the test. */
    private val deleteDatabaseRule = object : ExternalResource() {
        override fun before() {
            targetContext.deleteDatabase(QURAN_DATABASE_NAME)
        }
    }

    private val composeTestRule = createAndroidComposeRule<MainActivity>()

    /** Deletes the database before the compose rule launches the activity. */
    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(deleteDatabaseRule).around(composeTestRule)

    @Test
    fun launchesOnTheAyahOfTheDayAndMovesBetweenTabs() {
        waitForDailyAyahTitle()
        tabNode(TopLevelTab.HOME).assertIsSelected()

        tabNode(TopLevelTab.QUOTES).performClick()
        tabNode(TopLevelTab.QUOTES).assertIsSelected()

        tabNode(TopLevelTab.SETTINGS).performClick()
        tabNode(TopLevelTab.SETTINGS).assertIsSelected()
        settingsTitleNode().assertIsDisplayed()

        tabNode(TopLevelTab.HOME).performClick()
        tabNode(TopLevelTab.HOME).assertIsSelected()
        waitForDailyAyahTitle()
    }

    private fun waitForDailyAyahTitle() {
        val title = composeTestRule.activity.getString(R.string.daily_quote_title)

        composeTestRule.waitUntil(timeoutMillis = DAILY_AYAH_TIMEOUT_MILLIS) {
            val titleNodes = composeTestRule.onAllNodesWithText(title).fetchSemanticsNodes()
            titleNodes.isNotEmpty()
        }

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
    }

    /** A bottom bar item; the Settings screen title has the same text but is not selectable. */
    private fun tabNode(tab: TopLevelTab): SemanticsNodeInteraction {
        val tabLabel = composeTestRule.activity.getString(tab.labelResId)
        return composeTestRule.onNode(hasText(tabLabel) and isSelectable())
    }

    private fun settingsTitleNode(): SemanticsNodeInteraction {
        val settingsTitle = composeTestRule.activity.getString(R.string.settings_title)
        return composeTestRule.onNode(hasText(settingsTitle) and !isSelectable())
    }
}
