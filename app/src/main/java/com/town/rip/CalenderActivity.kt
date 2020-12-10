package com.town.rip

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import kotlinx.android.synthetic.main.activity_calender.*


class CalenderActivity : AppCompatActivity() {
    private lateinit var chart: LineChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        chart = LineChart(this)
        linearLayoutChart.addView(chart) // add the programmatically created chart


        /*generatedTaskListViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { generatedTaskListViewModel.repository.allGeneratedTaskLists }
            taskViewModel.allTasks.observe(this, Observer { tasks ->
                tasks?.let { taskViewModel.repository.allTasks }
                tasksList = taskViewModel.allTasks.value!!
                buttonGenerate.isEnabled = false
                for(task in taskViewModel.allTasks.value!!)
                    if(task.profile_ids.contains(profileList.last { it.selected }.id) && task.enabled) {
                        buttonGenerate.isEnabled = true
                        setGenerateButtonText()
                        break
                    }
            })
        })*/
    }
}