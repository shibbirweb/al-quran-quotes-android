package shibbir.me.alquranquotes.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import shibbir.me.alquranquotes.testing.readExportedSchemaIdentityHash

/**
 * Keeps every exported [QuranDatabase] schema fixed once it exists. When an entity changes but
 * the version does not, Room silently rewrites that version's schema file, and a device that
 * already has a database of that version crashes on open ("Room cannot verify the data
 * integrity"). Pinning each version's identity hash turns that into a failing test. It reads
 * `app/schemas` after Room's `copyRoomSchemas` task (see `app/build.gradle.kts`), so it checks
 * the schema of the entities as they are compiled now.
 *
 * After a schema change: bump the `QuranDatabase` version, add an `@AutoMigration` (or a
 * hand-written `Migration` when data moves), and pin the new version's hash below.
 */
class QuranDatabaseSchemaHistoryTest {

    @Test
    fun everyExportedSchemaKeepsItsPinnedIdentityHash() {
        pinnedIdentityHashes.forEach { (version, pinnedIdentityHash) ->
            val exportedIdentityHash = readExportedSchemaIdentityHash(version)
            assertNotNull("app/schemas has no schema for version $version", exportedIdentityHash)
            assertEquals(
                "The version $version schema changed. Revert the change to $version.json, then " +
                    "bump the QuranDatabase version and add a migration instead.",
                pinnedIdentityHash,
                exportedIdentityHash,
            )
        }
    }

    @Test
    fun noSchemaIsExportedPastTheLatestPinnedVersion() {
        val nextVersion = pinnedIdentityHashes.keys.max() + 1

        val nextIdentityHash = readExportedSchemaIdentityHash(nextVersion)

        assertNull(
            "Version $nextVersion was exported. Pin its identity hash in " +
                "QuranDatabaseSchemaHistoryTest and add its migration.",
            nextIdentityHash,
        )
    }

    private companion object {
        /** Each database version and the identity hash of its exported schema, oldest first. */
        val pinnedIdentityHashes = mapOf(
            1 to "640dba8f297e6d486786f16f736178ba",
            2 to "2c44e25c203a859475eee5ad5745f5d7",
        )
    }
}
