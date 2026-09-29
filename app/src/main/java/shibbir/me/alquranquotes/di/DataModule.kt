package shibbir.me.alquranquotes.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import shibbir.me.alquranquotes.core.time.EpochDayProvider
import shibbir.me.alquranquotes.core.time.SystemEpochDayProvider
import shibbir.me.alquranquotes.data.repository.AyahRepository
import shibbir.me.alquranquotes.data.repository.OfflineAyahRepository
import shibbir.me.alquranquotes.data.seed.AssetAyahSeedSource
import shibbir.me.alquranquotes.data.seed.AyahSeedSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAyahRepository(repository: OfflineAyahRepository): AyahRepository

    @Binds
    abstract fun bindAyahSeedSource(seedSource: AssetAyahSeedSource): AyahSeedSource

    @Binds
    abstract fun bindEpochDayProvider(provider: SystemEpochDayProvider): EpochDayProvider
}
