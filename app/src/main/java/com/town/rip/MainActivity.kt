package com.town.rip

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
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
import com.town.rip.database.Profile
import com.town.rip.database.ProfileViewModel
import com.town.rip.database.Task
import com.town.rip.database.TaskViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var tasksList: List<Task>
    private var profileList: List<Profile> = listOf()
    private var mutableProfileList: MutableList<String> = mutableListOf()
    private var profileOptions = 1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        setContentView(R.layout.activity_main)
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { taskViewModel.repository.allTasks }
            tasksList = taskViewModel.allTasks.value!!
            profileViewModel.allProfiles.observe(this, Observer {
                profileList = profileViewModel.allProfiles.value!!
                taskViewModel.allTasks.removeObservers(this)
                rebuildMutableProfileList()
                if(profileList.isNotEmpty()) {
                    if (profileList.filter { it.selected }.isEmpty()) {
                        var profile = profileList.first()
                        profile.selected = true
                        profileViewModel.update(profile)
                    }
                    buttonGenerate.isEnabled = false
                    for(task in taskViewModel.allTasks.value!!)
                        if(task.profile_ids.contains(profileList.last { it.selected }.id) && task.enabled) {
                            buttonGenerate.isEnabled = true
                            break
                        }
                    buttonProfile.text = "Profile: ${profileList.last { it.selected }.name}"
                }

                taskViewModel.allTasks.observe(this, Observer { tasks ->
                    tasks?.let { taskViewModel.repository.allTasks }
                    tasksList = taskViewModel.allTasks.value!!
                    buttonGenerate.isEnabled = false
                    for(task in taskViewModel.allTasks.value!!)
                        if(task.profile_ids.contains(profileList.last { it.selected }.id) && task.enabled) {
                            buttonGenerate.isEnabled = true
                            break
                        }
                })
            })
        })


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

    fun AlertDialog.Builder.setEditText(editText: EditText, textHint:String = ""): AlertDialog.Builder {
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
            Log.v("list id", which.toString());
           if(which < mutableProfileList.size - (1+profileOptions)) {
               var profile = profileList.last { it.selected }
               profile.selected = false
               profileViewModel.update(profile)
               profile = profileList[which]
               profile.selected = true
               profileViewModel.update(profile)
               buttonProfile.text = "Profile: " + profile.name
           }else if (which == mutableProfileList.size - profileOptions){
               val editText = EditText(this)
               AlertDialog.Builder(this)
                   .setTitle("Create Profile")
                   .setEditText(editText, "profile name")
                   .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                       // Do your work with text here
                       val text = editText.text.toString()
                       showVerticalToast("Profile $text created")
                       var profile = Profile(text, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                           Date()
                       ), false)
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
                   Log.v("list id", which.toString());
                   var profile = profileList.last { it.name == mutableProfileList[which] }
                   profileViewModel.delete(profile)

                   for(task in tasksList.filter { it.profile_id == profile.id })
                       taskViewModel.delete(task)

                   if(profileList.none { it.selected }) {
                       profile = profileList.first()
                       profile.selected = true
                       profileViewModel.update(profile)
                   }
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
        toast.setGravity(Gravity.CENTER_VERTICAL, 0,0)
        toast.show()
        if(debugBoolean) Log.d("", s)

    }

    fun viewTasks(view: View) {
        val intent = Intent(this, ScrollingViewTask::class.java).apply {
           // putExtras(extras)
        }
        startActivity(intent)
    }

    fun generateTasks(view: View) {
        val intent = Intent(this, GeneratedTaskListActivity::class.java).apply {
            // putExtras(extras)
        }
        startActivity(intent)
    }

    fun addTask(view: View) {


        val intent = Intent(this, EditTask::class.java).apply {
            // putExtras(extras)
        }
        startActivity(intent)
    }


}
