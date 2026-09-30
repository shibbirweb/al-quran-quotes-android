package shibbir.me.alquranquotes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Version 2 keeps every quote, bundled or the user's own, in `quotes`, records every bundled key
 * a seed stored in `seeded_bundled_keys`, and replaced the version 1 `ayahs` table (see
 * [MigrationOneToTwo], registered on the database builder). Every schema change needs a version
 * bump and a migration tested against the exported schemas in `app/schemas`, because the
 * database holds the user's quotes and edits.
 */
@Database(
    entities = [
        QuoteEntity::class,
        SeededBundledKeyEntity::class,
        AyahSeedInfoEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao

    abstract fun bundledAyahSeedDao(): BundledAyahSeedDao

    abstract fun dailyQuoteDao(): DailyQuoteDao
}
