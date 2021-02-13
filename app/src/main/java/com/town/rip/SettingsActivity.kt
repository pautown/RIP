package com.town.rip

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_scrolling_view_tasks.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.dynamic_view_profile.view.*
import java.util.*


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
        var roundAmount = 0
        themeInt = pref.getInt("THEME", -1);
        vibrationBool = pref.getBoolean("VIBRATION", true)
        pushBool = pref.getBoolean("PUSH", false)
        eitherOrBool = pref.getBoolean("EITHEROR", false)
        socialMediaPromptBool = pref.getBoolean("SOCIALMEDIAPROMPT", false)
        roundGeneratedBool = pref.getBoolean("ROUNDGENERATED", false)
        roundAmount = pref.getInt("ROUNDAMOUNT", 5)

        if(roundAmount == null)
        {
            roundAmount = 5
            editor.putInt("ROUNDAMOUNT", roundAmount)
            editor.commit();
        }

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
            if(vibrationBool && pushBool) linearLayoutVibration.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP
            )

        }

        eitherOrCheckbox.isChecked = eitherOrBool
        linearLayoutEitherOr.setOnClickListener{
            eitherOrBool = !eitherOrBool
            eitherOrCheckbox.isChecked = eitherOrBool
            editor.putBoolean("EITHEROR", eitherOrBool)
            editor.commit()
            if(vibrationBool && eitherOrBool) linearLayoutVibration.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP
            )

        }

        progressPromptCheckbox.isChecked = socialMediaPromptBool
        linearLayoutSocial.setOnClickListener{
            socialMediaPromptBool = !socialMediaPromptBool
            progressPromptCheckbox.isChecked = socialMediaPromptBool
            editor.putBoolean("SOCIALMEDIAPROMPT", socialMediaPromptBool)
            editor.commit()
            if(vibrationBool && socialMediaPromptBool) linearLayoutVibration.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP
            )

        }

        roundGeneratedCheckbox.isChecked = roundGeneratedBool
        linearLayoutRound.setOnClickListener{
            roundGeneratedBool = !roundGeneratedBool
            roundGeneratedCheckbox.isChecked = roundGeneratedBool
            editor.putBoolean("ROUNDGENERATED", roundGeneratedBool)
            editor.commit()
            if(vibrationBool && roundGeneratedBool) linearLayoutVibration.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP
            )
        }


        textViewRoundInput.setText(roundAmount.toString())
        textViewRoundInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) {
                    editor.putInt("ROUNDAMOUNT", textViewRoundInput.text.toString().toInt())
                    editor.commit()
                }
            }
        })



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

        backgroundLayout.setBackgroundColor(Color.parseColor(backgroundString))


        var arraylistText = ArrayList<TextView>()
        arraylistText.add(textViewHeader)
        arraylistText.add(textViewVibration)
        arraylistText.add(textViewPush)
        arraylistText.add(textViewEitherOr)
        arraylistText.add(textViewProgressPrompt)
        arraylistText.add(textViewRoundActivities)
        arraylistText.add(textViewAbove)
        arraylistText.add(textViewRoundInput)

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
        buttonBack.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor(
                buttonBackgroundString
            )
        )

    }


    fun returnToMenu(view: View) {
        finish()
    }
}