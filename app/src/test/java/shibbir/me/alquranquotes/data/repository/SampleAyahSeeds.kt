package shibbir.me.alquranquotes.data.repository

import shibbir.me.alquranquotes.data.seed.AyahSeed
import shibbir.me.alquranquotes.testing.testAyah
import shibbir.me.alquranquotes.testing.testAyahEntity

/** Seeds and stored rows shared by the [OfflineAyahRepository] test classes. */
object SampleAyahSeeds {

    val seedAyahs = listOf(
        testAyah(surahNumber = 94, ayahNumber = 5),
        testAyah(surahNumber = 2, ayahNumber = 153),
        testAyah(surahNumber = 13, ayahNumber = 28),
    )

    /** The (surah, ayah) keys of [seedAyahs]. */
    val seedAyahKeys: Set<Pair<Int, Int>> = seedAyahs.mapTo(mutableSetOf()) { ayah ->
        ayah.surahNumber to ayah.ayahNumber
    }

    val firstAyahSeed = AyahSeed(version = 1, ayahs = seedAyahs)

    val oldAyahEntities = listOf(
        testAyahEntity(surahNumber = 1, ayahNumber = 1),
    )

    /** Fails to store because both ayahs share the key 94:5. */
    val duplicateKeyAyahSeed = AyahSeed(
        version = 2,
        ayahs = listOf(
            testAyah(surahNumber = 94, ayahNumber = 5),
            testAyah(surahNumber = 94, ayahNumber = 5, translation = "duplicate"),
        ),
    )
}
