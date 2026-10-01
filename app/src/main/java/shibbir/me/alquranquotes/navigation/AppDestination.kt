package shibbir.me.alquranquotes.navigation

import kotlinx.serialization.Serializable

/** Every screen the app can navigate to, as type-safe Navigation Compose routes. */
@Serializable
sealed interface AppDestination {

    /** Bottom navigation tab: the daily quote. The start destination. */
    @Serializable
    data object Home : AppDestination

    /** Bottom navigation tab: all quotes, with add, edit, and delete. */
    @Serializable
    data object Quotes : AppDestination

    /** Bottom navigation tab: settings (placeholder for now). */
    @Serializable
    data object Settings : AppDestination

    /** The quote editor for a new quote of the user's own. */
    @Serializable
    data object AddQuote : AppDestination

    /** The quote editor for an existing quote, bundled or the user's own. */
    @Serializable
    data class EditQuote(val quoteId: Long) : AppDestination {
        companion object {
            /** SavedStateHandle key of [quoteId]; it must match the property name. */
            const val QUOTE_ID_KEY = "quoteId"
        }
    }
}
