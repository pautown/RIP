package com.town.rip

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
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
import com.town.rip.database.GeneratedTaskListViewModel
import com.town.rip.database.Task
import com.town.rip.database.TaskViewModel
import kotlinx.android.synthetic.main.activity_generated_task_list.*
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.*
import kotlinx.android.synthetic.main.dynamic_generated_task.view.*
import java.text.SimpleDateFormat
import java.util.*

class GeneratedTaskListActivity : AppCompatActivity() {
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var generatedTaskViewModel : GeneratedTaskListViewModel
    private var task_id: Int = 0
    private var generatedTasksList: List<GeneratedTask> = listOf()
    private var backgroundTint : Boolean = false;
    private var listOfGeneratedTasks: MutableList<GeneratedTask> = mutableListOf()
    private var listOfGeneratedTaskViews: MutableList<View> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        generatedTaskViewModel = ViewModelProvider(this).get(GeneratedTaskListViewModel::class.java)

        setContentView(R.layout.activity_generated_task_list)

        generatedTaskViewModel.allTasks.observe(this, Observer {
           // /*
                generatedTasksList = generatedTaskViewModel.allTasks.value!!
                if(generatedTasksList.isNotEmpty()) task_id = generatedTasksList.last().task_list_id.toInt() + 1
                Log.d("task list id final", task_id.toString())
                taskViewModel.allTasks.observe(this, Observer {
                    for (task in taskViewModel.allTasks.value!!) addTaskToNewGeneratedList(task)

                })
                generatedTaskViewModel.allTasks.removeObservers(this)
                generatedTaskViewModel.allTasks.observe(this, Observer {
                    generatedTasksList = generatedTaskViewModel.allTasks.value!!
                    for ((i, task) in listOfGeneratedTasks.withIndex()) {
                        task.id = generatedTasksList[(generatedTasksList.size) - listOfGeneratedTasks.size].id + i
                        Log.d("task id",  task.id.toString())
                    } // loop through newly created tasks and set id to match (for updating in db later)
                })

            // */
          //  generatedTaskViewModel.deleteAll()

        })



        vertical_layout_view_generated_activities.setOnClickListener{
            for(generated_task_view in listOfGeneratedTaskViews)
            {
                generated_task_view.progressBarGeneratedTask.visibility = View.VISIBLE
                generated_task_view.seekBarGeneratedTask.visibility = View.GONE
            }
        }




    }

    private fun createGeneratedTasks() {
        taskViewModel.allTasks.observe(this, Observer {
            if(!taskViewModel.allTasks.value.isNullOrEmpty()) {
                for (task in taskViewModel.allTasks.value!!) addTaskToNewGeneratedList(task)
            }
        })
    }
    private fun putTask(task: GeneratedTask) {
        class PutTask : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg params: Void?): Void? {
                generatedTaskViewModel.insert(task)

                return null
            }
        }
        PutTask().execute()
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
            task_id,
            task.id
        )


        listOfGeneratedTasks.add(task_generated)

        view.progressBarGeneratedTask.max = task_generated.amount_to_complete
        view.textViewGeneratedTaskDeskcription.text = task_generated.description
        view.textViewGeneratedTaskName.text = task_generated.name
        view.linear_layout_generated_task.isClickable = true
        view.seekBarGeneratedTask.visibility = View.GONE
        view.linear_layout_generated_task.setOnClickListener{
            if(view.progressBarGeneratedTask.visibility == View.VISIBLE) {
                for(generated_task_view in listOfGeneratedTaskViews)
                {
                    generated_task_view.progressBarGeneratedTask.visibility = View.VISIBLE
                    generated_task_view.seekBarGeneratedTask.visibility = View.GONE
                }
                view.progressBarGeneratedTask.visibility = View.GONE
                view.seekBarGeneratedTask.visibility = View.VISIBLE
            }
            else{
                view.progressBarGeneratedTask.visibility = View.VISIBLE
                view.seekBarGeneratedTask.visibility = View.GONE
            }

        }
        listOfGeneratedTaskViews.add(view)
        view.textViewGeneratedTaskProgressUnitOfMeasure.text = task_generated.unit_of_measurement

        view.seekBarGeneratedTask.max = task_generated.amount_to_complete
        view.seekBarGeneratedTask.progress = task_generated.amount_completed
        view.textViewGeneratedTaskProgressText.text = task_generated.amount_completed.toString() + "/" + task_generated.amount_to_complete.toString()

        view.seekBarGeneratedTask.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                task_generated.amount_completed = view.seekBarGeneratedTask.progress
                view.textViewGeneratedTaskProgressText.text = task_generated.amount_completed.toString() + "/" + task_generated.amount_to_complete.toString()
                view.progressBarGeneratedTask.progress = view.seekBarGeneratedTask.progress
                task_generated.amount_completed.toString() + "/" + task_generated.amount_to_complete.toString()
                if(task_generated.amount_completed != 0) {
                    view.textViewGeneratedTaskProgressPercentage.text = ((task_generated.amount_completed * 100.0f) / task_generated.amount_to_complete).toInt().toString() + "%"
                    view.progressBarGeneratedTask.progress = task_generated.amount_completed
                }
                else{
                    view.progressBarGeneratedTask.progress = 0
                    view.textViewGeneratedTaskProgressPercentage.text = "0%"
                }
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                generatedTaskViewModel.update(task_generated)
                Log.d("task modified id",task_generated.id.toString())
                Log.d("task modified completed",task_generated.amount_completed.toString())
                Log.d("task progress modified",task_generated.name)

            }
        })
        putTask(task_generated)
        if (backgroundTint) view.linear_layout_generated_task.setBackgroundColor(Color.parseColor("#EEEEEE"))
        backgroundTint = !backgroundTint
    }


    private fun addTaskToNewGeneratedList(task: Task) {
        if((1..7).random() <= task.freq){
            addTask(task)
            Log.d("New Random Task", task.name.toString())
        }
    }
}