package shibbir.me.alquranquotes.feature.dailyquote

import shibbir.me.alquranquotes.data.repository.DailyQuoteRepository
import shibbir.me.alquranquotes.model.Quote
import java.util.concurrent.ConcurrentHashMap

/**
 * Device-test repository that answers at once with the quote stored for the requested day, or
 * null (shown as Error) when that day has none. Thread safe, because tests fill it from the
 * instrumentation thread while the ViewModel reads it on the main thread.
 */
class FakeDailyQuoteRepository : DailyQuoteRepository {

    val dailyQuotesByEpochDay = ConcurrentHashMap<Long, Quote>()

    override suspend fun getDailyQuote(epochDay: Long): Quote? = dailyQuotesByEpochDay[epochDay]
}
