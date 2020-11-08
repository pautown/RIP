package com.town.rip

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(GeneratedTask::class), version = 1, exportSchema = false)
abstract class GeneratedTaskListRoomDatabase : RoomDatabase() {

    abstract fun generatedTaskListDao(): GeneratedTaskListDao

    private class GeneratedTaskListDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.generatedTaskListDao())
                }
            }
        }
        suspend fun populateDatabase(generatedTaskListDao: GeneratedTaskListDao) {

        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: GeneratedTaskListRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): GeneratedTaskListRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GeneratedTaskListRoomDatabase::class.java,
                    "generated_task_list_database"
                ).addCallback(GeneratedTaskListDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}