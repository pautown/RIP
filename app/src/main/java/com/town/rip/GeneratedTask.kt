package com.town.rip

import androidx.room.PrimaryKey

class GeneratedTask (
    var task_id: Int,
    var name: String,
    var description: String,
    var type: String,
    var unit_of_measurement: String,
    var amount_to_complete: Int,
    var amount_completed: Int = 0
)