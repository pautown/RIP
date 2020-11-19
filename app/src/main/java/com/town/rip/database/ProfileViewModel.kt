package com.town.rip.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.town.rip.database.GeneratedTask
import com.town.rip.database.GeneratedTaskListRepository
import com.town.rip.database.GeneratedTaskListRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val repository: ProfileRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    var allProfiles: LiveData<List<Profile>>

    init {
        val profileDao = ProfileRoomDatabase.getDatabase(application, viewModelScope).profileDao()

        repository = ProfileRepository(
            profileDao
        )
        allProfiles = repository.allProfiles

    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(profile: Profile) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(profile)
    }

    fun update(profile: Profile) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(profile)
    }

    fun delete(profile: Profile) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(profile)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }
}