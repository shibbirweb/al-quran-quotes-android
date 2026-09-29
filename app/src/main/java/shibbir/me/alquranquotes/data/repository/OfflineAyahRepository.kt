package shibbir.me.alquranquotes.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import shibbir.me.alquranquotes.data.local.AyahDao
import shibbir.me.alquranquotes.data.local.toAyah
import shibbir.me.alquranquotes.data.local.toAyahEntity
import shibbir.me.alquranquotes.data.seed.AyahSeed
import shibbir.me.alquranquotes.data.seed.AyahSeedSource
import shibbir.me.alquranquotes.model.Ayah
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serves ayahs from Room. Once per process it compares the stored seed with the bundled one and
 * replaces every stored ayah when the versions differ or the table is empty, so asset updates
 * reach installed apps and a broken table heals itself.
 *
 * It is safe to call from the main thread without switching dispatchers: [AyahSeedSource.load]
 * is main-safe by contract, and Room runs suspend DAO calls on its own background executors.
 */
@Singleton
class OfflineAyahRepository @Inject constructor(
    private val ayahDao: AyahDao,
    private val ayahSeedSource: AyahSeedSource,
) : AyahRepository {

    private val seedCheckMutex = Mutex()

    /** Only read and written while holding [seedCheckMutex]. */
    private var isSeedChecked = false

    override suspend fun getDailyAyah(epochDay: Long): Ayah? {
        ensureStoredSeedIsCurrent()
        val dailyAyahEntity = ayahDao.getAyahForDay(epochDay)
        return dailyAyahEntity?.toAyah()
    }

    /**
     * Marks the check done only after it succeeds, so a failed load or replace is retried on the
     * next call.
     */
    private suspend fun ensureStoredSeedIsCurrent() {
        seedCheckMutex.withLock {
            if (isSeedChecked) {
                return
            }
            val ayahSeed = ayahSeedSource.load()
            if (!isStoredSeedCurrent(ayahSeed)) {
                replaceStoredAyahs(ayahSeed)
            }
            isSeedChecked = true
        }
    }

    /**
     * Only an exact version match is current, so an older or a newer stored seed is replaced. An
     * empty table is never current, even at the same version, so it gets seeded again.
     */
    private suspend fun isStoredSeedCurrent(ayahSeed: AyahSeed): Boolean {
        val storedSeedVersion = ayahDao.getSeedVersion()
        if (storedSeedVersion != ayahSeed.version) {
            return false
        }
        val storedAyahCount = ayahDao.countAyahs()
        return storedAyahCount > 0
    }

    private suspend fun replaceStoredAyahs(ayahSeed: AyahSeed) {
        val ayahEntities = ayahSeed.ayahs.map { ayah -> ayah.toAyahEntity() }
        ayahDao.replaceAllAyahs(ayahEntities, ayahSeed.version)
    }
}
