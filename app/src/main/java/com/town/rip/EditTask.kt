package com.town.rip


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.town.rip.database.Profile
import com.town.rip.database.ProfileViewModel
import com.town.rip.database.Task
import com.town.rip.database.TaskViewModel
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class EditTask : AppCompatActivity() {
    private lateinit var taskIDList: MutableList<Int>
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var profileList: List<Profile> = listOf()

    private var editMode:Boolean = false

    lateinit var name: String
    lateinit var description: String
    lateinit var type: String
    var unit_of_measurement: String = "minutes"
    var minimum: Int = 0
    var maximum: Int= 0
    var freq: Int= 0
    var enabled: Boolean = false
     var attempts:Int = 0
    var completions:Int = 0
    var total_attempted:Int = 0
    var total_completed: Int = 0
    lateinit var creation_date: String
    lateinit var update_date: String
    var task_id:Int = 0

    var message_string = "Save the new activity and return to the previous screen?"
    var title_string = "Add Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentExtras = intent.extras
        if (intentExtras?.getInt("ID") != null) editMode = true

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        profileViewModel.allProfiles.observe(this, Observer {
            profileList = profileViewModel.allProfiles.value!!
            if(!editMode) taskIDList = listOf(profileList.last{ it.selected }.id).toMutableList()
        })
        setContentView(R.layout.activity_edit)
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { taskViewModel.repository.allTasks }
        })

        if(editMode)
        {
            message_string = "Modify the existing activity and return to the previous screen?"
            title_string = "Edit Activity"

            task_id = intentExtras!!.getInt("ID")
            textViewEditActivityHeader.text = "Edit Activity"
            textViewEditActivitySubeader.text = "Modify Existing Activity"
            name = intentExtras!!.getString("NAME").toString()
            description = intentExtras!!.getString("DESC").toString()
            type = intentExtras!!.getString("TYPE").toString()
            unit_of_measurement = intentExtras!!.getString("U_O_M").toString()
            minimum  = intentExtras!!.getInt("MIN")
            maximum = intentExtras!!.getInt("MAX")
            freq = intentExtras!!.getInt("FREQ")
            taskIDList = intentExtras!!.getIntegerArrayList("ID_LIST")!!.toMutableList()
            Log.d("taskIDList", taskIDList.first().toString())

            textInputName.setText(name)
            textInputDescription.setText(description)
            textInputUnitOfMeasurement.setText(unit_of_measurement)
            textInputMinUnit.setText(minimum.toString())
            textInputMaxUnit.setText(maximum.toString())
            textViewDaysPerWeek.setText(freq.toString())
            seekBar.progress = freq
            if(type == "r") {
                radioButtonNonMinutes.isChecked = true
                textInputUnitOfMeasurement.isEnabled = true
            }

        } else buttonActivityEditDelete.visibility =  View.GONE


       seekBar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                textViewDaysPerWeek.text = seekBar.progress.toString()
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
            }
        })


    }

    fun toggleMeasurementEditOn(view: View){
        textInputUnitOfMeasurement.isEnabled = true
        textInputUnitOfMeasurement.setText("")
        textInputUnitOfMeasurement.hint = "pages, essays, times, etc."
    }
    fun toggleMeasurementEditOff(view: View){
        textInputUnitOfMeasurement.isEnabled = false
        textInputUnitOfMeasurement.hint = "minutes"
        textInputUnitOfMeasurement.setText("minutes")
    }

    fun deleteTask(view:View){

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("This will permanently remove your activity from generation.")
            .setTitle(title_string)
            .setPositiveButton("Delete Activity?",
                DialogInterface.OnClickListener { dialog, id ->
                    taskViewModel.delete(taskViewModel.allTasks.value!!.find{ it.id == task_id}!!)
                    finish() // return to previous screen
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    // CANCEL
                })

        val alert = builder.create()
        alert.show()
    }

    fun cancelTask(view:View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Return to prior screen?")
            .setTitle(title_string)
            .setPositiveButton("Confirm",
                DialogInterface.OnClickListener { dialog, id ->
                    finish() // return to previous screen
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    // CANCEL
                })

        val alert = builder.create()
        alert.show()
    }
    

    fun createTask(view: View) {
        var taskType = "t"
        unit_of_measurement = "minutes"
        if (radioButtonNonMinutes.isChecked) {
            taskType = "r"
            unit_of_measurement = textInputUnitOfMeasurement.text.toString()
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(message_string)
            .setTitle(title_string)
            .setPositiveButton("Confirm",
                DialogInterface.OnClickListener { dialog, id ->
                    var task = Task(
                        textInputName.text.toString(),
                        textInputDescription.text.toString(),
                        taskType,
                        unit_of_measurement,
                        textInputMinUnit.text.toString().toInt(),
                        textInputMaxUnit.text.toString().toInt(),
                        textViewDaysPerWeek.text.toString().toInt(),
                        true,
                        0,
                        0,
                        0,
                        0,
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        profileList.last{ it.selected }.id,
                        taskIDList
                    )

                    if (editMode) {
                        task.id = task_id
                        taskViewModel.update(task)
                    }
                    else putTask(task)
                    finish() // return to previous screen
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    // CANCEL
                })

        val alert = builder.create()
        alert.show()
    }

    private fun putTask(task: Task) {
        class PutTask : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg params: Void?): Void? {
                taskViewModel.insert(task)
                return null
            }
        }
        PutTask().execute()
    }
}
