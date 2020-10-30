package com.town.rip


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_edit.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class EditActivity : AppCompatActivity() {
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        setContentView(R.layout.activity_edit)
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { taskViewModel.repository.allTasks }
        })


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
        textInputUnitOfMeasurement.hint = "pages, essays, eggs, etc."
    }
    fun toggleMeasurementEditOff(view: View){
        textInputUnitOfMeasurement.isEnabled = false
        textInputUnitOfMeasurement.hint = "minutes"
        textInputUnitOfMeasurement.setText("minutes")
    }
    

    fun createTask(view: View) {
        var taskType = "t"
        if (radioGroup.checkedRadioButtonId == 1) taskType = "r"
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Add this activity will return you to the main menu.")
            .setTitle("Add Activity?")
            .setPositiveButton("Confirm",
                DialogInterface.OnClickListener { dialog, id ->
                    var task = Task(
                        textInputName.text.toString(),
                        textInputDescription.text.toString(),
                        taskType,
                        textInputUnitOfMeasurement.text.toString(),
                        textInputMinUnit.text.toString().toInt(),
                        textInputMaxUnit.text.toString().toInt(),
                        textViewDaysPerWeek.text.toString().toInt(),
                        true ,
                        0,
                        0,
                        0,
                        0,
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    )

                    putTask(task)
                    // finish()
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    // CANCEL
                })

        val alert = builder.create()
        alert.show()



    }

    private fun putTask(task:Task) {
        class PutTask : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg params: Void?): Void? {
                taskViewModel.insert(task)
                return null
            }
        }
        PutTask().execute()
    }
}
