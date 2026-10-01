package shibbir.me.alquranquotes.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val MIGRATION_TEST_DATABASE_NAME = "quran-migration-test.db"

private const val TABLE_NAMES_QUERY = "SELECT name FROM sqlite_master WHERE type = 'table'"

/** Checks [MigrationOneToTwo] against the schemas exported to `app/schemas`. */
@RunWith(AndroidJUnit4::class)
class QuranDatabaseMigrationTest {

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        QuranDatabase::class.java,
    )

    /** Validation fails when any table, column, or index differs from `2.json`. */
    @Test
    fun migrationFromOneToTwoCreatesTheQuoteTablesAndClearsTheSeedVersion() {
        val versionOneDatabase = migrationTestHelper.createDatabase(MIGRATION_TEST_DATABASE_NAME, 1)
        insertVersionOneAyah(versionOneDatabase)
        insertVersionOneSeedVersion(versionOneDatabase)
        versionOneDatabase.close()

        val versionTwoDatabase = migrationTestHelper.runMigrationsAndValidate(
            MIGRATION_TEST_DATABASE_NAME,
            2,
            true,
            MigrationOneToTwo,
        )

        assertOnlyTheEmptyVersionTwoTablesRemain(versionTwoDatabase)
        versionTwoDatabase.close()
    }

    /** The ayahs table is gone, and nothing is seeded yet, so the next start seeds `quotes`. */
    private fun assertOnlyTheEmptyVersionTwoTablesRemain(database: SupportSQLiteDatabase) {
        val tableNames = queryTexts(database, TABLE_NAMES_QUERY)
        assertFalse("ayahs" in tableNames)
        assertTrue("quotes" in tableNames)
        assertTrue("seeded_bundled_keys" in tableNames)
        assertEquals("0", countRows(database, tableName = "quotes"))
        assertEquals("0", countRows(database, tableName = "seeded_bundled_keys"))
        assertEquals("0", countRows(database, tableName = "ayah_seed_info"))
    }

    private fun insertVersionOneAyah(database: SupportSQLiteDatabase) {
        val ayahValues = ContentValues()
        ayahValues.put("surah_number", 94)
        ayahValues.put("ayah_number", 5)
        ayahValues.put("surah_name_english", "Surah 94")
        ayahValues.put("surah_name_arabic", "surah-ar-94")
        ayahValues.put("arabic_text", "arabic-94-5")
        ayahValues.put("translation", "translation-94-5")
        database.insert("ayahs", SQLiteDatabase.CONFLICT_ABORT, ayahValues)
    }

    private fun insertVersionOneSeedVersion(database: SupportSQLiteDatabase) {
        val seedInfoValues = ContentValues()
        seedInfoValues.put("id", AyahSeedInfoEntity.SINGLE_ROW_ID)
        seedInfoValues.put("seed_version", 1)
        database.insert("ayah_seed_info", SQLiteDatabase.CONFLICT_ABORT, seedInfoValues)
    }

    private fun queryTexts(database: SupportSQLiteDatabase, sqlQuery: String): List<String> {
        val cursor = database.query(sqlQuery)
        return cursor.use { openCursor ->
            val texts = mutableListOf<String>()
            while (openCursor.moveToNext()) {
                texts += openCursor.getString(0)
            }
            texts
        }
    }

    private fun countRows(database: SupportSQLiteDatabase, tableName: String): String {
        val rowCounts = queryTexts(database, "SELECT COUNT(*) FROM `$tableName`")
        return rowCounts.single()
    }
}
