package shibbir.me.alquranquotes.feature.settings

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shibbir.me.alquranquotes.R
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

/** The static Settings placeholder: its title, sample rows, and the coming soon line. */
@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val targetContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun showSettingsScreen() {
        composeTestRule.setContent {
            AlQuranQuotesTheme {
                SettingsScreen()
            }
        }
    }

    @Test
    fun topAppBarShowsTheSettingsTitle() {
        val settingsTitle = targetContext.getString(R.string.settings_title)

        composeTestRule.onNodeWithText(settingsTitle).assertExists()
    }

    @Test
    fun showsTheThemeAndTextSizeRows() {
        val themeTitle = targetContext.getString(R.string.settings_theme_title)
        val textSizeTitle = targetContext.getString(R.string.settings_text_size_title)

        composeTestRule.onNodeWithText(themeTitle).assertExists()
        composeTestRule.onNodeWithText(textSizeTitle).assertExists()
    }

    @Test
    fun aboutRowShowsTheAppNameAndVersion() {
        val appName = targetContext.getString(R.string.app_name)
        val versionName = installedVersionName()
        val aboutText = targetContext.getString(R.string.settings_about_value, appName, versionName)

        composeTestRule.onNodeWithText(aboutText).assertExists()
    }

    @Test
    fun saysMoreSettingsAreComingSoon() {
        val comingSoonText = targetContext.getString(R.string.settings_coming_soon)

        composeTestRule.onNodeWithText(comingSoonText).assertExists()
    }

    @Suppress("DEPRECATION")
    private fun installedVersionName(): String {
        val packageManager = targetContext.packageManager
        val packageInfo = packageManager.getPackageInfo(targetContext.packageName, 0)
        return packageInfo.versionName.orEmpty()
    }
}
