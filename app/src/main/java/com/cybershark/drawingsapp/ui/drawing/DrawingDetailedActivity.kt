package com.cybershark.drawingsapp.ui.drawing

import android.R.id
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.databinding.ActivityDrawingDetailedBinding
import com.cybershark.drawingsapp.ui.drawing.viewmodel.DrawingViewModel
import com.cybershark.drawingsapp.util.longToast
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import kotlin.properties.Delegates


@AndroidEntryPoint
class DrawingDetailedActivity : AppCompatActivity(),SubsamplingScaleImageView.OnImageEventListener {

    private lateinit var binding: ActivityDrawingDetailedBinding
    private var drawingId by Delegates.notNull<Int>()
    private lateinit var currentDrawing: DrawingEntity
    private val drawingViewModel by viewModels<DrawingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        if (bundle != null) {
            drawingId = bundle.getInt(INTENT_ID_KEY)
        } else {
            finish()
            longToast("Error retrieving the drawing, Try again!")
        }

        currentDrawing = drawingViewModel.getDrawingByID(drawingId)
        binding.contentLoading.isVisible = true
        binding.imageView.setImage(ImageSource.uri(currentDrawing.imageURI))
        binding.imageView.setOnImageEventListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setCustomAnims()
    }

    private fun setCustomAnims() = overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    companion object {
        const val TAG = "DrawingDetailedActivity"
        const val INTENT_ID_KEY = "drawingID"
        fun getIntent(context: Context, drawingId: Int): Intent {
            val intent = Intent(context, DrawingDetailedActivity::class.java)
            intent.putExtra(INTENT_ID_KEY, drawingId)
            return intent
        }
    }

    override fun onReady() {
        binding.contentLoading.isGone = true
    }

    override fun onImageLoaded() {
        //NOTHING
    }

    override fun onPreviewLoadError(e: Exception?) {
        //NOTHING
    }

    override fun onImageLoadError(e: Exception?) {
        this.longToast("Error Loading Image")
        binding.imageView.setImage(ImageSource.resource(R.drawable.ic_error))
    }

    override fun onTileLoadError(e: Exception?) {
        this.longToast("Error Loading Image")
        binding.imageView.setImage(ImageSource.resource(R.drawable.ic_error))
    }

    override fun onPreviewReleased() {
        //NOTHING
    }
}