package shibbir.me.alquranquotes.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One bundled key, such as "94:5", that a seed has ever stored. A key stays recorded after the
 * user deletes its quote, so a newer seed never adds that bundled ayah back.
 */
@Entity(tableName = "seeded_bundled_keys")
data class SeededBundledKeyEntity(
    @PrimaryKey
    @ColumnInfo(name = "bundled_key")
    val bundledKey: String,
)
