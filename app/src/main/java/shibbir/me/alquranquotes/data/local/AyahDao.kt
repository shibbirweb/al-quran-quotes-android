package shibbir.me.alquranquotes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
abstract class AyahDao {

    @Query("SELECT COUNT(*) FROM ayahs")
    abstract suspend fun countAyahs(): Int

    /**
     * Returns the ayah at [position] when all ayahs are ordered by surah number, then ayah
     * number, or null when [position] is past the last ayah.
     */
    @Query("SELECT * FROM ayahs ORDER BY surah_number, ayah_number LIMIT 1 OFFSET :position")
    abstract suspend fun getAyahAtPosition(position: Int): AyahEntity?

    /**
     * Returns the ayah for [epochDay], or null when no ayahs are stored. The count and the lookup
     * run in one transaction, so a replace in between cannot make them disagree.
     *
     * Room's @Transaction takes a write transaction even for this read. For two small queries
     * that cost is negligible, and it keeps the count and the lookup consistent.
     */
    @Transaction
    open suspend fun getAyahForDay(epochDay: Long): AyahEntity? {
        val ayahCount = countAyahs()
        if (ayahCount == 0) {
            return null
        }
        val dailyPosition = dailyAyahPosition(epochDay, ayahCount)
        return getAyahAtPosition(dailyPosition)
    }

    /** Returns the seed version recorded by [replaceAllAyahs], or null before the first seed. */
    @Query(
        "SELECT seed_version FROM ayah_seed_info WHERE id = ${AyahSeedInfoEntity.SINGLE_ROW_ID}",
    )
    abstract suspend fun getSeedVersion(): Int?

    /**
     * Replaces every stored ayah with [ayahEntities] and records [seedVersion], all in one
     * transaction: when any step fails, the previous ayahs and version stay as they were.
     */
    @Transaction
    open suspend fun replaceAllAyahs(ayahEntities: List<AyahEntity>, seedVersion: Int) {
        deleteAllAyahs()
        insertAyahs(ayahEntities)
        val seedInfo = AyahSeedInfoEntity(seedVersion = seedVersion)
        upsertSeedInfo(seedInfo)
    }

    /** Fails on a duplicate (surah, ayah) pair, which rolls back [replaceAllAyahs]. */
    @Insert
    protected abstract suspend fun insertAyahs(ayahEntities: List<AyahEntity>)

    @Query("DELETE FROM ayahs")
    protected abstract suspend fun deleteAllAyahs()

    @Upsert
    protected abstract suspend fun upsertSeedInfo(seedInfo: AyahSeedInfoEntity)
}
