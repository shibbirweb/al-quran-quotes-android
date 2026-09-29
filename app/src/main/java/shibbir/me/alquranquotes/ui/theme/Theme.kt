package shibbir.me.alquranquotes.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary,
)

/**
 * App theme. Uses Material You dynamic color on Android 12+ when [useDynamicColor] is true.
 * Otherwise it uses the static light and dark schemes defined above, which are built from the
 * brand colors in Color.kt.
 */
@Composable
fun AlQuranQuotesTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = selectColorScheme(
        useDarkTheme = useDarkTheme,
        useDynamicColor = useDynamicColor,
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}

@Composable
private fun selectColorScheme(useDarkTheme: Boolean, useDynamicColor: Boolean): ColorScheme {
    if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (useDarkTheme) {
            return dynamicDarkColorScheme(context)
        }
        return dynamicLightColorScheme(context)
    }
    if (useDarkTheme) {
        return DarkColorScheme
    }
    return LightColorScheme
}
