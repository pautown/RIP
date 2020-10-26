package com.town.rip

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
class Task (
    val name: String,
    val description: String,
    val type: String,
    val unit_of_measurement: String,
    val minimum: Int,
    val maximum: Int,
    val freq: Int
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}