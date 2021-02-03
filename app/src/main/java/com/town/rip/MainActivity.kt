package com.town.rip

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.town.rip.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private var themeInt: Int = 0
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var generatedTaskListViewModel: GeneratedTaskListViewModel

    private lateinit var tasksList: List<Task>
    private lateinit var generatedTasksList: List<GeneratedTask>
    private var profileList: List<Profile> = listOf()
    private var mutableProfileList: MutableList<String> = mutableListOf()
    private var profileOptions = 1

    private var backgroundString = "#FFFFFF"
    private var buttonBackgroundString = "#ECEBEB"
    private var buttonBackgroundDisabledString = "#ECEBEB"
    private var buttonTextString = "#000000"

    private var taskViewModelGenerated = false
    private var profileViewModelGenerated = false
    private var generatedTaskListViewModelGenerated = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        generatedTaskListViewModel = ViewModelProvider(this).get(GeneratedTaskListViewModel::class.java)
        setContentView(R.layout.activity_main)

        profileViewModel.allProfiles.observe(this, Observer {
            profileList = profileViewModel.allProfiles.value!!
            profileViewModelGenerated = true
            rebuildMutableProfileList()
            if (profileList.isNotEmpty()) {
                if (profileList.filter { it.selected }.isEmpty()) {
                    var profile = profileList.first()
                    profile.selected = true
                    profileViewModel.update(profile)
                }
                buttonProfile.text = "Profile: ${profileList.last { it.selected }.name}"
            }
            if(taskViewModelGenerated && taskViewModelGenerated && profileViewModelGenerated)
                setGenerateButtonEnabled()
        })

        generatedTaskListViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { generatedTaskListViewModel.repository.allGeneratedTaskLists }
            generatedTasksList = generatedTaskListViewModel.allTasks.value!!
            generatedTaskListViewModelGenerated = true
            if(taskViewModelGenerated && taskViewModelGenerated && profileViewModelGenerated)
                setGenerateButtonEnabled()
        })

        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { taskViewModel.repository.allTasks }
            tasksList = taskViewModel.allTasks.value!!
            taskViewModelGenerated = true
            if(taskViewModelGenerated && taskViewModelGenerated && profileViewModelGenerated)
                setGenerateButtonEnabled()
        })

        loadSharedPrefs()
    }

    private fun setGenerateButtonEnabled() {
        buttonGenerate.isEnabled = false
        buttonGenerate.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundDisabledString))

        if(taskViewModelGenerated && taskViewModelGenerated && profileViewModelGenerated) {
            for (task in taskViewModel.allTasks.value!!)
                if ((task.profile_ids.contains(profileList.last { it.selected }.id) && task.enabled && task.freq > 0) ||
                    (!generatedTaskListViewModel.allTasks.value.isNullOrEmpty() &&
                             !generatedTaskListViewModel.allTasks.value!!.filter {it.profile_id == profileList.last { it.selected }.id  }.isNullOrEmpty()
                            && !generatedTaskListViewModel.allTasks.value!!.last { it.profile_id == profileList.last { it.selected }.id }.task_list_finished)) {
                    buttonGenerate.isEnabled = true
                    buttonGenerate.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
                    setGenerateButtonText()
                    break
                }
        }
    }


    fun setTheme(view:View){
        val pref = applicationContext.getSharedPreferences("app", 0) // 0 - for private mode
        val editor = pref.edit()
        themeInt ++
        if(themeInt > 2) themeInt = 0
        if(themeInt == 0) buttonTheme.text = "Light Mode"
        else if(themeInt == 1) buttonTheme.text = "Dark Mode"
        else if(themeInt == 2) buttonTheme.text = "Dusk Mode"
        editor.putInt("THEME", themeInt)
        editor.commit()
        loadTheme()
        setGenerateButtonEnabled()
    }

    private fun loadSharedPrefs() {
        val pref = applicationContext.getSharedPreferences("app", 0) // 0 - for private mode
        val editor = pref.edit()
        themeInt = pref.getInt("THEME", -1);
        if(themeInt == null)
        {
            themeInt = 0
            editor.putInt("THEME", themeInt)
            editor.commit()
        }
        when (themeInt) {
            0 -> buttonTheme.text = "Light Mode"
            1 -> buttonTheme.text = "Dark Mode"
            2 -> buttonTheme.text = "Dusk Mode"
        }
        when (themeInt) {
            0 // light mode
            -> {
                backgroundString = "#FFFFFF"
                buttonBackgroundString = "#ECEBEB"
                buttonBackgroundDisabledString = "#A1A0A0"
                buttonTextString = "#595959"
            }
            1 // dark mode
            -> {
                backgroundString = "#171717"
                buttonBackgroundString = "#113553"
                buttonBackgroundDisabledString = "#33383C"
                buttonTextString = "#B8A542"
            }
            2 // dusk mode
            -> {
                backgroundString = "#7C7A7A"
                buttonBackgroundString ="#BCBDBD"
                buttonBackgroundDisabledString = "#969797"
                buttonTextString = "#3C3C3C"
            }
        }
        loadTheme()
    }

    private fun loadTheme() {

        when (themeInt) {
            0 // light mode
            -> {
                backgroundString = "#FFFFFF"
                buttonBackgroundString = "#ECEBEB"
                buttonBackgroundDisabledString = "#A1A0A0"
                buttonTextString = "#595959"
            }
            1 // dark mode
            -> {
                backgroundString = "#171717"
                buttonBackgroundString = "#113553"
                buttonBackgroundDisabledString = "#33383C"
                buttonTextString = "#B8A542"
            }
            2 // dusk mode
            -> {
                backgroundString = "#7C7A7A"
                buttonBackgroundString ="#BCBDBD"
                buttonBackgroundDisabledString = "#969797"
                buttonTextString = "#3C3C3C"
            }
        }

        constraintLayout.setBackgroundColor(Color.parseColor(backgroundString))

        textView.setTextColor(Color.parseColor(buttonTextString))
        textView2.setTextColor(Color.parseColor(buttonTextString))

        buttonGenerate.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        buttonGenerate.setTextColor(Color.parseColor(buttonTextString))

        button5.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        button5.setTextColor(Color.parseColor(buttonTextString))

        button6.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        button6.setTextColor(Color.parseColor(buttonTextString))

        buttonCalender.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        buttonCalender.setTextColor(Color.parseColor(buttonTextString))

        buttonTheme.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        buttonTheme.setTextColor(Color.parseColor(buttonTextString))

        buttonProfile.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        buttonProfile.setTextColor(Color.parseColor(buttonTextString))

        buttonSettings.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        buttonSettings.setTextColor(Color.parseColor(buttonTextString))

        setGenerateButtonEnabled()
    }

    private fun setGenerateButtonText() {
        var buttonText = "Generate Activities"
        if(!generatedTaskListViewModel.allTasks.value.isNullOrEmpty() &&
            generatedTaskListViewModel.allTasks.value!!.filter{ it.profile_id == profileList.last { it.selected }.id }.isNotEmpty())
            if(!generatedTaskListViewModel.allTasks.value!!.last { it.profile_id == profileList.last { it.selected }.id }.task_list_finished)
                buttonText = "Continue Activities"
        buttonGenerate.text = buttonText
    }

    private fun rebuildMutableProfileList() {
        mutableProfileList.clear()
        for(profile in profileList) mutableProfileList.add(profile.name)
        profileOptions = 1
        mutableProfileList.add("------------------")
        mutableProfileList.add("create new profile")
        if(profileList.size > 1)
        {
            mutableProfileList.add("delete existing profile")
            profileOptions++
        }
    }

    val Float.toPx: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun AlertDialog.Builder.setEditText(editText: EditText, textHint: String = ""): AlertDialog.Builder {
        val container = FrameLayout(context)
        container.addView(editText)
        editText.hint = textHint
        val containerParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        val marginHorizontal = 48F
        val marginTop = 16F
        containerParams.topMargin = (marginTop / 2).toPx
        containerParams.leftMargin = marginHorizontal.toInt()
        containerParams.rightMargin = marginHorizontal.toInt()
        container.layoutParams = containerParams

        val superContainer = FrameLayout(context)
        superContainer.addView(container)

        setView(superContainer)

        return this
    }

    fun setProfile(view: View){
        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose profile")
        builder.setItems(mutableProfileList.toTypedArray()) { dialog, which ->

           if(which < mutableProfileList.size - (1+profileOptions)) {
               var profile = profileList.last { it.selected }
               profile.selected = false
               profileViewModel.update(profile)
               profile = profileList[which]
               profile.selected = true
               profileViewModel.update(profile)
               buttonProfile.text = "Profile: " + profile.name
               setGenerateButtonText()
           }else if (which == mutableProfileList.size - profileOptions){
               val editText = EditText(this)
               AlertDialog.Builder(this)
                   .setTitle("Create Profile")
                   .setEditText(editText, "profile name")
                   .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                       // Do your work with text here
                       val text = editText.text.toString()
                       showVerticalToast("Profile $text created")
                       var profile = Profile(
                           text, SimpleDateFormat(
                               "dd/MM/yyyy",
                               Locale.getDefault()
                           ).format(
                               Date()
                           ), false
                       )
                       profileViewModel.insert(profile)
                   }
                   .setNegativeButton("Cancel", null)
                   .show()


           }else if (which == mutableProfileList.size - 1 && profileOptions == 2){
               mutableProfileList.clear()
               for(profile in profileList) mutableProfileList.add(profile.name)
               val builder = AlertDialog.Builder(this)
               builder.setTitle("Delete profile")
               builder.setItems(mutableProfileList.toTypedArray()) { dialog, which ->

                   var profile = profileList.last { it.name == mutableProfileList[which] }
                   val builder2: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
                   builder2.setMessage("This will also delete all activities created by this profile.")
                       .setTitle("Delete Profile?")
                       .setPositiveButton("Confirm",
                           DialogInterface.OnClickListener { dialog, id ->
                               profileViewModel.delete(profile)

                               for(task in tasksList) {
                                   if (task.profile_id == profile.id)
                                       taskViewModel.delete(task)
                                   else if (task.profile_ids.contains(profile.id)) {
                                       var profileIDsTemp = task.profile_ids.toMutableList()
                                       for ((i, id) in profileIDsTemp.withIndex()) {
                                           if (id == profile.id) {
                                               profileIDsTemp.remove(i)
                                               task.profile_ids = profileIDsTemp
                                               taskViewModel.update(task)
                                               break
                                           }
                                       }
                                   }
                               }

                               for(generatedTask in generatedTasksList.filter { it.profile_id == profile.id })
                                   generatedTaskListViewModel.delete(generatedTask)
                               if(profileList.none { it.selected }) {
                                   profile = profileList.first()
                                   profile.selected = true
                                   profileViewModel.update(profile)
                               }
                           })
                       .setNegativeButton("No",
                           DialogInterface.OnClickListener { dialog, id ->
                               // CANCEL
                           })
                   val alert2 = builder2.create()
                   alert2.show()

                   buttonProfile.text = "Profile: " + profile.name

               }
               val dialog = builder.create()
               dialog.show()


           }
        }


        val dialog = builder.create()
        dialog.show()
        rebuildMutableProfileList()
    }

    private fun showVerticalToast(s: String, debugBoolean: Boolean = true) {
        var toast =  Toast.makeText(applicationContext, s, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
    }

    fun viewCalender(view: View) {
        val intent = Intent(this, CalenderActivity::class.java).apply {
            // putExtras(extras)
        }
        startActivity(intent)
    }

    fun viewTasks(view: View) {
        val intent = Intent(this, ScrollingViewTask::class.java).apply {
           // putExtras(extras)
        }
        startActivity(intent)
    }

    fun viewSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java).apply {
            // putExtras(extras)
        }
        startActivity(intent)
    }

    fun generateTasks(view: View) {
        val intent = Intent(this, GeneratedTaskListActivity::class.java)
        startActivity(intent)
    }

    fun addTask(view: View) {
        val intent = Intent(this, EditTask::class.java).apply {
            // putExtras(extras)
        }
        startActivity(intent)
    }


}
