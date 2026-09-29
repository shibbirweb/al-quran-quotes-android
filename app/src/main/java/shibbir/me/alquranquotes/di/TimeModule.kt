package shibbir.me.alquranquotes.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import shibbir.me.alquranquotes.core.time.DayChangeSource
import shibbir.me.alquranquotes.core.time.EpochDayProvider
import shibbir.me.alquranquotes.core.time.SystemDayChangeSource
import shibbir.me.alquranquotes.core.time.SystemEpochDayProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {

    @Binds
    abstract fun bindEpochDayProvider(
        systemEpochDayProvider: SystemEpochDayProvider,
    ): EpochDayProvider

    @Binds
    abstract fun bindDayChangeSource(
        systemDayChangeSource: SystemDayChangeSource,
    ): DayChangeSource
}
