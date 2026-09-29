package shibbir.me.alquranquotes.data.seed

/** Provides the bundled ayahs used to fill or refresh the database. */
interface AyahSeedSource {

    /** Main-safe: implementations move any blocking read or parsing off the caller's thread. */
    suspend fun load(): AyahSeed
}
