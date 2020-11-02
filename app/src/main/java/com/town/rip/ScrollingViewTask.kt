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
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.*
import kotlinx.android.synthetic.main.dynamic_linear_layout_task.view.*


class ScrollingViewTask : AppCompatActivity() {
    private lateinit var taskViewModel :TaskViewModel
    private var tasksList: List<Task> = listOf()

    private var backgroundTint : Boolean = false;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        setContentView(R.layout.activity_scrolling_view_tasks)
        taskViewModel.allTasks.observe(this, Observer {
            loadTasks()
        })

    }

    private fun loadTasks() {
        Log.d("LISTSIZEVM", taskViewModel.allTasks.value?.toString())
        vertical_layout_view_1.removeAllViews()
        taskViewModel.allTasks.removeObservers(this)
        if(!taskViewModel.allTasks.value.isNullOrEmpty()) {
            tasksList = taskViewModel.allTasks.value!!
            for (task in taskViewModel.allTasks.value!!) addTask()
        }
        taskViewModel.allTasks.observe(this, Observer {
            if(tasksList.size != taskViewModel.allTasks.value!!.size)
            {
                vertical_layout_view_1.removeAllViews()
                tasksList = taskViewModel.allTasks.value!!
                for (task in taskViewModel.allTasks.value!!) addTask()
            }else {
                tasksList = taskViewModel.allTasks.value!!
                for (task in tasksList) Log.d("LISTSVM", task.name.toString())
                val layout = findViewById<View>(R.id.vertical_layout_view_1) as LinearLayout
                for ((i, view) in layout.children.withIndex()) {
                    val task = tasksList[i]
                    view.textViewDynamicTaskName.text = task.name
                    view.textViewDynamicTaskDescription.text = task.description
                    view.textViewDynamicTaskMagnitude.text = task.minimum.toString() + " to " + task.maximum.toString() + " " + task.unit_of_measurement + " per activity"
                    view.textViewDynamicTaskFrequency.text = task.freq.toString() + " days a week"
                    view.checkBoxDynamicTaskEnabled.isChecked = task.enabled
                    view.checkBoxDynamicTaskEnabled.setOnClickListener {
                        task.enabled = view.checkBoxDynamicTaskEnabled.isChecked
                        taskViewModel.update(task)
                    }
                    view.buttonInfo.setOnClickListener {
                        viewTask(view)
                    }
                    view.buttonScrollingViewInfoEdit.setOnClickListener {
                        startActivity(launchEditScreen(this, task))
                    }
                }
            }
        })

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
        intent.putExtras(bundle)
        return intent
    }
    private fun viewTask(view: View) {
        if (view.linearLayoutInfo.visibility == View.VISIBLE) view.linearLayoutInfo.visibility = View.GONE
        else view.linearLayoutInfo.visibility = View.VISIBLE
    }

}
