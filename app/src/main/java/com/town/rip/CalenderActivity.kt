package com.town.rip

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.town.rip.database.*
import kotlinx.android.synthetic.main.activity_calender.*
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.*
import kotlinx.android.synthetic.main.dynamic_linear_layout_task.view.*


class CalenderActivity : AppCompatActivity() {
    private lateinit var generatedTaskListViewModel: GeneratedTaskListViewModel
    private lateinit var profileViewModel:ProfileViewModel
    private var profileList: List<Profile> = listOf()
    private lateinit var tasksList: List<GeneratedTask>
    private var mutableProfileList: MutableList<String> = mutableListOf()
    private var profileID:Int = 0
    private var viewAll:Boolean = false
    private val entries = ArrayList<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        generatedTaskListViewModel = ViewModelProvider(this).get(GeneratedTaskListViewModel::class.java)
        setContentView(R.layout.activity_calender)

        generatedTaskListViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { generatedTaskListViewModel.repository.allGeneratedTaskLists }
            tasksList = generatedTaskListViewModel.allTasks.value!!
            generatedTaskListViewModel.allTasks.removeObservers(this)
            profileViewModel.allProfiles.observe(this, Observer {
                profileList = profileViewModel.allProfiles.value!!
                profileID = profileList.last { it.selected }.id
                mutableProfileList.clear()
                mutableProfileList.add("All Activities")
                for(profile in profileList) mutableProfileList.add(profile.name)
                profileViewModel.allProfiles.removeObservers(this)
                loadChartView()
            })
        })
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
        if(!viewAll && tasksList.filter{it.profile_id == profileID}.isNotEmpty())
            tasksList.filter{it.profile_id == profileID}.first().task_list_id
        // loop through tasks and create percentage entry for each days tasks
        var cumulative_complete = 0
        var days_tasks = 0
        var i = 0

        //clear entries
        entries.clear()
        var tempTasksList = tasksList
        if(!viewAll) tempTasksList = tempTasksList.filter { it.profile_id == profileID }
        for(task in tempTasksList)
        {
            if(task.task_list_id == task_list_id && task != tasksList.last())
            {
                if(task.amount_completed > 0)
                    cumulative_complete += (task.amount_completed/task.amount_to_complete)
                days_tasks++
            }else{
                task_list_id = task.task_list_id
                if(cumulative_complete > 0)
                    cumulative_complete = (((cumulative_complete * 100.0f) / days_tasks)).toInt()

                Log.d("task list i", i.toString())
                Log.d("task list id", task_list_id.toString())
                Log.d("task list cumulative", cumulative_complete.toString())
                entries.add(Entry(i.toFloat(),cumulative_complete.toFloat()))
                cumulative_complete = 0
                days_tasks = 0
                i++
            }
        }
        val vl = LineDataSet(entries, "Percentage Complete")
        //Part4
        vl.setDrawValues(false)
        vl.setDrawFilled(true)

        //Part5
        chart.xAxis.labelRotationAngle = 0f

        //Part6
        if(entries.isNotEmpty())
            chart.data = LineData(vl)
        else chart.data.clearValues()

        //Part7
        chart.axisRight.isEnabled = false
        //chart.xAxis.axisMaximum = j+0.1f

        //Part8
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)

        //Part9
        chart.description.text = "Days"
        chart.setNoDataText("No history for this profile yet!")

        //Part10
        chart.animateX(1800, Easing.EaseInExpo)

    }



}