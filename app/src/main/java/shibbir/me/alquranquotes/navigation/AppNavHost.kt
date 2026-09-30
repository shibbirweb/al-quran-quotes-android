package shibbir.me.alquranquotes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import shibbir.me.alquranquotes.feature.dailyquote.DailyQuoteRoute
import shibbir.me.alquranquotes.feature.quoteeditor.QuoteEditorRoute
import shibbir.me.alquranquotes.feature.quotes.QuotesRoute
import shibbir.me.alquranquotes.feature.settings.SettingsScreen

/**
 * Every [AppDestination] and the screen it shows, starting on [AppDestination.Home]. Screens
 * change with the short fade through from [NavigationMotion], not the slow default crossfade.
 * Each screen draws its own Scaffold and top app bar. The quote editor opens from the Quotes tab
 * and goes back to it when done.
 */
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val openNewQuoteEditor: () -> Unit = { navController.navigate(AppDestination.AddQuote) }
    val openQuoteEditor: (Long) -> Unit = { quoteId ->
        navController.navigate(AppDestination.EditQuote(quoteId))
    }
    val returnFromEditor: () -> Unit = { navController.popBackStack() }
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home,
        modifier = modifier,
        enterTransition = { NavigationMotion.fadeThroughEnter() },
        exitTransition = { NavigationMotion.fadeThroughExit() },
    ) {
        composable<AppDestination.Home> { DailyQuoteRoute() }
        composable<AppDestination.Quotes> {
            QuotesRoute(onAddQuote = openNewQuoteEditor, onEditQuote = openQuoteEditor)
        }
        composable<AppDestination.Settings> { SettingsScreen() }
        composable<AppDestination.AddQuote> { QuoteEditorRoute(onDone = returnFromEditor) }
        composable<AppDestination.EditQuote> { QuoteEditorRoute(onDone = returnFromEditor) }
    }
}
