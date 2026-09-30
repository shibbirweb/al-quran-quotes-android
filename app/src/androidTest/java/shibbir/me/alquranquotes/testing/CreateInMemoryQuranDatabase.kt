package shibbir.me.alquranquotes.testing

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import shibbir.me.alquranquotes.data.local.QuranDatabase

/** A fresh, empty in-memory [QuranDatabase] for one device test. */
fun createInMemoryQuranDatabase(): QuranDatabase {
    val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
    val databaseBuilder = Room.inMemoryDatabaseBuilder(targetContext, QuranDatabase::class.java)
    return databaseBuilder.build()
}
