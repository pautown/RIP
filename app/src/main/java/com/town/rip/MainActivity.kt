package com.town.rip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider


class MainActivity : AppCompatActivity() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var tasksList: List<Task>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        setContentView(R.layout.activity_main)
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { taskViewModel.repository.allTasks }
        })

        val filename = "myfile"
        val fileContents = "Hello world!"
        openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun viewTasks(view: View) {
        val intent = Intent(this, ScrollingViewTask::class.java).apply {
           // putExtras(extras)
        }
        startActivity(intent)
    }

    fun generateTasks(view: View) {
        val intent = Intent(this, GeneratedTaskListActivity::class.java).apply {
            // putExtras(extras)
        }
        startActivity(intent)
    }

    fun addTask(view: View) {


        val intent = Intent(this, EditTask::class.java).apply {
            // putExtras(extras)
        }
        startActivity(intent)
    }


}
