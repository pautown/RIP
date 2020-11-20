package com.town.rip

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.town.rip.database.Profile
import com.town.rip.database.ProfileViewModel
import com.town.rip.database.Task
import com.town.rip.database.TaskViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var tasksList: List<Task>
    private var profileList: List<Profile> = listOf()
    private var mutableProfileList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        setContentView(R.layout.activity_main)
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { taskViewModel.repository.allTasks }
        })
        profileViewModel.allProfiles.observe(this, Observer {
            profileList = profileViewModel.allProfiles.value!!
            for(profile in profileList) mutableProfileList.add(profile.name)
            mutableProfileList.add("create new profile")
            var adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                applicationContext,
                android.R.layout.simple_spinner_item, mutableProfileList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

        })

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
