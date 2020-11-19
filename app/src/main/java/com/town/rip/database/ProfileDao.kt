package com.town.rip.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(profile: Profile): Long

    @Query("SELECT * from profile_table ORDER BY name ASC")
    fun getAlphabetizedProfiles():  LiveData<List<Profile>>

    @Query("SELECT * from profile_table")
    fun getAllProfiles(): LiveData<List<Profile>>

    @Insert
    fun addMultipleProfiles(vararg profile: Profile)

    @Query("DELETE FROM profile_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(profile: Profile)

    @Update
    suspend fun update(profile: Profile)

}