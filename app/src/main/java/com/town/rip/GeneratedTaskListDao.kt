package com.town.rip

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface GeneratedTaskListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(generatedTaskList: GeneratedTaskList)

    @Query("SELECT * from generated_task_list_table ORDER BY name ASC")
    fun getAlphabetizedWords():  LiveData<List<GeneratedTaskList>>

    @Query("SELECT * from generated_task_list_table")
    fun getAllGeneratedTaskLists(): LiveData<List<GeneratedTaskList>>

    @Insert
    fun addMultipleTasks(vararg generatedTaskList: GeneratedTaskList)

    @Query("DELETE FROM generated_task_list_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(generatedTaskList: GeneratedTaskList)

    @Update
    suspend fun update(generatedTaskList: GeneratedTaskList)

}