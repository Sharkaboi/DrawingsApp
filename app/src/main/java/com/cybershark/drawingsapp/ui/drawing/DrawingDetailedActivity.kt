package com.cybershark.drawingsapp.ui.drawing

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.databinding.ActivityDrawingDetailedBinding
import com.cybershark.drawingsapp.ui.drawing.viewmodel.DrawingViewModel
import com.cybershark.drawingsapp.util.longToast
import com.cybershark.drawingsapp.util.shortToast
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates


@AndroidEntryPoint
class DrawingDetailedActivity : AppCompatActivity(), SubsamplingScaleImageView.OnImageEventListener, View.OnTouchListener {

    private lateinit var binding: ActivityDrawingDetailedBinding
    private var drawingId by Delegates.notNull<Int>()
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
        loadImage()
        setLiveData()
    }

    private fun loadImage() {
        binding.contentLoading.isVisible = true
        drawingViewModel.currentDrawing.observe(this) { currentDrawing ->
            binding.imageView.setImage(ImageSource.uri(currentDrawing.imageURI))
        }
        binding.imageView.setOnImageEventListener(this)
        binding.imageView.setOnTouchListener(this)
        binding.imageView.setMinimumDpi(100)
    }

    private fun setLiveData() {
//        TODO("Not yet implemented")
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private val gestureDetector by lazy {
        GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (binding.imageView.isReady) {
                    val sourceCoordinates = binding.imageView.viewToSourceCoord(e.x, e.y)!!
                    Log.d(TAG, "onSingleTapConfirmed: $sourceCoordinates")
                    //TODO("Implement check with markers to see if marker already there if yes open bottomsheet")
                } else {
                    this@DrawingDetailedActivity.shortToast("Please wait till the image loads.")
                }
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {

                if (binding.imageView.isReady) {
                    val sourceCoordinates = binding.imageView.viewToSourceCoord(e.x, e.y)!!
                    Log.d(TAG, "onDoubleTap: $sourceCoordinates")
                    //TODO("Add new marker in given x and y")
                } else {
                    this@DrawingDetailedActivity.shortToast("Please wait till the image loads.")
                }
                return super.onDoubleTap(e)
            }
        })
    }
}