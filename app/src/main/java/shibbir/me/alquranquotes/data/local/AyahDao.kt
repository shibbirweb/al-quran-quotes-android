package shibbir.me.alquranquotes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AyahDao {

    @Query("SELECT COUNT(*) FROM ayahs")
    suspend fun count(): Int

    /** Returns the ayah at [position] when all ayahs are ordered by surah, then ayah number. */
    @Query("SELECT * FROM ayahs ORDER BY surah, ayah LIMIT 1 OFFSET :position")
    suspend fun getAt(position: Int): AyahEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(ayahs: List<AyahEntity>)
}
