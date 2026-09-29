package shibbir.me.alquranquotes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AyahEntity::class], version = 1, exportSchema = false)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun ayahDao(): AyahDao
}
