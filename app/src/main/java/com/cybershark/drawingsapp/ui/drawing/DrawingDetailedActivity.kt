package com.cybershark.drawingsapp.ui.drawing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cybershark.drawingsapp.databinding.ActivityDrawingDetailedBinding

class DrawingDetailedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingDetailedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}