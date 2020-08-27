package com.cybershark.drawingsapp.ui.drawing

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.data.models.MarkerEntity
import com.cybershark.drawingsapp.databinding.ActivityDrawingDetailedBinding
import com.cybershark.drawingsapp.ui.drawing.viewmodel.DrawingViewModel
import com.cybershark.drawingsapp.util.longToast
import com.cybershark.drawingsapp.util.shortToast
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs
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

        getDrawingIDFromIntent()
        loadImage()
        setLiveData()
    }

    private fun getDrawingIDFromIntent() {
        val bundle = intent.extras
        if (bundle != null) {
            drawingId = bundle.getInt(INTENT_ID_KEY)
        } else {
            finish()
            longToast("Error retrieving the drawing, Try again!")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadImage() {
        binding.contentLoading.isVisible = true
        binding.imageView.apply {
            drawingViewModel.currentDrawing.observe(this@DrawingDetailedActivity) { currentDrawing ->
                this.setImage(ImageSource.uri(currentDrawing.imageURI))
            }
            this.setOnImageEventListener(this@DrawingDetailedActivity)
            this.setOnTouchListener(this@DrawingDetailedActivity)
            this.setMinimumDpi(100)
            //set dp super high to disable zoom on double tap
            this.setDoubleTapZoomDpi(999999)
        }
    }

    private fun setLiveData() {
        // draws markers as soon as livedata updates
        drawingViewModel.listOfMarkers.observe(this) { listOfMarkers ->
            drawMarkers(listOfMarkers)
        }
    }

    private fun drawMarkers(listOfMarkers: List<MarkerEntity>) {
        // draws each marker in specified position
        val listOfPoints = listOfMarkers.map { marker ->
            PointF(marker.markerPositionX, marker.markerPositionY)
        }
        binding.imageView.setPins(listOfPoints)
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

    // SubsamplingScaleImageView.OnImageEventListener Functions
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
        finish()
    }

    override fun onTileLoadError(e: Exception?) {
        this.longToast("Error Loading Image")
        finish()
    }

    override fun onPreviewReleased() {
        //NOTHING
    }

    // View.OnTouchListener methods
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    // Custom gesture detector for detecting double and single tap and getting coordinates.
    private val gestureDetector by lazy {
        GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (binding.imageView.isReady) {
                    val tappedPoint = binding.imageView.viewToSourceCoord(e.x, e.y)!!
                    //Checking if marker clicked
                    drawingViewModel.listOfMarkers.value?.forEach { existingMarker ->
                        if (abs(tappedPoint.x - existingMarker.markerPositionX) == 10f && abs(tappedPoint.y - existingMarker.markerPositionY) == 10f) {
                            Log.d(TAG, "onSingleTapConfirmed: existing $existingMarker")
                            Log.d(TAG, "onSingleTapConfirmed: tapped $tappedPoint")
                            this@DrawingDetailedActivity.shortToast("Clicked!")
                        }

                    }
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
                    // passes coordinates to dialog, if confirmed; marker updated from livedata
                    openAddMarkerDialog(sourceCoordinates, drawingId)
                } else {
                    this@DrawingDetailedActivity.shortToast("Please wait till the image loads.")
                }
                return super.onDoubleTap(e)
            }
        })
    }

    private fun openAddMarkerDialog(sourceCoordinates: PointF, drawingId: Int) {
        AddMarkerDialogFragment.instance(sourceCoordinates, drawingId).show(supportFragmentManager, AddMarkerDialogFragment.TAG)
    }

}