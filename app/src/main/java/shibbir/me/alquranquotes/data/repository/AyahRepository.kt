package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import shibbir.me.alquranquotes.core.coroutines.IoDispatcher
import shibbir.me.alquranquotes.data.local.AyahDao
import shibbir.me.alquranquotes.data.local.toAyah
import shibbir.me.alquranquotes.data.seed.AyahSeedSource
import shibbir.me.alquranquotes.model.Ayah
import javax.inject.Inject

interface AyahRepository {
    /** Returns the ayah for [epochDay], or null when no ayahs are available. */
    suspend fun getDailyAyah(epochDay: Long): Ayah?
}

/** Serves ayahs from Room, filling the database from the bundled seed on first use. */
class OfflineAyahRepository @Inject constructor(
    private val ayahDao: AyahDao,
    private val seedSource: AyahSeedSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AyahRepository {

    override suspend fun getDailyAyah(epochDay: Long): Ayah? = withContext(ioDispatcher) {
        var ayahCount = ayahDao.count()
        if (ayahCount == 0) {
            ayahDao.insertAll(seedSource.load())
            ayahCount = ayahDao.count()
        }
        if (ayahCount == 0) {
            return@withContext null
        }
        ayahDao.getAt(dailyAyahIndex(epochDay, ayahCount))?.toAyah()
    }
}

/** Maps a day to an ayah position so every day shows one ayah and the list repeats after [ayahCount] days. */
internal fun dailyAyahIndex(epochDay: Long, ayahCount: Int): Int {
    require(ayahCount > 0) { "ayahCount must be positive" }
    return epochDay.mod(ayahCount.toLong()).toInt()
}
