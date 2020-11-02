package com.town.rip

import androidx.lifecycle.LiveData


// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class GeneratedTaskListRepository(private val generatedTaskListDao: GeneratedTaskListDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allGeneratedTaskLists: LiveData<List<GeneratedTaskList>> = generatedTaskListDao.getAllGeneratedTaskLists()

    suspend fun insert(generatedTaskList: GeneratedTaskList) {
        generatedTaskListDao.insert(generatedTaskList)
    }
    suspend fun update(generatedTaskList: GeneratedTaskList){
        generatedTaskListDao.update(generatedTaskList)
    }
    suspend fun delete(generatedTaskList: GeneratedTaskList){
        generatedTaskListDao.delete(generatedTaskList)
    }
}