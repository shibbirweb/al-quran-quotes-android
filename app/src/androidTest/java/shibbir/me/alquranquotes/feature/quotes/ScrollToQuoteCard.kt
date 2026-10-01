package shibbir.me.alquranquotes.feature.quotes

import androidx.compose.ui.test.hasScrollToKeyAction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performScrollToKey

/**
 * Scrolls the Quotes list to the card of [quoteCard], keyed by its quote id. The list is lazy,
 * so on a small screen (such as the CI emulator) a card further down does not exist until it is
 * scrolled into view.
 */
fun ComposeContentTestRule.scrollToQuoteCard(quoteCard: QuoteListCard) {
    onNode(hasScrollToKeyAction()).performScrollToKey(quoteCard.quoteId)
}
