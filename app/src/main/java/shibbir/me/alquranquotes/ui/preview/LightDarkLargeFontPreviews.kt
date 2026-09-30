package shibbir.me.alquranquotes.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/** Shows a preview in the light theme, the dark theme, and at twice the default font size. */
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large font", fontScale = 2f)
annotation class LightDarkLargeFontPreviews
