package shibbir.me.alquranquotes.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import shibbir.me.alquranquotes.data.repository.DailyQuoteRepository
import shibbir.me.alquranquotes.data.repository.OfflineDailyQuoteRepository
import shibbir.me.alquranquotes.data.repository.OfflineQuoteRepository
import shibbir.me.alquranquotes.data.repository.QuoteRepository
import shibbir.me.alquranquotes.data.seed.AssetAyahSeedSource
import shibbir.me.alquranquotes.data.seed.AyahSeedSource

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindDailyQuoteRepository(
        offlineDailyQuoteRepository: OfflineDailyQuoteRepository,
    ): DailyQuoteRepository

    @Binds
    abstract fun bindQuoteRepository(
        offlineQuoteRepository: OfflineQuoteRepository,
    ): QuoteRepository

    @Binds
    abstract fun bindAyahSeedSource(assetAyahSeedSource: AssetAyahSeedSource): AyahSeedSource
}
