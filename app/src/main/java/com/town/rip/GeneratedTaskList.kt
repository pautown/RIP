package com.town.rip

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_task_list_table")
class GeneratedTaskList (
    var generated_tasks:List<GeneratedTask>,
    var creation_date: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}