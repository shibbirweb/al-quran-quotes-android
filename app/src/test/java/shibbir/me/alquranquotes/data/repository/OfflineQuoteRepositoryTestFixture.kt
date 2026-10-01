package shibbir.me.alquranquotes.data.repository

import shibbir.me.alquranquotes.data.seed.BundledAyahSeeder
import shibbir.me.alquranquotes.data.seed.SampleAyahSeeds
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource
import shibbir.me.alquranquotes.testing.FakeBundledAyahSeedDao
import shibbir.me.alquranquotes.testing.FakeQuoteDao
import shibbir.me.alquranquotes.testing.FakeQuoteTables

/** Fakes and a builder shared by the [OfflineQuoteRepository] test classes. */
class OfflineQuoteRepositoryTestFixture(
    val quoteTables: FakeQuoteTables = FakeQuoteTables(),
    val ayahSeedSource: FakeAyahSeedSource = FakeAyahSeedSource(SampleAyahSeeds.firstAyahSeed),
) {

    fun createRepository(): OfflineQuoteRepository {
        val bundledAyahSeedDao = FakeBundledAyahSeedDao(quoteTables)
        return OfflineQuoteRepository(
            bundledAyahSeeder = BundledAyahSeeder(bundledAyahSeedDao, ayahSeedSource),
            quoteDao = FakeQuoteDao(quoteTables),
        )
    }
}
