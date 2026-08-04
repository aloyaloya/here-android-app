package ru.aloyaloya.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.aloyaloya.database.HereDatabase
import ru.aloyaloya.database.dao.MemoryDao
import ru.aloyaloya.database.dao.MemoryMediaDao
import ru.aloyaloya.database.migration.MIGRATION_1_2
import javax.inject.Singleton

/**
 * Dagger-модуль базы данных.
 *
 * Предоставляет синглтон [HereDatabase] и DAO для доступа
 * к таблицам воспоминаний и медиафайлов.
 */
@Module
object DatabaseModule {

    /** Создаёт и предоставляет экземпляр [HereDatabase]. */
    @Provides
    @Singleton
    fun provideHereDatabase(context: Context): HereDatabase = Room
        .databaseBuilder(context, HereDatabase::class.java, "here.db")
        .addMigrations(MIGRATION_1_2)
        .build()

    /** Предоставляет [MemoryDao] из экземпляра базы данных. */
    @Provides
    @Singleton
    fun provideMemoryDao(db: HereDatabase): MemoryDao = db.memoryDao()

    /** Предоставляет [MemoryMediaDao] из экземпляра базы данных. */
    @Provides
    @Singleton
    fun provideMemoryMediaDao(db: HereDatabase): MemoryMediaDao = db.memoryMediaDao()
}
