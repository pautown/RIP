package com.town.rip

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeneratedTaskListViewModel(application: Application) : AndroidViewModel(application) {
    val repository: GeneratedTaskListRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    var allTasks: LiveData<List<GeneratedTask>>

    init {
        val generatedTaskListsDao = GeneratedTaskListRoomDatabase.getDatabase(application, viewModelScope).generatedTaskListDao()

        repository = GeneratedTaskListRepository(generatedTaskListsDao)
        allTasks = repository.allGeneratedTaskLists

    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(generatedTask: GeneratedTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(generatedTask)
    }

    fun update(generatedTask: GeneratedTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(generatedTask)
    }

    fun delete(generatedTask: GeneratedTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(generatedTask)
    }
}