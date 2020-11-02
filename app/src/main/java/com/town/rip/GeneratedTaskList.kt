package com.town.rip

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_task_list_table")
class GeneratedTaskList (
    var name: String,
    var description: String,
    var type: String,
    var unit_of_measurement: String,
    var creation_date: String,
    var amount_to_complete: Int,
    var task_list_id: Int,
    var task_id: Int,
    var amount_completed: Int = 0
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
