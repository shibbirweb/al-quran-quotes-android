package shibbir.me.alquranquotes.data.repository

import shibbir.me.alquranquotes.data.local.DailyQuoteDao
import shibbir.me.alquranquotes.data.seed.BundledAyahSeeder
import shibbir.me.alquranquotes.model.Quote
import javax.inject.Inject

/**
 * Serves the daily quote from Room, after making sure the stored bundled ayahs are current. Safe
 * to call from the main thread, like [BundledAyahSeeder] and Room's suspend DAO calls.
 */
class OfflineDailyQuoteRepository @Inject constructor(
    private val bundledAyahSeeder: BundledAyahSeeder,
    private val dailyQuoteDao: DailyQuoteDao,
) : DailyQuoteRepository {

    override suspend fun getDailyQuote(epochDay: Long): Quote? {
        bundledAyahSeeder.ensureStoredSeedIsCurrent()
        return dailyQuoteDao.getDailyQuote(epochDay)
    }
}
