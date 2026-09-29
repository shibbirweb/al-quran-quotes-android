package shibbir.me.alquranquotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteRoute
import shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteScreen
import shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteUiState
import shibbir.me.alquranquotes.feature.dailyquote.dailyQuotePreviewAyah
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlQuranQuotesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // Also keeps text clear of display cutouts, for example in landscape.
                    contentWindowInsets = WindowInsets.safeDrawing,
                ) { innerPadding ->
                    DailyQuoteRoute(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Previews cannot create Hilt ViewModels, so this shows the stateless screen with a sample ayah.
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainActivityPreview() {
    AlQuranQuotesTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { innerPadding ->
            DailyQuoteScreen(
                uiState = DailyQuoteUiState.Success(dailyQuotePreviewAyah),
                onRetry = {},
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
