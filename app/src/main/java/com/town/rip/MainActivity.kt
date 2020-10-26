package com.town.rip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filename = "myfile"
        val fileContents = "Hello world!"
        openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun viewTasks(view: View) {


        val intent = Intent(this, ScrollingViewTasks::class.java).apply {
           // putExtras(extras)
        }
        startActivity(intent)
    }

    fun addTask(view: View) {


        val intent = Intent(this, AddTask::class.java).apply {
            // putExtras(extras)
        }
        startActivity(intent)
    }


}
