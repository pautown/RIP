package com.town.rip.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
class Task(
    var name: String,
    var description: String,
    var type: String,
    var unit_of_measurement: String,
    var minimum: Int,
    var maximum: Int,
    var freq: Int,
    var enabled: Boolean,
    var attempts:Int,
    var completions:Int,
    var total_attempted:Int,
    var total_completed: Int,
    var creation_date: String,
    var update_date: String,
    var profile_id: Int
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}