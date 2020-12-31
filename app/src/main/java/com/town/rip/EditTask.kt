package com.town.rip


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.town.rip.database.Profile
import com.town.rip.database.ProfileViewModel
import com.town.rip.database.Task
import com.town.rip.database.TaskViewModel
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.*
import org.w3c.dom.Text
import java.lang.reflect.Field
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

    private var backgroundString = "#FFFFFF"
    private var backgroundStringLight = "#FFFFFF"
    private var buttonBackgroundString = "#ECEBEB"
    private var buttonTextString = "#000000"
    private var textHintString = "#000000"

    private var themeInt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentExtras = intent.extras
        if (intentExtras?.getInt("ID") != null) editMode = true

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        profileViewModel.allProfiles.observe(this, Observer {
            profileList = profileViewModel.allProfiles.value!!
            if (!editMode) taskIDList = listOf(profileList.last { it.selected }.id).toMutableList()
        })
        setContentView(R.layout.activity_edit)
        loadSharedPrefs()
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
               seekBar.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
           }

           override fun onStartTrackingTouch(seek: SeekBar) {
               // write custom code for progress is started
           }

           override fun onStopTrackingTouch(seek: SeekBar) {
               // write custom code for progress is stopped
           }
       })


    }

    private fun loadSharedPrefs() {
        val pref = applicationContext.getSharedPreferences("app", 0) // 0 - for private mode
        val editor = pref.edit()
        themeInt = pref.getInt("THEME", -1);
        if(themeInt == null)
        {
            themeInt = 0
            editor.putInt("THEME", themeInt)
            editor.commit();
        }

        loadTheme()
    }

    private fun loadTheme() {
        when (themeInt) {
            0 // light mode
            -> {
                backgroundString = "#FFFFFF"
                buttonBackgroundString = "#ECEBEB"
                backgroundStringLight = "#F8F8F8"
                buttonTextString = "#595959"
                textHintString = "#AAAAAA"
            }
            1 // dark mode
            -> {
                backgroundString = "#171717"
                backgroundStringLight = "#222222"
                buttonBackgroundString = "#113553"
                buttonTextString = "#B8A542"
                textHintString = "#685E26"
            }
            2 // dusk mode
            -> {
                backgroundString = "#7C7A7A"
                backgroundStringLight = "#8F8E8E"
                buttonBackgroundString = "#BCBDBD"
                buttonTextString = "#3C3C3C"
                textHintString = "#4C4C4C"

            }
        }

        editActivityLinearLayout.setBackgroundColor(Color.parseColor(backgroundString))

        var arraylistEditText = ArrayList<EditText>()
        arraylistEditText.add(textInputDescription)
        arraylistEditText.add(textInputName)
        arraylistEditText.add(textInputMaxUnit)
        arraylistEditText.add(textInputMinUnit)
        arraylistEditText.add(textInputUnitOfMeasurement)

        var arraylistText = ArrayList<TextView>()
        arraylistText.add(textViewEditActivityHeader)
        arraylistText.add(textViewEditActivitySubeader)
        arraylistText.add(activityDescriptionLabel)
        arraylistText.add(activityNameLabel)
        arraylistText.add(textViewUnitOfMeasurement)
        arraylistText.add(textInputMinUnit)
        arraylistText.add(textView19)
        arraylistText.add(textInputMaxUnit)
        arraylistText.add(textView13)
        arraylistText.add(textView14)
        arraylistText.add(textView6)
        arraylistText.add(textViewDaysPerWeek)
        arraylistText.add(textView8)
        arraylistText.add(textView15)

        var arraylistButton = ArrayList<Button>()
        arraylistButton.add(button4)
        arraylistButton.add(buttonActivityEditFinish)
        for(textInput in arraylistText)
        {
            textInput.setTextColor(Color.parseColor(buttonTextString))
            textInput.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonTextString))
            textInput.setHintTextColor(Color.parseColor(textHintString))
        }
        for(textInput in arraylistEditText)
        {
            textInput.setTextColor(Color.parseColor(buttonTextString))
            textInput.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonTextString))
            textInput.setHintTextColor(Color.parseColor(textHintString))
            setCursorColor(textInput, Color.parseColor(buttonTextString))
        }

        seekBar.thumbTintList = ColorStateList.valueOf(Color.parseColor(buttonTextString))
        seekBar.progressTintList = ColorStateList.valueOf(Color.parseColor(buttonTextString))

        radioButtonMinutes.setTextColor(Color.parseColor(buttonTextString))
        radioButtonMinutes.buttonTintList = ColorStateList.valueOf(Color.parseColor(buttonTextString))
        radioButtonNonMinutes.setTextColor(Color.parseColor(buttonTextString))
        radioButtonNonMinutes.buttonTintList = ColorStateList.valueOf(
            Color.parseColor(
                buttonTextString
            )
        )

        for(button in arraylistButton)
        {
            button.setTextColor(Color.parseColor(buttonTextString))
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        }



    }

    private fun setCursorColor(view: EditText, @ColorInt color: Int) {
        try {
            // Get the cursor resource id
            var field: Field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true
            val drawableResId: Int = field.getInt(view)

            // Get the editor
            field = TextView::class.java.getDeclaredField("mEditor")
            field.isAccessible = true
            val editor: Any = field.get(view)

            // Get the drawable and set a color filter
            val drawable = ContextCompat.getDrawable(view.context, drawableResId)
            drawable!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            val drawables = arrayOf<Drawable?>(drawable, drawable)

            // Set the drawables
            field = editor.javaClass.getDeclaredField("mCursorDrawable")
            field.isAccessible = true
            field.set(editor, drawables)
        } catch (ignored: Exception) {
        }
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

    fun deleteTask(view: View){

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("This will permanently remove your activity from generation.")
            .setTitle(title_string)
            .setPositiveButton("Delete Activity?",
                DialogInterface.OnClickListener { dialog, id ->
                    taskViewModel.delete(taskViewModel.allTasks.value!!.find { it.id == task_id }!!)
                    finish() // return to previous screen
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    // CANCEL
                })

        val alert = builder.create()
        alert.show()
    }

    fun cancelTask(view: View){
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
        if(textInputName.text.toString() == "") showVerticalToast("Set activity name")
        else if(textInputDescription.text.toString() == "") showVerticalToast("Set activity description")
        else if(unit_of_measurement == "") showVerticalToast("Set unit of measurement")
        else if(textInputMinUnit.text.toString() == ""  || textInputMaxUnit.text.toString() == "") showVerticalToast(
            "Set min/max values"
        )
        else {
            var min = textInputMinUnit.text.toString().toInt()
            var max = textInputMaxUnit.text.toString().toInt()
            if(textInputMinUnit.text.toString().toInt() > textInputMaxUnit.text.toString().toInt()){
                max = textInputMinUnit.text.toString().toInt()
                min = textInputMaxUnit.text.toString().toInt()
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
                            min,
                            max,
                            textViewDaysPerWeek.text.toString().toInt(),
                            true,
                            0,
                            0,
                            0,
                            0,
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                            profileList.last { it.selected }.id,
                            taskIDList
                        )


                        if (editMode) {
                            task.id = task_id
                            taskViewModel.update(task)
                        } else putTask(task)
                        finish() // return to previous screen


                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, id ->
                        // CANCEL
                    })

            val alert = builder.create()
            alert.show()
        }
    }

    private fun showVerticalToast(s: String, debugBoolean: Boolean = true) {
        var toast =  Toast.makeText(applicationContext, s, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
        if(debugBoolean) Log.d("", s)

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
