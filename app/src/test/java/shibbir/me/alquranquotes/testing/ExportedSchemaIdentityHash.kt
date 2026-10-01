package shibbir.me.alquranquotes.testing

import java.io.File
import org.json.JSONObject
import shibbir.me.alquranquotes.data.local.QuranDatabase

/** The folder Room exports [QuranDatabase]'s schemas to, inside `app/schemas`. */
private val SCHEMA_FOLDER_NAME = QuranDatabase::class.java.name

/**
 * The identity hash Room recorded in the exported schema of database [version], or null when no
 * schema was exported for it. The Gradle test tasks pass the `app/schemas` path as the
 * `roomSchemaDirectory` system property, after Room has written the current schema there.
 */
fun readExportedSchemaIdentityHash(version: Int): String? {
    val roomSchemaDirectory = requireNotNull(System.getProperty("roomSchemaDirectory")) {
        "Run the unit tests through Gradle, which sets the roomSchemaDirectory property"
    }
    val schemaFile = File(roomSchemaDirectory, "$SCHEMA_FOLDER_NAME/$version.json")
    if (!schemaFile.exists()) {
        return null
    }
    val databaseJson = JSONObject(schemaFile.readText()).getJSONObject("database")
    return databaseJson.getString("identityHash")
}
