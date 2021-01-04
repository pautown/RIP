package com.town.rip

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
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
import com.town.rip.database.Profile
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.*
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.constraintLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ScrollingViewTask : AppCompatActivity() {
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var profileViewModel : ProfileViewModel
    private var mutableProfileList: MutableList<String> = mutableListOf()

    private var tasksList: List<Task> = listOf()

    private var tasksListViews: MutableList<View> = mutableListOf()
    private var profileList: List<Profile> = listOf()
    private var profileID:Int = 0
    private var viewAll:Boolean = false


    private var backgroundTint : Boolean = false

    private var backgroundString = "#FFFFFF"
    private var backgroundStringLight = "#FFFFFF"
    private var buttonBackgroundString = "#ECEBEB"
    private var buttonTextString = "#000000"
    private var themeInt: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        setContentView(R.layout.activity_scrolling_view_tasks)
        loadSharedPrefs()
        profileViewModel.allProfiles.observe(this, Observer {tasks -> tasks?.let { profileViewModel.repository.allProfiles }
            profileList = profileViewModel.allProfiles.value!!
            profileID = profileList.last { it.selected }.id
            mutableProfileList.clear()
            mutableProfileList.add("All Activities")
            for(profile in profileList) mutableProfileList.add(profile.name)
            buttonActivities.text = "Activities: ${profileList.last { it.selected }.name}"
            profileViewModel.allProfiles.removeObservers(this)
            taskViewModel.allTasks.observe(this, Observer {
                loadTasks()
            })
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
            }
            1 // dark mode
            -> {
                backgroundString = "#171717"
                backgroundStringLight = "#222222"
                buttonBackgroundString = "#113553"
                buttonTextString = "#B8A542"
            }
            2 // dusk mode
            -> {
                backgroundString = "#7C7A7A"
                backgroundStringLight = "#8F8E8E"
                buttonBackgroundString ="#BCBDBD"
                buttonTextString = "#3C3C3C"
            }
        }
        constraintLayout.setBackgroundColor(Color.parseColor(backgroundString))

        textView5.setTextColor(Color.parseColor(buttonTextString))
        textViewSubheading.setTextColor(Color.parseColor(buttonTextString))
        textViewViewTextsLoadingMessage.setTextColor(Color.parseColor(buttonTextString))

        buttonActivities.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        buttonActivities.setTextColor(Color.parseColor(buttonTextString))
    }

    fun setProfile(view: View){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("View activities")
        builder.setItems(mutableProfileList.toTypedArray()) { dialog, which ->
            if(which != 0) {
                profileID = profileList[which - 1].id
                viewAll = false
                buttonActivities.text = "Activities: " + profileList[which - 1].name
            }else{
                viewAll = true
                tasksList = taskViewModel.allTasks.value!!
                buttonActivities.text = "All Activities"
            }
            loadTasks()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun loadTasks() {
        vertical_layout_view_1.removeAllViews()
        taskViewModel.allTasks.removeObservers(this)
        if(!taskViewModel.allTasks.value.isNullOrEmpty()) {
            if(viewAll) tasksList = taskViewModel.allTasks.value!!
            else taskViewModel.allTasks.value!!.filter { it.profile_id == profileID }

            var enabledTasks = 0
            for (task in tasksList) {
                if (task.enabled) enabledTasks++
                addTask()
                updateSubheading(tasksList.size, enabledTasks)
            }
            updateTaskDisplays()
        }else textViewSubheading.text = "No activities for this profile, create some!"

        taskViewModel.allTasks.observe(this, Observer {
            var enabledTasks = 0
            if((viewAll && tasksList.size != taskViewModel.allTasks.value!!.size) ||
                (!viewAll && tasksList.size != taskViewModel.allTasks.value!!.filter { it.profile_id == profileID }.size))
            {
                vertical_layout_view_1.removeAllViews()
                tasksList = if(viewAll) taskViewModel.allTasks.value!!
                else taskViewModel.allTasks.value!!.filter { it.profile_id == profileID }
                for (task in tasksList) {
                    if (task.enabled) enabledTasks++
                    addTask()
                }
                updateSubheading(tasksList.size,enabledTasks)
                updateTaskDisplays()
            }else if(!viewEnabledEqualsRepositoryEnabled()){
                if(viewAll) tasksList =  taskViewModel.allTasks.value!!
                else taskViewModel.allTasks.value!!.filter { it.profile_id == profileID }
                enabledTasks = 0
                for(task in tasksList) if (task.enabled) enabledTasks++
                updateSubheading(tasksList.size,enabledTasks)
                updateTaskDisplays()
            }
        })

    }

    private fun viewEnabledEqualsRepositoryEnabled(): Boolean {
        var returnBool = true
        for ((i, task) in tasksList.withIndex())
            if((viewAll && tasksList[i].enabled !=  taskViewModel.allTasks.value!![i].enabled) ||
                (!viewAll && tasksList[i].enabled !=  taskViewModel.allTasks.value!!.filter { it.profile_id == profileID }[i].enabled))
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
            setTaskTheme(view)
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
                view.linearLayoutInfo.visibility = View.GONE
                startActivity(launchEditScreen(this, task))
            }

            tasksListViews.add(view)
            for(profile in profileViewModel.allProfiles.value!!)
            {

                val inflaterProfile = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val viewProfile: View = inflaterProfile.inflate(R.layout.dynamic_view_profile, null)
                val container = view.layoutProfiles
                container.addView(viewProfile)

                viewProfile.checkboxTextView.text = profile.name

                if (!backgroundTint)
                    viewProfile.divider3.setBackgroundColor(Color.parseColor(backgroundStringLight))
                else
                    viewProfile.divider3.setBackgroundColor(Color.parseColor(backgroundString))

                viewProfile.checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
                viewProfile.checkboxTextView.setTextColor(Color.parseColor(buttonTextString))

                viewProfile.checkBox.isChecked = task.profile_ids.contains(profile.id)
                viewProfile.checkBox.isEnabled = profile.id != task.profile_id
                viewProfile.checkBox.isClickable = false

                viewProfile.profileLayout.setOnClickListener{ toggleTaskProfiles(viewProfile, profile, task)}
            }
            backgroundTint = !backgroundTint
        }
    }

    private fun toggleTaskProfiles(viewProfile: View, profile: Profile, task: Task) {
        var profile_ids_temp = task.profile_ids.toMutableList()
        if(viewProfile.checkBox.isEnabled) viewProfile.checkBox.toggle()
        if(viewProfile.checkBox.isChecked && !task.profile_ids.contains(profile.id))
            profile_ids_temp.add(profile.id)
        else if (!viewProfile.checkBox.isChecked && task.profile_ids.contains(profile.id)){
            for ((i, id) in profile_ids_temp.withIndex()) {
                if (id == profile.id)
                {
                    profile_ids_temp.removeAt(i)
                    break
                }
            }
        }
        task.profile_ids = profile_ids_temp
        taskViewModel.update(task)
    }

    private fun setTaskTheme(view: View) {

        if (backgroundTint) {
            view.linearLayout.setBackgroundColor(Color.parseColor(backgroundStringLight))
            view.view2.setBackgroundColor(Color.parseColor(backgroundString))
            view.divider6.setBackgroundColor(Color.parseColor(backgroundString))
            view.view.setBackgroundColor(Color.parseColor(backgroundString))
        }else{
            view.linearLayout.setBackgroundColor(Color.parseColor(backgroundString))
            view.view2.setBackgroundColor(Color.parseColor(backgroundStringLight))
            view.divider6.setBackgroundColor(Color.parseColor(backgroundStringLight))
            view.view.setBackgroundColor(Color.parseColor(backgroundStringLight))
        }

        view.textViewDynamicTaskName.setTextColor(Color.parseColor(buttonTextString))
        view.textViewEnabled.setTextColor(Color.parseColor(buttonTextString))
        view.textViewDynamicTaskDescription.setTextColor(Color.parseColor(buttonTextString))
        view.textViewDynamicTaskMagnitude.setTextColor(Color.parseColor(buttonTextString))
        view.textViewDynamicTaskFrequency.setTextColor(Color.parseColor(buttonTextString))
        view.textView4.setTextColor(Color.parseColor(buttonTextString))




        view.buttonScrollingViewInfoEdit.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        view.buttonScrollingViewInfoEdit.setTextColor(Color.parseColor(buttonTextString))
    }

    private fun addTask(){
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.dynamic_linear_layout_task, null)
        val container = findViewById<LinearLayout>(R.id.vertical_layout_view_1)
        container.addView(view)

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
