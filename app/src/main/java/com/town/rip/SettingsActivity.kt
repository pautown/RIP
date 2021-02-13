package com.town.rip

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.constraintLayout
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private var backgroundString = "#FFFFFF"
    private var backgroundStringLight = "#FFFFFF"
    private var buttonBackgroundString = "#ECEBEB"
    private var buttonTextString = "#000000"
    private var themeInt: Int = 0

    private var vibrationBool:Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        loadSharedPrefs()

    }

    private fun loadSharedPrefs() {
        val pref = applicationContext.getSharedPreferences("app", 0) // 0 - for private mode
        val editor = pref.edit()
        themeInt = pref.getInt("THEME", -1);
        vibrationBool = pref.getBoolean("VIBRATION", true)

        if(themeInt == null)
        {
            themeInt = 0
            editor.putInt("THEME", themeInt)
            editor.commit();
        }

        if(vibrationBool == null)
        {
            vibrationBool = true
            editor.putBoolean("VIBRATION", vibrationBool)
            editor.commit();
        }
        vibrationCheckbox.isChecked = vibrationBool
        linearLayoutVibration.setOnClickListener{
            vibrationBool = !vibrationBool
            vibrationCheckbox.isChecked = vibrationBool
            editor.putBoolean("VIBRATION", vibrationBool)
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
        //constraintLayout.setBackgroundColor(Color.parseColor(backgroundString))

        //textView5.setTextColor(Color.parseColor(buttonTextString))
       // textViewSubheading.setTextColor(Color.parseColor(buttonTextString))
       // textViewViewTextsLoadingMessage.setTextColor(Color.parseColor(buttonTextString))

       // buttonActivities.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
       // buttonActivities.setTextColor(Color.parseColor(buttonTextString))
    }


    fun returnToMenu(view: View) {
        finish()
    }
}