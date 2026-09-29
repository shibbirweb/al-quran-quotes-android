package shibbir.me.alquranquotes.data.repository

import shibbir.me.alquranquotes.testing.FakeAyahDao
import shibbir.me.alquranquotes.testing.FakeAyahSeedSource

/** Builds an [OfflineAyahRepository] on top of the in-memory fakes. */
fun createOfflineAyahRepository(
    ayahDao: FakeAyahDao,
    ayahSeedSource: FakeAyahSeedSource,
) = OfflineAyahRepository(
    ayahDao = ayahDao,
    ayahSeedSource = ayahSeedSource,
)
