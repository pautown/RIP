package com.town.rip

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.constraintLayout
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.dynamic_view_profile.view.*
import java.util.ArrayList

class SettingsActivity : AppCompatActivity() {

    private var backgroundString = "#FFFFFF"
    private var backgroundStringLight = "#FFFFFF"
    private var buttonBackgroundString = "#ECEBEB"
    private var buttonTextString = "#000000"
    private var themeInt: Int = 0

    private var vibrationBool:Boolean = true
    private var pushBool:Boolean = true
    private var eitherOrBool:Boolean = true
    private var socialMediaPromptBool:Boolean = true
    private var roundGeneratedBool:Boolean = true


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
        pushBool = pref.getBoolean("PUSH", false)
        eitherOrBool = pref.getBoolean("EITHEROR", false)
        socialMediaPromptBool = pref.getBoolean("SOCIALMEDIAPROMPT", false)
        roundGeneratedBool = pref.getBoolean("ROUNDGENERATED", false)

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
        if(pushBool == null)
        {
            pushBool = false
            editor.putBoolean("PUSH", pushBool)
            editor.commit();
        }
        if(eitherOrBool == null)
        {
            eitherOrBool = false
            editor.putBoolean("EITHEROR", eitherOrBool)
            editor.commit();
        }
        if(socialMediaPromptBool == null)
        {
            socialMediaPromptBool = false
            editor.putBoolean("SOCIALMEDIAPROMPT", socialMediaPromptBool)
            editor.commit();
        }
        if(roundGeneratedBool == null)
        {
            roundGeneratedBool = false
            editor.putBoolean("ROUNDGENERATED", roundGeneratedBool)
            editor.commit();
        }



        vibrationCheckbox.isChecked = vibrationBool
        linearLayoutVibration.setOnClickListener{
            vibrationBool = !vibrationBool
            vibrationCheckbox.isChecked = vibrationBool
            editor.putBoolean("VIBRATION", vibrationBool)
            editor.commit()
            if(vibrationBool) linearLayoutVibration.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }

        pushCheckbox.isChecked = pushBool
        linearLayoutPush.setOnClickListener{
            pushBool = !pushBool
            pushCheckbox.isChecked = pushBool
            editor.putBoolean("PUSH", pushBool)
            editor.commit()
            if(vibrationBool && pushBool) linearLayoutVibration.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

        }

        eitherOrCheckbox.isChecked = eitherOrBool
        linearLayoutEitherOr.setOnClickListener{
            eitherOrBool = !eitherOrBool
            eitherOrCheckbox.isChecked = eitherOrBool
            editor.putBoolean("EITHEROR", eitherOrBool)
            editor.commit()
            if(vibrationBool && eitherOrBool) linearLayoutVibration.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

        }

        progressPromptCheckbox.isChecked = socialMediaPromptBool
        linearLayoutSocial.setOnClickListener{
            socialMediaPromptBool = !socialMediaPromptBool
            progressPromptCheckbox.isChecked = socialMediaPromptBool
            editor.putBoolean("SOCIALMEDIAPROMPT", socialMediaPromptBool)
            editor.commit()
            if(vibrationBool && socialMediaPromptBool) linearLayoutVibration.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

        }

        roundGeneratedCheckbox.isChecked = roundGeneratedBool
        linearLayoutRound.setOnClickListener{
            roundGeneratedBool = !roundGeneratedBool
            roundGeneratedCheckbox.isChecked = roundGeneratedBool
            editor.putBoolean("ROUNDGENERATED", roundGeneratedBool)
            editor.commit()
            if(vibrationBool && roundGeneratedBool) linearLayoutVibration.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

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

        backgroundLayout.setBackgroundColor(Color.parseColor(backgroundString))


        var arraylistText = ArrayList<TextView>()
        arraylistText.add(textViewHeader)
        arraylistText.add(textViewVibration)
        arraylistText.add(textViewPush)
        arraylistText.add(textViewEitherOr)
        arraylistText.add(textViewProgressPrompt)
        arraylistText.add(textViewRoundActivities)
        arraylistText.add(textViewAbove)
        arraylistText.add(textViewBy)
        arraylistText.add(textViewRoundInputAbove)
        arraylistText.add(textViewRoundInputBy)

        var arraylistCheckbox = ArrayList<CheckBox>()
        arraylistCheckbox.add(vibrationCheckbox)
        arraylistCheckbox.add(pushCheckbox)
        arraylistCheckbox.add(eitherOrCheckbox)
        arraylistCheckbox.add(progressPromptCheckbox)
        arraylistCheckbox.add(roundGeneratedCheckbox)

        for(textInput in arraylistText)
        {
            textInput.setTextColor(Color.parseColor(buttonTextString))
            textInput.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonTextString))
        }

        for(checkBox in arraylistCheckbox)
        {
            checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))
        }




        buttonBack.setTextColor(Color.parseColor(buttonTextString))
        buttonBack.backgroundTintList = ColorStateList.valueOf(Color.parseColor(buttonBackgroundString))

    }


    fun returnToMenu(view: View) {
        finish()
    }
}