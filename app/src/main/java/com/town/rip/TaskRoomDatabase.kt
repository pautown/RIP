package com.town.rip

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(Task::class), version = 1, exportSchema = false)
abstract class TaskRoomDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    private class TaskDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.taskDao())
                }
            }
        }
        suspend fun populateDatabase(taskDao: TaskDao) {

        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TaskRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): TaskRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskRoomDatabase::class.java,
                    "task_database"
                ).addCallback(TaskDatabaseCallback(scope))
                 .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}