package com.town.rip

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.town.rip.database.ProfileViewModel
import com.town.rip.database.Task
import com.town.rip.database.TaskViewModel
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.*
import kotlinx.android.synthetic.main.dynamic_linear_layout_task.view.*
import kotlinx.android.synthetic.main.dynamic_view_profile.view.*
import com.google.gson.Gson


class ScrollingViewTask : AppCompatActivity() {
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var profileViewModel : ProfileViewModel

    private var tasksList: List<Task> = listOf()

    private var tasksListViews: MutableList<View> = mutableListOf()

    private var backgroundTint : Boolean = false;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        setContentView(R.layout.activity_scrolling_view_tasks)
        profileViewModel.allProfiles.observe(this, Observer {tasks ->
                tasks?.let { profileViewModel.repository.allProfiles }
            Log.d("profiles", profileViewModel.allProfiles.value!!.size.toString())
            profileViewModel.allProfiles.removeObservers(this)
            taskViewModel.allTasks.observe(this, Observer {
                loadTasks()
            })
        })


    }

    private fun loadTasks() {
        Log.d("LISTSIZEVM", taskViewModel.allTasks.value?.toString())
        vertical_layout_view_1.removeAllViews()
        taskViewModel.allTasks.removeObservers(this)
        if(!taskViewModel.allTasks.value.isNullOrEmpty()) {
            tasksList = taskViewModel.allTasks.value!!
            var enabledTasks = 0
            for (task in taskViewModel.allTasks.value!!) {
                if (task.enabled) enabledTasks++
                addTask()
                updateSubheading(taskViewModel.allTasks.value!!.size, enabledTasks)
            }
            updateTaskDisplays()
        }else textViewSubheading.text = "No activities created, create some!"
        taskViewModel.allTasks.observe(this, Observer {
            var enabledTasks = 0
            if(tasksList.size != taskViewModel.allTasks.value!!.size)
            {
                vertical_layout_view_1.removeAllViews()
                tasksList = taskViewModel.allTasks.value!!
                for (task in tasksList) {
                    if (task.enabled) enabledTasks++
                    addTask()
                }
                updateSubheading(tasksList.size,enabledTasks)
                updateTaskDisplays()
            }else if(!viewEnabledEqualsRepositoryEnabled()){
                tasksList = taskViewModel.allTasks.value!!
                enabledTasks = 0
                for(task in tasksList) if (task.enabled) enabledTasks++
                updateSubheading(tasksList.size,enabledTasks)
                for (task in tasksList) Log.d("LISTSVM", task.name.toString())
                updateTaskDisplays()
            }
        })

    }

    private fun viewEnabledEqualsRepositoryEnabled(): Boolean {
        var returnBool = true
        for ((i, task) in tasksList.withIndex())
            if(tasksList[i].enabled !=  taskViewModel.allTasks.value!![i].enabled)
                returnBool = false
        return  returnBool
    }

    private fun updateSubheading(totalTasks:Int , enabledTasks: Int) {
        textViewSubheading.text = "hold activity to toggle enable/disable\n$totalTasks total activities, $enabledTasks enabled"
    }

    private fun updateTaskDisplays() {
        val layout = findViewById<View>(R.id.vertical_layout_view_1) as LinearLayout
        for ((i, view) in layout.children.withIndex()) {
            val task = tasksList[i]
            view.textViewDynamicTaskName.text = task.name
            view.textViewDynamicTaskDescription.text = task.description
            view.textViewDynamicTaskMagnitude.text = "${task.minimum} to ${task.maximum} ${task.unit_of_measurement} per activity"
            view.textViewDynamicTaskFrequency.text = "${task.freq} days a week"
            if(task.enabled) view.textViewEnabled.text = "(enabled)"
            else view.textViewEnabled.text = "(disabled)"
            view.dynamic_linear_layout_base_task.setOnLongClickListener {
                task.enabled = !task.enabled
                if(task.enabled) view.textViewEnabled.text = "(enabled)"
                else view.textViewEnabled.text = "(disabled)"
                taskViewModel.update(task)
                true
            }

            view.dynamic_linear_layout_base_task.setOnClickListener {
                viewTask(view)
            }
            view.textViewEnabled.setOnClickListener{
                viewTask(view)
            }
            view.buttonScrollingViewInfoEdit.setOnClickListener {
                startActivity(launchEditScreen(this, task))
            }

            tasksListViews.add(view)
            for(profile in profileViewModel.allProfiles.value!!)
            {

                val inflaterProfile = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val viewProfile: View = inflaterProfile.inflate(R.layout.dynamic_view_profile, null)
                val container = view.layoutProfiles
                container.addView(viewProfile)

                viewProfile.checkBox.text = profile.name
                viewProfile.checkBox.isChecked = task.profile_ids.contains(profile.id)
                viewProfile.checkBox.isEnabled = profile.id != task.profile_id
                viewProfile.checkBox.setOnClickListener{
                    var profile_ids_temp = task.profile_ids.toMutableList()
                    if(viewProfile.checkBox.isChecked && !task.profile_ids.contains(profile.id))
                        profile_ids_temp.add(profile.id)
                    else if (!viewProfile.checkBox.isChecked && task.profile_ids.contains(profile.id)){
                        for ((i, id) in profile_ids_temp.withIndex()) {
                            if (id == profile.id) profile_ids_temp.removeAt(i)
                            break
                        }
                    }
                    task.profile_ids = profile_ids_temp
                    taskViewModel.update(task)
                }
            }

        }
    }

    private fun addTask(){
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.dynamic_linear_layout_task, null)
        val container = findViewById<LinearLayout>(R.id.vertical_layout_view_1)
        container.addView(view)
        if (backgroundTint) view.dynamic_linear_layout_base_task.setBackgroundColor(Color.parseColor("#EEEEEE"))
        backgroundTint = !backgroundTint
    }

    fun launchEditScreen(context: Context, task: Task): Intent {
        val intent = Intent(context, EditTask::class.java)
        val bundle = Bundle()

        bundle.putSerializable("ID", task.id)
        bundle.putSerializable("NAME", task.name)
        bundle.putSerializable("DESC", task.description)
        bundle.putSerializable("TYPE", task.type)
        bundle.putSerializable("U_O_M", task.unit_of_measurement)
        bundle.putSerializable("MIN", task.minimum)
        bundle.putSerializable("MAX", task.maximum)
        bundle.putSerializable("FREQ", task.freq)
        bundle.putSerializable("ATTEMPTS", task.attempts)
        bundle.putSerializable("COMPLETIONS", task.completions)
        bundle.putSerializable("TOTAL_ATTEMPTED", task.total_attempted)
        bundle.putSerializable("TOTAL_COMPLETED", task.total_completed)
        bundle.putSerializable("CREATION_DATE", task.creation_date)
        bundle.putSerializable("UPDATE_DATE", task.update_date)
        bundle.putIntegerArrayList("ID_LIST", ArrayList(task.profile_ids))
        intent.putExtras(bundle)
        return intent
    }
    private fun viewTask(view: View) {
        for(viewList in tasksListViews) if(viewList != view) viewList.linearLayoutInfo.visibility = View.GONE
        if (view.linearLayoutInfo.visibility == View.VISIBLE) view.linearLayoutInfo.visibility = View.GONE
        else view.linearLayoutInfo.visibility = View.VISIBLE
    }

}
