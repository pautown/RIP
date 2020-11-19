package com.town.rip.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
class Profile (
    var name: String,
    var creation_date: String,
    var selected: Boolean
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
