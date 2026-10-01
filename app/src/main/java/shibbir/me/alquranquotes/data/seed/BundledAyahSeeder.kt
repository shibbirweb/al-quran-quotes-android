package shibbir.me.alquranquotes.data.seed

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import shibbir.me.alquranquotes.data.local.BundledAyahSeedDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keeps the stored bundled ayahs in step with the bundled seed. Once per process it compares the
 * stored seed version with the bundled one and, when they differ or the quotes table is empty,
 * merges the seed in ([BundledAyahSeedDao.mergeBundledSeed]). The user's changes always win: the
 * merge never overwrites an edited bundled ayah, never adds back a deleted one, and never touches
 * the user's own quotes.
 *
 * It is safe to call from the main thread without switching dispatchers: [AyahSeedSource.load]
 * is main-safe by contract, and Room runs suspend DAO calls on its own background executors.
 */
@Singleton
class BundledAyahSeeder @Inject constructor(
    private val bundledAyahSeedDao: BundledAyahSeedDao,
    private val ayahSeedSource: AyahSeedSource,
) {

    private val seedCheckMutex = Mutex()

    /** Only read and written while holding [seedCheckMutex]. */
    private var isSeedChecked = false

    /**
     * Merges the seed when the stored one is not current. Marks the check done only after it
     * succeeds, so a failed load or merge is retried on the next call.
     */
    suspend fun ensureStoredSeedIsCurrent() {
        seedCheckMutex.withLock {
            if (isSeedChecked) {
                return
            }
            val ayahSeed = ayahSeedSource.load()
            if (!isStoredSeedCurrent(ayahSeed)) {
                mergeBundledSeed(ayahSeed)
            }
            isSeedChecked = true
        }
    }

    /**
     * Only an exact version match is current, so an older or a newer stored seed is merged. An
     * empty quotes table is never current, even at the same version, so it gets seeded again.
     */
    private suspend fun isStoredSeedCurrent(ayahSeed: AyahSeed): Boolean {
        val storedSeedVersion = bundledAyahSeedDao.getSeedVersion()
        if (storedSeedVersion != ayahSeed.version) {
            return false
        }
        val storedQuoteCount = bundledAyahSeedDao.countQuotes()
        return storedQuoteCount > 0
    }

    private suspend fun mergeBundledSeed(ayahSeed: AyahSeed) {
        val seedQuoteEntities = ayahSeed.ayahs.map { ayah -> ayah.toBundledQuoteEntity() }
        bundledAyahSeedDao.mergeBundledSeed(seedQuoteEntities, ayahSeed.version)
    }
}
