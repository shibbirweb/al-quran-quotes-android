package shibbir.me.alquranquotes.data.local

import org.junit.Assert.assertEquals
import org.junit.Test
import shibbir.me.alquranquotes.testing.SqlRecordingDatabase

/**
 * The statements of [MigrationOneToTwo]. The device test QuranDatabaseMigrationTest checks them
 * against the exported schemas on a real database.
 */
class MigrationOneToTwoTest {

    @Test
    fun createsTheQuoteTablesDropsAyahsAndClearsTheSeedVersion() {
        val sqlRecordingDatabase = SqlRecordingDatabase()

        MigrationOneToTwo.migrate(sqlRecordingDatabase.database)

        val expectedSql = listOf(
            "CREATE TABLE IF NOT EXISTS `quotes` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `origin` TEXT NOT NULL, " +
                "`type` TEXT NOT NULL, `bundled_key` TEXT, `surah_name` TEXT, " +
                "`surah_number` INTEGER, `ayah_number` INTEGER, `arabic_text` TEXT, " +
                "`translation` TEXT, `free_text` TEXT, `reference` TEXT)",
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_quotes_bundled_key` " +
                "ON `quotes` (`bundled_key`)",
            "CREATE TABLE IF NOT EXISTS `seeded_bundled_keys` (" +
                "`bundled_key` TEXT NOT NULL, PRIMARY KEY(`bundled_key`))",
            "DROP TABLE IF EXISTS `ayahs`",
            "DELETE FROM `ayah_seed_info`",
        )
        assertEquals(expectedSql, sqlRecordingDatabase.executedSql)
        assertEquals(1, MigrationOneToTwo.startVersion)
        assertEquals(2, MigrationOneToTwo.endVersion)
    }
}
