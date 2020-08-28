package com.cybershark.drawingsapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.cybershark.drawingsapp.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkOrLightTheme()
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    private fun setDarkOrLightTheme() {
        val themeOption =
            PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkTheme", false)
        if (themeOption)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}