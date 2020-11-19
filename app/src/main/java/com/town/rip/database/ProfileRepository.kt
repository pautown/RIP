package com.town.rip.database

import androidx.lifecycle.LiveData
import com.town.rip.database.GeneratedTask
import com.town.rip.database.GeneratedTaskListDao


// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ProfileRepository(private val profileDao: ProfileDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allProfiles: LiveData<List<Profile>> = profileDao.getAllProfiles()

    suspend fun insert(profile: Profile): Long {
        return profileDao.insert(profile)
    }
    suspend fun update(profile: Profile){
        profileDao.update(profile)
    }
    suspend fun delete(profile: Profile){
        profileDao.delete(profile)
    }

    suspend fun deleteAll() {
        profileDao.deleteAll()
    }
}