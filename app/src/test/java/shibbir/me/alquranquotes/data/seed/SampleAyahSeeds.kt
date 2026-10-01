package shibbir.me.alquranquotes.data.seed

import shibbir.me.alquranquotes.testing.testAyah

/** Seeds shared by the seeder and repository test classes. */
object SampleAyahSeeds {

    /** Deliberately not in surah order, so tests see the stored order is not the seed order. */
    val seedAyahs = listOf(
        testAyah(surahNumber = 94, ayahNumber = 5),
        testAyah(surahNumber = 2, ayahNumber = 153),
        testAyah(surahNumber = 13, ayahNumber = 28),
    )

    val firstAyahSeed = AyahSeed(version = 1, ayahs = seedAyahs)
}
