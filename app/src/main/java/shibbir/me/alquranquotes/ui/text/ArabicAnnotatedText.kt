package shibbir.me.alquranquotes.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.withStyle

/** Language of the ayah's Arabic text, so TalkBack reads it with an Arabic voice. */
internal const val ARABIC_LANGUAGE_TAG = "ar"

/**
 * Marks [arabicText] as Arabic. The locale is set on a span because span locales reach the
 * accessibility text, so TalkBack can read the ayah with an Arabic voice. A locale on the
 * TextStyle only affects drawing.
 */
@Composable
internal fun rememberArabicAnnotatedText(arabicText: String): AnnotatedString {
    return remember(arabicText) {
        val arabicSpanStyle = SpanStyle(localeList = LocaleList(ARABIC_LANGUAGE_TAG))
        buildAnnotatedString {
            withStyle(arabicSpanStyle) {
                append(arabicText)
            }
        }
    }
}
