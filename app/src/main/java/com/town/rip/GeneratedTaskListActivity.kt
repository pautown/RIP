package com.town.rip

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.town.rip.database.GeneratedTask
import com.town.rip.database.Task
import com.town.rip.database.TaskViewModel
import kotlinx.android.synthetic.main.activity_generated_task_list.*
import kotlinx.android.synthetic.main.dynamic_generated_task.view.*
import java.text.SimpleDateFormat
import java.util.*

class GeneratedTaskListActivity : AppCompatActivity() {
    private lateinit var taskViewModel : TaskViewModel
    private var tasksList: List<Task> = listOf()
    private var backgroundTint : Boolean = false;
    private var listOfGeneratedTasks: MutableList<GeneratedTask> = mutableListOf()
    private var listOfGeneratedTaskViews: MutableList<View> = mutableListOf()

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

    private fun addTask(task: Task){
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.dynamic_generated_task, null)
        val container = findViewById<LinearLayout>(R.id.vertical_layout_view_generated_activities)
        container.addView(view)
        var task_generated = GeneratedTask(
            task.name,
            task.description,
            task.type,
            task.unit_of_measurement,
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            (task.minimum..task.maximum).random(),
            0,
            task.id
        )

        listOfGeneratedTasks.add(task_generated)
        listOfGeneratedTaskViews.add(view)
        view.progressBarGeneratedTask.max = task_generated.amount_to_complete
        view.textViewGeneratedTaskName.text = task_generated.name
        view.linear_layout_generated_task.isClickable = true
        var view_index = listOfGeneratedTasks.size - 1
        view.linear_layout_generated_task.setOnClickListener{updateHUD(view_index)}

        updateGeneratedTaskDisplay(view_index)


        if (backgroundTint) view.linear_layout_generated_task.setBackgroundColor(Color.parseColor("#EEEEEE"))
        backgroundTint = !backgroundTint
    }

    private fun updateHUD(i: Int): View.OnClickListener? {
        if (textViewGeneratedTaskListPlaceholder.visibility == View.VISIBLE){
            textViewGeneratedTaskListPlaceholder.visibility = View.GONE
            linearLayoutGeneratedTasksHUD.visibility = View.VISIBLE
        }

        var task_generated = listOfGeneratedTasks[i]
        textViewGeneratedTaskListHUDName.text = task_generated.name
        textViewGeneratedTaskListHUDDescription.text = task_generated.description
        textViewGeneratedTaskListHUDUOM.text = task_generated.unit_of_measurement + " completed"
        textViewGeneratedTaskListHUDCompleted.text = task_generated.amount_completed.toString() + "/" + task_generated.amount_to_complete.toString()

        seekBarGeneratedTaskList.setOnSeekBarChangeListener(null)
        seekBarGeneratedTaskList.max = task_generated.amount_to_complete
        seekBarGeneratedTaskList.progress = task_generated.amount_completed

        seekBarGeneratedTaskList.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                updateHUDSEEKBAR(i)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped

            }
        })


        return null
    }

    private fun updateHUDSEEKBAR(i: Int): SeekBar.OnSeekBarChangeListener? {
        var task_generated = listOfGeneratedTasks[i]
        var view = listOfGeneratedTaskViews[i]
        task_generated.amount_completed = seekBarGeneratedTaskList.progress
        textViewGeneratedTaskListHUDCompleted.text = task_generated.amount_completed.toString() + "/" + task_generated.amount_to_complete.toString()
        updateGeneratedTaskDisplay(i)
        return null
    }

    private fun updateGeneratedTaskDisplay(i: Int) {
        Log.d("task click", i.toString())
        var view = listOfGeneratedTaskViews[i]
        var task_generated = listOfGeneratedTasks[i]

        view.textViewGeneratedTaskProgressUnitOfMeasure.text = task_generated.unit_of_measurement
        view.textViewGeneratedTaskProgressText.text = task_generated.amount_completed.toString() + "/" + task_generated.amount_to_complete.toString()

        if(task_generated.amount_completed != 0) {
            view.textViewGeneratedTaskProgressPercentage.text = ((task_generated.amount_completed * 100.0f) / task_generated.amount_to_complete).toInt().toString() + "%"
            view.progressBarGeneratedTask.progress = task_generated.amount_completed
        }
        else{
            view.progressBarGeneratedTask.progress = 0
            view.textViewGeneratedTaskProgressPercentage.text = "0%"
        }
    }

    private fun addTaskToNewGeneratedList(task: Task) {
        if((1..7).random() <= task.freq){
            addTask(task)
            Log.d("New Random Task", task.name.toString())
        }
    }
}