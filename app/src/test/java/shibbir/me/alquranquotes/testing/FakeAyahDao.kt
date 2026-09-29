package shibbir.me.alquranquotes.testing

import shibbir.me.alquranquotes.data.local.AyahDao
import shibbir.me.alquranquotes.data.local.AyahEntity
import shibbir.me.alquranquotes.data.local.AyahSeedInfoEntity

/**
 * In-memory [AyahDao] that behaves like the Room one: it orders ayahs by surah, then ayah number,
 * rejects a duplicate (surah, ayah) key, and rolls [replaceAllAyahs] back when it fails. It does
 * not override [getAyahForDay], so tests run the real DAO body on top of this in-memory table.
 */
class FakeAyahDao(
    initialAyahEntities: List<AyahEntity> = emptyList(),
    initialSeedVersion: Int? = null,
) : AyahDao() {

    /** Stands in for the `ayahs` table. */
    private val ayahTable = initialAyahEntities.toMutableList()

    val storedAyahEntities: List<AyahEntity>
        get() = ayahTable.toList()

    var storedSeedVersion: Int? = initialSeedVersion
        private set

    var replaceCallCount = 0
        private set

    /** The (surah number, ayah number) keys of the stored ayahs. */
    fun storedAyahKeys(): Set<Pair<Int, Int>> {
        val ayahKeys = storedAyahEntities.map { it.surahNumber to it.ayahNumber }
        return ayahKeys.toSet()
    }

    override suspend fun countAyahs(): Int = ayahTable.size

    override suspend fun getAyahAtPosition(position: Int): AyahEntity? {
        val surahThenAyahOrder = compareBy<AyahEntity>({ it.surahNumber }, { it.ayahNumber })
        val orderedAyahEntities = ayahTable.sortedWith(surahThenAyahOrder)
        return orderedAyahEntities.getOrNull(position)
    }

    override suspend fun getSeedVersion(): Int? = storedSeedVersion

    override suspend fun replaceAllAyahs(ayahEntities: List<AyahEntity>, seedVersion: Int) {
        replaceCallCount++
        val ayahEntitiesBeforeReplace = ayahTable.toList()
        val seedVersionBeforeReplace = storedSeedVersion
        try {
            super.replaceAllAyahs(ayahEntities, seedVersion)
        } catch (replaceFailure: Exception) {
            rollBack(ayahEntitiesBeforeReplace, seedVersionBeforeReplace)
            throw replaceFailure
        }
    }

    /** Throws on a duplicate key like the real `@Insert` (conflict strategy ABORT). */
    override suspend fun insertAyahs(ayahEntities: List<AyahEntity>) {
        ayahEntities.forEach { ayahEntity -> insertAyah(ayahEntity) }
    }

    override suspend fun deleteAllAyahs() {
        ayahTable.clear()
    }

    override suspend fun upsertSeedInfo(seedInfo: AyahSeedInfoEntity) {
        storedSeedVersion = seedInfo.seedVersion
    }

    private fun insertAyah(ayahEntity: AyahEntity) {
        val isDuplicateKey = ayahTable.any { storedAyahEntity ->
            storedAyahEntity.surahNumber == ayahEntity.surahNumber &&
                storedAyahEntity.ayahNumber == ayahEntity.ayahNumber
        }
        check(!isDuplicateKey) {
            "UNIQUE constraint failed: ayahs.surah_number, ayahs.ayah_number"
        }
        ayahTable += ayahEntity
    }

    private fun rollBack(ayahEntities: List<AyahEntity>, seedVersion: Int?) {
        ayahTable.clear()
        ayahTable += ayahEntities
        storedSeedVersion = seedVersion
    }
}
