package shibbir.me.alquranquotes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AyahEntity::class,
        AyahSeedInfoEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun ayahDao(): AyahDao
}
