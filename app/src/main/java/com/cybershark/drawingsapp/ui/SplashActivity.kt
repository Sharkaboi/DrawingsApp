package com.cybershark.drawingsapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        startActivity(Intent(this, MainActivity::class.java))
    }
}