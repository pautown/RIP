package com.town.rip

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.*
import kotlinx.android.synthetic.main.dynamic_linear_layout_task.view.*


class ScrollingViewTask : AppCompatActivity() {
    private lateinit var taskViewModel :TaskViewModel
    private lateinit var tasksList: List<Task>
    private lateinit var taskViewsList: List<View>

    var background_tint : Boolean = false;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        setContentView(R.layout.activity_scrolling_view_tasks)
        taskViewModel.allTasks.observe(this, Observer {
            loadTasks()
        })



       // Log.d("LISTSIZEVM", taskViewModel2.allTasks.value?.toString())
       // loadTasks()
       button7sadasdasd.setOnClickListener{insertTask()}


    }

    private fun insertTask() {
        var task = Task("aaaaaaaaaa","asdddddddd", "t", "minutes", 5, 10,6)
        taskViewModel.insert(task)
        addTask(task)

    }


    private fun loadTasks() {
        Log.d("LISTSIZEVM", taskViewModel.allTasks.value?.toString())
        if(!taskViewModel.allTasks.value.isNullOrEmpty()) {
            tasksList = taskViewModel.allTasks.value!!
            for (task in tasksList) addTask(task)
        }
        taskViewModel.allTasks.removeObservers(this)
        (textViewViewTextsLoadingMessage.getParent() as ViewManager).removeView(textViewViewTextsLoadingMessage)

    }

    private fun addTask(task:Task){
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.dynamic_linear_layout_task, null)
        val container = findViewById<LinearLayout>(R.id.vertical_layout_view_1)
        container.addView(view)
        view.textViewDynamicTaskName.text = task.name
        view.textViewDynamicTaskDescription.text = task.description
        view.textViewDynamicTaskFrequency.text = task.description
        view.buttonInfo.setOnClickListener {
            viewTask(view)
        }
        if (background_tint) view.dynamic_linear_layout_base_task.setBackgroundColor(Color.parseColor("#EEEEEE"))
        background_tint = !background_tint

    }

    private fun viewTask(view: View) {

        if (view.linearLayoutInfo.visibility == View.VISIBLE) view.linearLayoutInfo.visibility = View.GONE
        else view.linearLayoutInfo.visibility = View.VISIBLE
    }

}
