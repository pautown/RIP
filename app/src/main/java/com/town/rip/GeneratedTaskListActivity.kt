package com.town.rip

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.provider.AlarmClock
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.town.rip.database.GeneratedTask
import com.town.rip.database.GeneratedTaskListViewModel
import com.town.rip.database.Task
import com.town.rip.database.TaskViewModel
import kotlinx.android.synthetic.main.activity_generated_task_list.*
import kotlinx.android.synthetic.main.dynamic_generated_task.view.*
import java.text.SimpleDateFormat
import java.util.*


class GeneratedTaskListActivity : AppCompatActivity() {
    private var finished_generating_tasks: Boolean = false
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var generatedTaskViewModel : GeneratedTaskListViewModel
    private var session_task_list_id: Int = 0
    private var tasksList: List<GeneratedTask> = listOf()
    private var backgroundTint : Boolean = false;
    private var generatedTaskList: List<GeneratedTask> = listOf()
    private var listOfGeneratedTaskViews: MutableList<View> = mutableListOf()
    private var generatedTaskCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        generatedTaskViewModel = ViewModelProvider(this).get(GeneratedTaskListViewModel::class.java)

        setContentView(R.layout.activity_generated_task_list)





        generatedTaskViewModel.allTasks.observe(this, Observer {
           // /*
            //generatedTaskViewModel.deleteAll()
                tasksList = generatedTaskViewModel.allTasks.value!!
                if(tasksList.isNotEmpty()) session_task_list_id = tasksList.last().task_list_id.toInt() + 1
            Log.d("task list id", session_task_list_id.toString())

            if(tasksList.isEmpty() || tasksList.filter{ it.task_list_id == session_task_list_id - 1}.last().task_list_finished){ // generate new tasks if no unfinished tasks in last list
                generatedTaskViewModel.allTasks.removeObservers(this)
                generateNewTasks()
                }else{
                    Log.d("task list not empty", tasksList.filter{ it.task_list_id == session_task_list_id - 1}.last().task_list_finished.toString())
                    linear_layout_generated_session_init.visibility = View.VISIBLE
                    buttonContinueUnfinishedActivities.setOnClickListener{continueGeneratedTasks()}
                    buttonGenerateNewActivities.setOnClickListener{createNewGeneratedTasks()}

                    generatedTaskViewModel.allTasks.removeObservers(this)
                }
            // */
        })

        vertical_layout_view_generated_activities.setOnClickListener{
            for(generated_task_view in listOfGeneratedTaskViews)
            {
                generated_task_view.progressBarGeneratedTask.visibility = View.VISIBLE
                generated_task_view.seekBarGeneratedTask.visibility = View.GONE
            }
        }
    }


    private fun generateNewTasks() {
        taskViewModel.allTasks.observe(this, Observer {
            for (task in taskViewModel.allTasks.value!!) addGeneratedTaskToNewGeneratedList(task)
            finished_generating_tasks = true
        })
        generatedTaskViewModel.allTasks.removeObservers(this)

        generatedTaskViewModel.allTasks.observe(this, Observer {
            tasksList = generatedTaskViewModel.allTasks.value!!
            if(generatedTaskList.size != tasksList.filter{it.task_list_id == session_task_list_id}.size) {
                if(tasksList.filter { it.task_list_id == session_task_list_id }.size == generatedTaskCount && finished_generating_tasks){
                    generatedTaskList = tasksList.filter { it.task_list_id == session_task_list_id }
                    for(task in generatedTaskList) addTaskView(task)
                    updateHUDStats()
                    initHUDHistorical()
                }
            }
        })
        buttonGeneratedActivitiesFinish.visibility = View.VISIBLE
        linearLayoutHUD.visibility = View.VISIBLE

    }

    private fun continueGeneratedTasks(): View.OnClickListener? {
        linear_layout_generated_session_init.visibility = View.GONE
        session_task_list_id --
        generatedTaskList = tasksList.filter { it.task_list_id == session_task_list_id }
        for(task in generatedTaskList) addTaskView(task)
        buttonGeneratedActivitiesFinish.visibility = View.VISIBLE
        linearLayoutHUD.visibility = View.VISIBLE
        updateHUDStats()
        initHUDHistorical()
        return null
    }

    private fun initHUDHistorical() {
        var generatedTaskListPrior = tasksList.filter { it.task_list_id == session_task_list_id - 1}
        var totalTaskPercentage : Int = 0
        var totalTasksCompletedPercentage : Int

        if(generatedTaskListPrior.isNotEmpty()) { // get average of last session for display
            for (task in generatedTaskListPrior) {
                totalTaskPercentage += getTaskProgress(task)
            }
            totalTaskPercentage =
                ((totalTaskPercentage * 100.0f) / (generatedTaskListPrior.size * 100)).toInt()

            textViewGeneratedActivitiesHUDLast.text = "$totalTaskPercentage% last"
        }else  textViewGeneratedActivitiesHUDLast.text = ""

        if(tasksList.isNotEmpty()){ // get average of all sessions for display
            totalTaskPercentage = 0
            for (task in tasksList) {
                totalTaskPercentage += getTaskProgress(task)
            }
            totalTaskPercentage =
                ((totalTaskPercentage * 100.0f) / (tasksList.size * 100)).toInt()

            textViewGeneratedActivitiesHUDAvg.text = "$totalTaskPercentage% avg"
        }else textViewGeneratedActivitiesHUDAvg.text = ""
    }

    fun finishGeneratedTasks(view:View){
        for(task in generatedTaskList) {
            task.task_list_finished = true
            generatedTaskViewModel.update(task)
        }
        // TODO: Create view for finishing of tasks.
        finish()
    }

    private fun createNewGeneratedTasks(): View.OnClickListener?{
        for(task in tasksList.filter{ it.task_list_id == session_task_list_id - 1})
        {
            task.task_list_finished = true
            generatedTaskViewModel.update(task)
        }
        linear_layout_generated_session_init.visibility = View.GONE
        generateNewTasks()
        return null
    }

    private fun addGeneratedTaskToNewGeneratedList(task: Task) {
        if((1..7).random() <= task.freq && task.enabled){
            generatedTaskCount ++
            Log.d("New Random Task", task.name.toString())
            var random_task = GeneratedTask(
                task.name,
                task.description,
                task.type,
                task.unit_of_measurement,
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                (task.minimum..task.maximum).random(),
                session_task_list_id,
                task.id
            )
            putTask(random_task)
        }
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

    private fun addTaskView(task_generated: GeneratedTask){
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.dynamic_generated_task, null)
        val container = findViewById<LinearLayout>(R.id.vertical_layout_view_generated_activities)
        var sinceLastHaptic = 0
        container.addView(view)
        if(task_generated.type != "t") view.textViewTimer.visibility = View.GONE

        view.progressBarGeneratedTask.max = task_generated.amount_to_complete
        view.textViewGeneratedTaskDeskcription.text = task_generated.description
        view.textViewGeneratedTaskName.text = task_generated.name
        view.linear_layout_generated_task.isClickable = true
        view.seekBarGeneratedTask.visibility = View.GONE
        view.linear_layout_generated_task.setOnClickListener{
            if(view.progressBarGeneratedTask.visibility == View.VISIBLE) {
                for(generated_task_view in listOfGeneratedTaskViews)
                {
                    generated_task_view.textViewTimer.visibility = View.GONE
                    generated_task_view.progressBarGeneratedTask.visibility = View.VISIBLE
                    generated_task_view.seekBarGeneratedTask.visibility = View.GONE
                }
                view.progressBarGeneratedTask.visibility = View.GONE
                if(task_generated.type == "t") view.textViewTimer.visibility = View.VISIBLE
                view.seekBarGeneratedTask.visibility = View.VISIBLE

            }
            else{
                view.textViewTimer.visibility = View.GONE
                view.progressBarGeneratedTask.visibility = View.VISIBLE
                view.seekBarGeneratedTask.visibility = View.GONE
            }

        }

        if(task_generated.type == "t" && task_generated.amount_to_complete != task_generated.amount_completed)
            view.linear_layout_generated_task.setOnLongClickListener {

                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setMessage("Start ${(task_generated.amount_to_complete - task_generated.amount_completed)} minute timer for ${task_generated.name}?")
                    .setTitle("Timer?")
                    .setPositiveButton("Confirm", DialogInterface.OnClickListener { dialog, id ->
                        Toast.makeText(
                            this,
                            "${(task_generated.amount_to_complete - task_generated.amount_completed)} minute timer for ${task_generated.name} started ",
                            Toast.LENGTH_SHORT
                        ).show()
                        startTimer(
                            task_generated.name,
                            (task_generated.amount_to_complete - task_generated.amount_completed) * 60
                        )
                    })
                    .setNegativeButton("No",
                        DialogInterface.OnClickListener { dialog, id ->
                            // CANCEL
                        })

                val alert = builder.create()
                alert.show()



                true
            }

        listOfGeneratedTaskViews.add(view)
        view.textViewGeneratedTaskProgressUnitOfMeasure.text = task_generated.unit_of_measurement

        view.seekBarGeneratedTask.max = task_generated.amount_to_complete
        view.seekBarGeneratedTask.progress = task_generated.amount_completed
        view.progressBarGeneratedTask.progress = task_generated.amount_completed

        if(task_generated.amount_completed != 0) {
            view.textViewGeneratedTaskProgressPercentage.text = ((task_generated.amount_completed * 100.0f) / task_generated.amount_to_complete).toInt().toString() + "%"
            view.progressBarGeneratedTask.progress = task_generated.amount_completed
        }else{
            view.progressBarGeneratedTask.progress = 0
            view.textViewGeneratedTaskProgressPercentage.text = "0%"
        }


        view.textViewGeneratedTaskProgressText.text = task_generated.amount_completed.toString() + "/" + task_generated.amount_to_complete.toString()

        view.seekBarGeneratedTask.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                var performHapticBool = false
                if(view.seekBarGeneratedTask.progress > task_generated.amount_completed && view.seekBarGeneratedTask.max < 10) performHapticBool = true
                else if (view.seekBarGeneratedTask.progress > task_generated.amount_completed && view.seekBarGeneratedTask.progress >= sinceLastHaptic + view.seekBarGeneratedTask.max/7)
                {
                    performHapticBool = true
                    sinceLastHaptic = view.seekBarGeneratedTask.progress
                }
                if(view.seekBarGeneratedTask.progress <= sinceLastHaptic - view.seekBarGeneratedTask.max/7) sinceLastHaptic = view.seekBarGeneratedTask.progress
                if (performHapticBool) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

                task_generated.amount_completed = view.seekBarGeneratedTask.progress
                view.textViewGeneratedTaskProgressText.text = task_generated.amount_completed.toString() + "/" + task_generated.amount_to_complete.toString()
                view.progressBarGeneratedTask.progress = view.seekBarGeneratedTask.progress
                task_generated.amount_completed.toString() + "/" + task_generated.amount_to_complete.toString()
                view.textViewGeneratedTaskProgressPercentage.text = getTaskProgress(task_generated).toString() + "%"
                view.progressBarGeneratedTask.progress = task_generated.amount_completed

                updateHUDStats()
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                generatedTaskViewModel.update(task_generated)
            }
        })

        if (backgroundTint) view.linear_layout_generated_task.setBackgroundColor(Color.parseColor("#EEEEEE"))
        backgroundTint = !backgroundTint
    }

    private fun startTimer(message: String, seconds: Int) {
        "com.android.alarm.permission.SET_ALARM"
        val intent = Intent(AlarmClock.ACTION_SET_TIMER)

        val targetIntents: MutableList<Intent> = ArrayList()
        val packages =
            this.packageManager.queryIntentActivities(intent, 0)

        for (candidate in packages) {
            val packageName = candidate.activityInfo.packageName
            if (packageName != "com.town.rip") { //GooglePlus Package name
                val target = Intent(AlarmClock.ACTION_SET_TIMER)
                target.putExtra(AlarmClock.EXTRA_MESSAGE, message)
                target.putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                target.putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                target.setPackage(packageName)
                targetIntents.add(target)
            }
        }
        val chooserIntent = Intent.createChooser(targetIntents.removeAt(0), "Share.")
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            targetIntents.toTypedArray()
        )
        startActivity(chooserIntent)
    }


    private fun updateHUDStats() {
        var totalTaskPercentage : Int = 0
        var totalTimePercentage : Int
        var totalTasksCompletedPercentage : Int
        var totalTimeCompleted = 0
        var totalTimeToComplete = 0
        var totalTasksCompleted = 0
        var totalTasks = generatedTaskList.size

        for(task in generatedTaskList) {
            totalTaskPercentage += getTaskProgress(task)
            if(task.type == "t") {
                totalTimeToComplete += task.amount_to_complete
                totalTimeCompleted += task.amount_completed
            }
            if(task.amount_completed == task.amount_to_complete) totalTasksCompleted ++
        }
        totalTaskPercentage = ((totalTaskPercentage * 100.0f) / (generatedTaskList.size*100)).toInt()
        totalTimePercentage = ((totalTimeCompleted * 100.0f) / totalTimeToComplete).toInt()
        totalTasksCompletedPercentage = ((totalTasksCompleted * 100.0f) / generatedTaskList.size).toInt()

        progressBarGeneratedActivitiesHUDMinutes.progress = totalTimePercentage
        textViewGeneratedActivitiesHUDMinutes.text = "$totalTimeCompleted/$totalTimeToComplete minutes"
        progressBarGeneratedActivitiesHUDActivities.progress = totalTasksCompletedPercentage
        textViewGeneratedActivitiesHUDActivities.text = "$totalTasksCompleted/$totalTasks activities"
        progressBarGeneratedActivitiesHUDCompleted.progress = totalTaskPercentage
        textViewGeneratedActivitiesHUDCompleted.text = "$totalTaskPercentage% finished"

    }

    private fun getTaskProgress(task: GeneratedTask): Int {
        return if(task.amount_completed == 0) 0
        else ((task.amount_completed * 100.0f) / task.amount_to_complete).toInt()
    }

}