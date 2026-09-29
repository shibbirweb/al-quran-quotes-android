package shibbir.me.alquranquotes.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import shibbir.me.alquranquotes.data.local.AyahDao
import shibbir.me.alquranquotes.data.local.QuranDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideQuranDatabase(@ApplicationContext context: Context): QuranDatabase =
        Room.databaseBuilder(context, QuranDatabase::class.java, "quran.db").build()

    @Provides
    fun provideAyahDao(database: QuranDatabase): AyahDao = database.ayahDao()
}
