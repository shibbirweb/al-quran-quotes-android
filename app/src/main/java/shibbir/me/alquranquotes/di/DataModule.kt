package shibbir.me.alquranquotes.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import shibbir.me.alquranquotes.data.repository.AyahRepository
import shibbir.me.alquranquotes.data.repository.OfflineAyahRepository
import shibbir.me.alquranquotes.data.seed.AssetAyahSeedSource
import shibbir.me.alquranquotes.data.seed.AyahSeedSource

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindAyahRepository(offlineAyahRepository: OfflineAyahRepository): AyahRepository

    @Binds
    abstract fun bindAyahSeedSource(assetAyahSeedSource: AssetAyahSeedSource): AyahSeedSource
}
