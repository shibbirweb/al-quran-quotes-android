package shibbir.me.alquranquotes.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Moves version 1 (bundled ayahs only, in `ayahs`) to version 2 (every quote in `quotes`). The
 * version 1 rows are only re-seedable bundled data, so instead of copying them it drops `ayahs`
 * and clears the seed version: on the next start the seeder finds no stored version and seeds
 * every bundled ayah into `quotes`. The statements match `app/schemas/.../2.json`.
 */
object MigrationOneToTwo : Migration(1, 2) {

    // Keeps Room's parameter name `db`, as overrides must, so named arguments stay valid.
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `quotes` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `origin` TEXT NOT NULL, " +
                "`type` TEXT NOT NULL, `bundled_key` TEXT, `surah_name` TEXT, " +
                "`surah_number` INTEGER, `ayah_number` INTEGER, `arabic_text` TEXT, " +
                "`translation` TEXT, `free_text` TEXT, `reference` TEXT)",
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_quotes_bundled_key` " +
                "ON `quotes` (`bundled_key`)",
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `seeded_bundled_keys` (" +
                "`bundled_key` TEXT NOT NULL, PRIMARY KEY(`bundled_key`))",
        )
        db.execSQL("DROP TABLE IF EXISTS `ayahs`")
        db.execSQL("DELETE FROM `ayah_seed_info`")
    }
}
