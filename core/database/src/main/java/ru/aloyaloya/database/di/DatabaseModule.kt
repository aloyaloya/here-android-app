package ru.aloyaloya.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.aloyaloya.database.HereDatabase
import ru.aloyaloya.database.dao.MemoryDao
import ru.aloyaloya.database.dao.MemoryMediaDao
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideHereDatabase(context: Context): HereDatabase =
        Room.databaseBuilder(context, HereDatabase::class.java, "here.db").build()

    @Provides
    @Singleton
    fun provideMemoryDao(db: HereDatabase): MemoryDao = db.memoryDao()

    @Provides
    @Singleton
    fun provideMemoryMediaDao(db: HereDatabase): MemoryMediaDao = db.memoryMediaDao()
}
