package com.town.rip

import android.content.ClipDescription
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.dynamic_linear_layout_task.view.*


class ScrollingViewTasks : AppCompatActivity() {

    var background_tint : Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling_view_tasks)

        addTask("name 1", "asdasd")
        addTask("name 2", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
        addTask("name 3", "asdasd")
        addTask("name 4", "asdasd")
    }

    private fun addTask(name_str: String, desc_str: String){

        val inflater = applicationContext.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater
        val view: View = inflater.inflate(R.layout.dynamic_linear_layout_task, null)
        val container = findViewById(R.id.vertical_layout_view_1) as LinearLayout
        container.addView(view)
        view.textViewName.setText(name_str)
        if (background_tint) view.dynamic_linear_layout_base_task.setBackgroundColor(Color.parseColor("#EEEEEE"))
        background_tint = !background_tint

    }

}
