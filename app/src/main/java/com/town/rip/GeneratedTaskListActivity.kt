package com.town.rip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class GeneratedTaskListActivity : AppCompatActivity() {
    private lateinit var taskViewModel :TaskViewModel
    private var tasksList: List<Task> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        setContentView(R.layout.activity_generated_task_list)

        taskViewModel.allTasks.observe(this, Observer {
            if(!taskViewModel.allTasks.value.isNullOrEmpty()) {
                tasksList = taskViewModel.allTasks.value!!
                for (task in taskViewModel.allTasks.value!!) addTaskToNewGeneratedList(task)
            }
        })

    }

    private fun addTaskToNewGeneratedList(task:Task) {
        if((1..7).random() <= task.freq) Log.d("New Random Task", task.name.toString())
    }
}