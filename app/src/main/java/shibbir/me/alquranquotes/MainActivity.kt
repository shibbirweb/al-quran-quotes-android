package shibbir.me.alquranquotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteScreen
import shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteUiState
import shibbir.me.alquranquotes.feature.dailyquote.dailyQuotePreviewQuote
import shibbir.me.alquranquotes.navigation.QuranQuotesAppScaffold
import shibbir.me.alquranquotes.navigation.QuranQuotesAppShell
import shibbir.me.alquranquotes.navigation.TopLevelTab
import shibbir.me.alquranquotes.ui.theme.AlQuranQuotesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlQuranQuotesTheme {
                QuranQuotesAppShell()
            }
        }
    }
}

// Previews cannot create Hilt ViewModels, so this shows the app frame on the Home tab with the
// stateless daily quote screen and a sample ayah.
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainActivityPreview() {
    AlQuranQuotesTheme {
        QuranQuotesAppScaffold(
            selectedTab = TopLevelTab.HOME,
            onTabSelected = {},
        ) { contentModifier ->
            DailyQuoteScreen(
                uiState = DailyQuoteUiState.Success(dailyQuotePreviewQuote()),
                onRetry = {},
                modifier = contentModifier,
            )
        }
    }
}
