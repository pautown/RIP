package com.town.rip.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.town.rip.database.GeneratedTask


@Dao
interface GeneratedTaskListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(generatedTask: GeneratedTask): Long

    @Query("SELECT * from generated_task_list_table ORDER BY name ASC")
    fun getAlphabetizedWords():  LiveData<List<GeneratedTask>>

    @Query("SELECT * from generated_task_list_table")
    fun getAllGeneratedTaskLists(): LiveData<List<GeneratedTask>>

    @Insert
    fun addMultipleTasks(vararg generatedTask: GeneratedTask)

    @Query("DELETE FROM generated_task_list_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(generatedTask: GeneratedTask)

    @Update
    suspend fun update(generatedTask: GeneratedTask)

}