package com.cybershark.drawingsapp.ui.drawing

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.data.models.MarkerEntity
import com.cybershark.drawingsapp.databinding.ActivityDrawingDetailedBinding
import com.cybershark.drawingsapp.ui.drawing.adapters.ImagesAdapter
import com.cybershark.drawingsapp.ui.drawing.viewmodel.DrawingViewModel
import com.cybershark.drawingsapp.util.UIState
import com.cybershark.drawingsapp.util.longToast
import com.cybershark.drawingsapp.util.shortToast
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        //observer ui state to show progress bar
        drawingViewModel.uiState.observe(this) { uiState ->
            when (uiState) {
                is UIState.ERROR -> {
                    binding.contentLoading.isGone = true
                    this.longToast(uiState.message)
                }
                is UIState.LOADING -> binding.contentLoading.isVisible = true
                is UIState.COMPLETED -> {
                    binding.contentLoading.isGone = true
                    this.shortToast(uiState.message)
                }
            }
        }
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


    // SubsamplingScaleImageView.OnImageEventListener Functions to show loading screen
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

    // View.OnTouchListener methods to detect double and singe touch
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    // Custom gesture detector for detecting double and single tap and getting coordinates.
    private val gestureDetector by lazy {
        GestureDetector(this, object : SimpleOnGestureListener() {

            // Single touch detected
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (binding.imageView.isReady) {
                    val tappedPoint = binding.imageView.viewToSourceCoord(e.x, e.y)!!

                    drawingViewModel.listOfMarkers.value?.forEach { existingMarker ->
                        //Checking if marker clicked
                        if (abs(tappedPoint.x - existingMarker.markerPositionX) == 10f && abs(tappedPoint.y - existingMarker.markerPositionY) == 10f) {
                            Log.d(TAG, "onSingleTapConfirmed: existing $existingMarker")
                            Log.d(TAG, "onSingleTapConfirmed: tapped $tappedPoint")
                            this@DrawingDetailedActivity.shortToast("Clicked!")
                            openBottomSheet(existingMarker)
                        }
                    }
                } else {
                    this@DrawingDetailedActivity.shortToast("Please wait till the image loads.")
                }
                return true
            }

            // Double tap occurs
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

    // Making bottom sheet visible, setting data to bottom sheet
    private fun openBottomSheet(existingMarker: MarkerEntity) {
        binding.bottomSheetRoot.bottomSheetMarkerSV.isVisible = true
        // Observing livedata to refresh when updated
        drawingViewModel.listOfMarkers.observe(this) { list ->
            val markerEntityFromLiveData = list.first { it.markerID == existingMarker.markerID }
            binding.bottomSheetRoot.tvMarkerTitle.text = markerEntityFromLiveData.title
            binding.bottomSheetRoot.tvMarkerDescription.text = markerEntityFromLiveData.description
            binding.bottomSheetRoot.tvMarkerAssignee.text = markerEntityFromLiveData.assignee
            binding.bottomSheetRoot.tvMarkerRemarks.text = markerEntityFromLiveData.remarks
            drawingViewModel.getMarkerImagesByID(markerEntityFromLiveData.markerID)
            setupImagesRecyclerView(markerEntityFromLiveData.markerID)
            setupListenersForBottomSheet(markerEntityFromLiveData.markerID)
        }
    }

    // setup Popup menu with destination functions/
    private fun setupListenersForBottomSheet(markerID: Int) {
        binding.bottomSheetRoot.ibMenu.setOnClickListener { anchor: View ->
            val popup = PopupMenu(anchor.context, anchor)
            popup.menuInflater.inflate(R.menu.popup_menu_marker, popup.menu)
            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.item_edit_marker) openEditDialog(markerID)
                else if (it.itemId == R.id.item_delete_marker) confirmMarkerDelete(markerID)
                return@setOnMenuItemClickListener true
            }
            popup.show()

        }
    }

    // Confirm delete of marker with alert dialog.
    private fun confirmMarkerDelete(markerID: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_marker)
            .setMessage(R.string.irreversible)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                drawingViewModel.deleteMarker(markerID)
            }
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Open dialog fragment to edit data
    private fun openEditDialog(markerID: Int) {
        EditMarkerDialogFragment.instance(markerID, drawingId).show(supportFragmentManager, EditMarkerDialogFragment.TAG)
    }

    // Setup choosen images from livedata
    private fun setupImagesRecyclerView(markerID: Int) {
        val adapter = ImagesAdapter()
        binding.bottomSheetRoot.rvImages.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
        }
        drawingViewModel.listOfImagesByMarkerID.observe(this) { listOfMarkerImages ->
            if (listOfMarkerImages.isNullOrEmpty()) {
                binding.bottomSheetRoot.rvImages.isVisible = false
            } else {
                adapter.submitList(listOfMarkerImages.map { it.imageURI })
                binding.bottomSheetRoot.rvImages.isVisible = true
            }
        }
    }

    // Open dialog fragment fo adding data
    private fun openAddMarkerDialog(sourceCoordinates: PointF, drawingId: Int) {
        AddMarkerDialogFragment.instance(sourceCoordinates, drawingId).show(supportFragmentManager, AddMarkerDialogFragment.TAG)
    }

    // Settings custom animations
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
}