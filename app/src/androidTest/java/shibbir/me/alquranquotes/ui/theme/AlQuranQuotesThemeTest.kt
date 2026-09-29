package shibbir.me.alquranquotes.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Checks which color scheme the theme uses with dynamic color on and off. */
@RunWith(AndroidJUnit4::class)
class AlQuranQuotesThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun darkThemeWithoutDynamicColorUsesTheDarkPrimary() {
        val primaryColor = themePrimaryColor(useDarkTheme = true, useDynamicColor = false)

        assertEquals(DarkPrimary, primaryColor)
    }

    @Test
    fun lightThemeWithoutDynamicColorUsesTheLightPrimary() {
        val primaryColor = themePrimaryColor(useDarkTheme = false, useDynamicColor = false)

        assertEquals(LightPrimary, primaryColor)
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.S)
    fun lightThemeWithDynamicColorUsesTheDynamicLightPrimary() {
        var dynamicLightPrimary = Color.Unspecified

        val primaryColor = themePrimaryColor(useDarkTheme = false, useDynamicColor = true) {
            val context = LocalContext.current
            dynamicLightPrimary = dynamicLightColorScheme(context).primary
        }

        assertEquals(dynamicLightPrimary, primaryColor)
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.S)
    fun darkThemeWithDynamicColorUsesTheDynamicDarkPrimary() {
        var dynamicDarkPrimary = Color.Unspecified

        val primaryColor = themePrimaryColor(useDarkTheme = true, useDynamicColor = true) {
            val context = LocalContext.current
            dynamicDarkPrimary = dynamicDarkColorScheme(context).primary
        }

        assertEquals(dynamicDarkPrimary, primaryColor)
    }

    /**
     * Returns the primary color that [AlQuranQuotesTheme] provides. [readExpectedColor] runs in
     * the same composition, so a test can read the platform's colors from LocalContext.
     */
    private fun themePrimaryColor(
        useDarkTheme: Boolean,
        useDynamicColor: Boolean,
        readExpectedColor: @Composable () -> Unit = {},
    ): Color {
        var primaryColor = Color.Unspecified
        composeTestRule.setContent {
            readExpectedColor()
            AlQuranQuotesTheme(
                useDarkTheme = useDarkTheme,
                useDynamicColor = useDynamicColor,
            ) {
                primaryColor = MaterialTheme.colorScheme.primary
            }
        }
        composeTestRule.waitForIdle()
        return primaryColor
    }
}
