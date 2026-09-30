package shibbir.me.alquranquotes.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

/**
 * How screens change, using Material 3 "fade through": the old screen fades out quickly, then
 * the new one fades in, 300 ms in total. Navigation Compose's default is a 700 ms crossfade that
 * keeps both screens drawing at once, which makes switching tabs feel slow.
 */
object NavigationMotion {

    const val FADE_OUT_MILLIS = 90

    const val FADE_IN_MILLIS = 210

    fun fadeThroughExit(): ExitTransition = fadeOut(tween(durationMillis = FADE_OUT_MILLIS))

    /** Starts once [fadeThroughExit] has finished, so the two screens barely overlap. */
    fun fadeThroughEnter(): EnterTransition {
        val fadeInSpec = tween<Float>(
            durationMillis = FADE_IN_MILLIS,
            delayMillis = FADE_OUT_MILLIS,
        )
        return fadeIn(fadeInSpec)
    }
}
