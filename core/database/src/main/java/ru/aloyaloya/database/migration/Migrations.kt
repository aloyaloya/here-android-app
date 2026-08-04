package ru.aloyaloya.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Появилось время события отдельно от времени записи.
 *
 * Старым воспоминаниям время события проставляется равным времени создания:
 * ничего другого про них не известно.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE memories ADD COLUMN happenedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("UPDATE memories SET happenedAt = createdAt")
    }
}