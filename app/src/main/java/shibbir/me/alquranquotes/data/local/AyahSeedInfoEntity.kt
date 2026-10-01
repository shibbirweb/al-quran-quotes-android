package shibbir.me.alquranquotes.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Records which version of the bundled ayah seed was last merged into the `quotes` table. The
 * table only ever holds one row, with id [SINGLE_ROW_ID].
 */
@Entity(tableName = "ayah_seed_info")
data class AyahSeedInfoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = SINGLE_ROW_ID,
    @ColumnInfo(name = "seed_version")
    val seedVersion: Int,
) {
    companion object {
        const val SINGLE_ROW_ID = 1
    }
}
