package com.town.rip.database

import androidx.lifecycle.LiveData
import com.town.rip.database.GeneratedTask
import com.town.rip.database.GeneratedTaskListDao


// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class GeneratedTaskListRepository(private val generatedTaskListDao: GeneratedTaskListDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allGeneratedTaskLists: LiveData<List<GeneratedTask>> = generatedTaskListDao.getAllGeneratedTaskLists()

    suspend fun insert(generatedTask: GeneratedTask) {
        generatedTaskListDao.insert(generatedTask)
    }
    suspend fun update(generatedTask: GeneratedTask){
        generatedTaskListDao.update(generatedTask)
    }
    suspend fun delete(generatedTask: GeneratedTask){
        generatedTaskListDao.delete(generatedTask)
    }
}