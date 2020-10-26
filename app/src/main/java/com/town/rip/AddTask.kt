package com.town.rip


import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_task.*
import android.app.AlertDialog
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class AddTask : AppCompatActivity() {
    private var taskStage: Int = 0
    private lateinit var taskViewModel: TaskViewModel

    var layouts: ArrayList<LinearLayout> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { taskViewModel.repository.allTasks }
        })

        setContentView(R.layout.activity_add_task)

        layouts.add(linearLayout1)
        layouts.add(linearLayout2)
        layouts.add(linearLayout3)
        layouts.add(linearLayout4)
        layouts.add(linearLayout5)
        for (layout in layouts) layout.visibility = View.GONE
        linearLayout1.visibility = View.VISIBLE
        progressBar2.progress = 1
        //TaskDatabase(activity!!).getTaskDao().addTask()

        seekBar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                textView5DaysPerWeek.text = seekBar.progress.toString()
            }
            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped

            }
        })
    }


    fun iterateForward(view: View){
        if(taskStage == 4){
            var taskType = "t"
            if(radioGroup.checkedRadioButtonId == 1) taskType = "r"
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Add this activity will return you to the main menu.")
                .setTitle("Add Activity?")
                .setPositiveButton("Confirm",
                    DialogInterface.OnClickListener { dialog, id ->
                        var task = Task(editText1.text.toString(),editText2.text.toString(),taskType, editText3.text.toString(), editText4_min.text.toString().toInt(), editText4_max.text.toString().toInt(), textView5DaysPerWeek.text.toString().toInt())

                        grabTasks(task)
                        // finish()
                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, id ->
                        // CANCEL
                    })

            val alert = builder.create()
            alert.show()

        }else{
            taskStage++

        layouts[taskStage - 1].visibility = View.GONE
        layouts[taskStage].visibility = View.VISIBLE
        updateTextViewInstructions()
        progressBar2.progress = taskStage + 1
        if(taskStage == 4) buttonContinue.text = "Finish"
        else if(buttonPrevious.text == "Cancel") buttonPrevious.text = "Previous"}
    }


    private fun grabTaskList(){
        Log.d("LISTSIZEVM",  taskViewModel.allTasks.value?.get(1)?.name)
        Log.d("LISTSIZEVM", taskViewModel.allTasks.value?.toString())
    }
    private fun grabTasks(task:Task) {
        class GrabTasks : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg params: Void?): Void? {


                taskViewModel.insert(task)
                return null
            }
        }
        GrabTasks().execute()
    }

    fun iterateBackward(view: View){
        //createPopup("Canceling this activity will return you to the main menu.", "Cancel Activity?", "Confirm", finish(), "No")
        if(taskStage == 0){
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Canceling this activity will return you to the main menu.")
                .setTitle("Cancel Activity?")
                .setPositiveButton("Confirm",
                    DialogInterface.OnClickListener { dialog, id ->
                        finish()
                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, id ->
                        // CANCEL
                    })

            val alert = builder.create()

            // show alert dialog
            alert.show()

        }else{
            taskStage--
            layouts[taskStage + 1].visibility = View.GONE
            layouts[taskStage].visibility = View.VISIBLE
            updateTextViewInstructions()
            progressBar2.progress = taskStage + 1
            if(taskStage == 0) buttonPrevious.text = "Cancel"
            else if(buttonContinue.text == "Finish") buttonContinue.text = "Continue"
        }
    }

    private fun createPopup(s: String, s1: String, s2: String, finish: Unit, s3: String) {

    }

    private fun updateTextViewInstructions() {
        var instructionsString = ""
        if(taskStage == 0) instructionsString = getString(R.string.activity_screen_textviewinstructions_1)
        else if(taskStage == 1) instructionsString = getString(R.string.activity_screen_textviewinstructions_2)
        else if(taskStage == 2) instructionsString = getString(R.string.activity_screen_textviewinstructions_3)
        else if(taskStage == 3) instructionsString = getString(R.string.activity_screen_textviewinstructions_4)
        else if(taskStage == 4) instructionsString = getString(R.string.activity_screen_textviewinstructions_5)
        textViewInstructions.text = instructionsString
    }

    fun toggleMeasurementEditOn(view: View){
        editText3.isEnabled = true
        editText3.setText("")
        editText3.hint = "pages, essays, eggs, etc."
    }
    fun toggleMeasurementEditOff(view: View){
        editText3.isEnabled = false
        editText3.hint = "minutes"
        editText3.setText("minutes")
    }






}
