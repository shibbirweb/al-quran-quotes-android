package shibbir.me.alquranquotes.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationMotionTest {

    @Test
    fun screenSwitchFinishesWithinMaterialFadeThroughDuration() {
        val fadeOutMillis = NavigationMotion.FADE_OUT_MILLIS
        val fadeInMillis = NavigationMotion.FADE_IN_MILLIS
        val switchDurationMillis = fadeOutMillis + fadeInMillis

        assertTrue(switchDurationMillis <= MATERIAL_FADE_THROUGH_MILLIS)
    }

    @Test
    fun oldScreenFadesOutQuickly() {
        val fadeOutSpec = tween<Float>(durationMillis = NavigationMotion.FADE_OUT_MILLIS)
        val expectedExitTransition = fadeOut(fadeOutSpec)

        assertEquals(expectedExitTransition, NavigationMotion.fadeThroughExit())
    }

    @Test
    fun newScreenFadesInOnlyAfterTheOldOneIsGone() {
        val expectedEnterTransition = fadeIn(
            tween(
                durationMillis = NavigationMotion.FADE_IN_MILLIS,
                delayMillis = NavigationMotion.FADE_OUT_MILLIS,
            ),
        )

        assertEquals(expectedEnterTransition, NavigationMotion.fadeThroughEnter())
    }

    private companion object {
        /** Material 3 fade through: about 300 ms in total, with the exit taking the first 90 ms. */
        const val MATERIAL_FADE_THROUGH_MILLIS = 300
    }
}
