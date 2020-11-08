package com.town.rip.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.town.rip.database.Task


@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Query("SELECT * from task_table ORDER BY name ASC")
    fun getAlphabetizedWords():  LiveData<List<Task>>

    @Query("SELECT * from task_table")
    fun getAllTasks(): LiveData<List<Task>>

    @Insert
    fun addMultipleTasks(vararg task: Task)

    @Query("DELETE FROM task_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

}