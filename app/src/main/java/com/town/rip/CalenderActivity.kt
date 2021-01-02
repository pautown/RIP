package com.town.rip

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.town.rip.database.*
import kotlinx.android.synthetic.main.activity_calender.*


class CalenderActivity : AppCompatActivity() {
    private lateinit var generatedTaskListViewModel: GeneratedTaskListViewModel
    private lateinit var profileViewModel:ProfileViewModel
    private var profileList: List<Profile> = listOf()
    private lateinit var tasksList: List<GeneratedTask>
    private var mutableProfileList: MutableList<String> = mutableListOf()
    private var profileID:Int = 0
    private var perCounter:Int = 0
    private var viewAll:Boolean = false
    private val entries = ArrayList<Entry>()

    private var backgroundString = "#FFFFFF"
    private var backgroundStringLight = "#FFFFFF"
    private var buttonBackgroundString = "#ECEBEB"
    private var buttonTextString = "#000000"
    private var themeInt: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        generatedTaskListViewModel = ViewModelProvider(this).get(GeneratedTaskListViewModel::class.java)
        setContentView(R.layout.activity_calender)
        loadSharedPrefs()
        profileViewModel.allProfiles.observe(this, Observer {
            profileList = profileViewModel.allProfiles.value!!
            profileID = profileList.last { it.selected }.id
            buttonProfile.text = "Activities: " + profileList.last { it.selected }.name
            generatedTaskListViewModel.allTasks.observe(this, Observer { tasks ->
                tasks?.let { generatedTaskListViewModel.repository.allGeneratedTaskLists }
                tasksList = generatedTaskListViewModel.allTasks.value!!
                generatedTaskListViewModel.allTasks.removeObservers(this)
                mutableProfileList.clear()
                mutableProfileList.add("All Activities")
                for(profile in profileList) mutableProfileList.add(profile.name)
                profileViewModel.allProfiles.removeObservers(this)
                loadChartView()
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
                buttonBackgroundString = "#BCBDBD"
                buttonTextString = "#3C3C3C"
            }
        }

        constraintLayout.setBackgroundColor(Color.parseColor(backgroundString))
        header.setTextColor(Color.parseColor(buttonTextString))

        var arraylistButtons = java.util.ArrayList<Button>()
        arraylistButtons.add(buttonPer)
        arraylistButtons.add(buttonProfile)


        for(button in arraylistButtons)
        {
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
            button.setTextColor(Color.parseColor(buttonTextString))
        }
        chart.setGridBackgroundColor(Color.parseColor(backgroundString))
        chart.xAxis.textColor = Color.parseColor(buttonTextString)
        chart.axisLeft.textColor = Color.parseColor(buttonTextString)
        chart.legend.textColor = Color.parseColor(buttonTextString)
        chart.axisRight.textColor = Color.parseColor(buttonTextString)
        chart.setNoDataTextColor(Color.parseColor(buttonTextString))

        chart.setNoDataText("Loading history for this profile...")

    }

    fun setPer(view: View){
        perCounter ++
        if(perCounter > 2) perCounter = 0
        when (perCounter) {
            0 -> buttonPer.text = "Per: 1 Day"
            1 -> buttonPer.text = "Per: 7 Days"
            2 -> buttonPer.text = "Per: 31 Days"
        }
        loadChartView()
    }

    fun setProfile(view: View){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("View activities")
        builder.setItems(mutableProfileList.toTypedArray()) { dialog, which ->
            Log.v("profile id", which.toString());
            if(which != 0) {
                profileID = profileList[which - 1].id
                viewAll = false
                buttonProfile.text = "Activities: " + profileList[which - 1].name
            }else{
                viewAll = true
                tasksList = generatedTaskListViewModel.allTasks.value!!
                buttonProfile.text = "All Activities"
            }
            loadChartView()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun loadChartView() {
        // get date of first task in task list
        var task_list_id = tasksList.first().task_list_id

        // loop through tasks and create percentage entry for each days tasks
        var cumulative_complete = 0
        var iterationTasks = 0
        var i = 0
        var counterLimit = 1
        var counter = 0

        if(perCounter == 1) counterLimit = 7
        else if(perCounter == 2) counterLimit = 31

        entries.clear()
        var tempTasksList = tasksList
        if(!viewAll) tempTasksList = tempTasksList.filter { it.profile_id == profileID }
        if(!viewAll && tempTasksList.filter{it.profile_id == profileID}.isNotEmpty())
            task_list_id = tempTasksList.first().task_list_id
        for(task in tempTasksList) {
            if (task.task_list_id != task_list_id){
                task_list_id = task.task_list_id
                counter++
                if (counter == counterLimit) {
                    counter = 0
                    Log.d("Newest Task:", task.name.toString())
                    if (cumulative_complete > 0)
                        cumulative_complete /= iterationTasks
                    entries.add(Entry(i.toFloat(), cumulative_complete.toFloat()))
                    Log.d("Activities Total:", iterationTasks.toString())
                    Log.d("Activities Percentage:", cumulative_complete.toString())
                    cumulative_complete = 0
                    cumulative_complete += getTaskProgress(task)

                    iterationTasks = 0
                    i++
                }
            }else if(task == tempTasksList.last())
            {
                if (task.amount_completed > 0)
                    cumulative_complete += getTaskProgress(task)
                iterationTasks ++
                if (cumulative_complete > 0)
                    cumulative_complete /= iterationTasks
                entries.add(Entry(i.toFloat(), cumulative_complete.toFloat()))
                Log.d("Activities Total:", iterationTasks.toString())
                Log.d("Activities Percentage:", cumulative_complete.toString())
            }else if (task.amount_completed > 0) cumulative_complete += getTaskProgress(task)
            iterationTasks++
        }
        val vl = LineDataSet(entries, "Percentage Complete")
        //Part4
        vl.setColors(Color.parseColor(buttonBackgroundString))
        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.highLightColor = Color.parseColor(backgroundString)
        vl.valueTextColor = Color.parseColor(buttonTextString)
        vl.color = Color.parseColor(buttonTextString)

        vl.setColor(Color.parseColor(buttonTextString))
        vl.setCircleColor(Color.parseColor(buttonTextString))
        vl.setDrawValues(false)

        //Part5
        chart.xAxis.labelRotationAngle = 0f

        //Part6
        if(!entries.isNullOrEmpty() && !entries.isNullOrEmpty())
            chart.data = LineData(vl)
        else if(!entries.isNullOrEmpty()) chart.data.clearValues()

        //Part7
        chart.axisRight.isEnabled = false
        vl.fillColor = Color.parseColor(buttonTextString)

        //chart.xAxis.axisMaximum = j+0.1f

        //Part8
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.setNoDataText("No history for this profile yet!")
        //Part9
        chart.description.text = ""

        //Part10
        chart.animateX(1800, Easing.EaseInExpo)

    }

    private fun getTaskProgress(task: GeneratedTask): Int {
        Log.d("Task Name:", task.name)
        if(task.amount_completed !=0)
            Log.d("Percentage completed:", ((task.amount_completed * 100.0f) / task.amount_to_complete).toInt().toString())
        return if(task.amount_completed == 0) 0
        else ((task.amount_completed * 100.0f) / task.amount_to_complete).toInt()
    }



}