package shibbir.me.alquranquotes.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import shibbir.me.alquranquotes.data.local.BundledAyahSeedDao
import shibbir.me.alquranquotes.data.local.DailyQuoteDao
import shibbir.me.alquranquotes.data.local.MigrationOneToTwo
import shibbir.me.alquranquotes.data.local.QuoteDao
import shibbir.me.alquranquotes.data.local.QuranDatabase
import javax.inject.Singleton

/** File name of the app database; tests use it to delete the database between runs. */
internal const val QURAN_DATABASE_NAME = "quran.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideQuranDatabase(@ApplicationContext applicationContext: Context): QuranDatabase {
        val databaseBuilder = Room.databaseBuilder(
            applicationContext,
            QuranDatabase::class.java,
            QURAN_DATABASE_NAME,
        )
        databaseBuilder.addMigrations(MigrationOneToTwo)
        return databaseBuilder.build()
    }

    @Provides
    fun provideQuoteDao(quranDatabase: QuranDatabase): QuoteDao = quranDatabase.quoteDao()

    @Provides
    fun provideBundledAyahSeedDao(quranDatabase: QuranDatabase): BundledAyahSeedDao {
        return quranDatabase.bundledAyahSeedDao()
    }

    @Provides
    fun provideDailyQuoteDao(quranDatabase: QuranDatabase): DailyQuoteDao {
        return quranDatabase.dailyQuoteDao()
    }
}
