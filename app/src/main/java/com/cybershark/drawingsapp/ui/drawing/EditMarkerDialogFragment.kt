package com.cybershark.drawingsapp.ui.drawing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.drawingsapp.data.models.MarkerEntity
import com.cybershark.drawingsapp.databinding.FragmentAddEditMarkerBinding
import com.cybershark.drawingsapp.ui.drawing.adapters.ImagesAdapter
import com.cybershark.drawingsapp.ui.drawing.viewmodel.DrawingViewModel
import com.cybershark.drawingsapp.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class EditMarkerDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentAddEditMarkerBinding
    private val drawingViewModel by viewModels<DrawingViewModel>()
    private var drawingId by Delegates.notNull<Int>()
    private var markerID by Delegates.notNull<Int>()
    private lateinit var currentMarker: MarkerEntity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddEditMarkerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgs()
        setData()
        setupRecyclerView()
        setupListeners()
    }

    // Sets up listeners for click events
    private fun setupListeners() {
        binding.btnConfirm.setOnClickListener { updateMarker() }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnAttachImages.setOnClickListener { getImagesFromImagePicker() }
    }

    // Gets Images From Imagepicker to attach
    private fun getImagesFromImagePicker() {
        TODO("add images to list of images")
    }

    // Updates marker if condition satisfied
    private fun updateMarker() {
        when {
            binding.etMarkerTitle.text.isNullOrBlank() -> {
                context?.shortToast("Enter Title!")
            }
            binding.etMarkerDescription.text.isNullOrBlank() -> {
                context?.shortToast("Enter description!")
            }
            binding.etAssigneeName.text.isNullOrBlank() -> {
                context?.shortToast("Enter Assignee!")
            }
            else -> {
                drawingViewModel.updateMarker(MarkerEntity(
                    markerPositionX = currentMarker.markerPositionX,
                    markerPositionY = currentMarker.markerPositionY,
                    title = binding.etMarkerTitle.text.toString(),
                    assignee = binding.etAssigneeName.text.toString(),
                    description = binding.etMarkerDescription.text.toString(),
                    drawingID = drawingId,
                    remarks = binding.etRemarks.text.toString()
                ))
                dismiss()
            }
        }
    }

    // Sets up marker images recycler view from livedata
    private fun setupRecyclerView() {
        val adapter = ImagesAdapter()
        binding.rvImages.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
        }
        drawingViewModel.listOfImagesByMarkerID.observe(this) { listOfMarkerImages ->
            if (listOfMarkerImages.isNullOrEmpty()) {
                binding.rvImages.isVisible = false
            } else {
                adapter.submitList(listOfMarkerImages.map { it.imageURI })
                binding.rvImages.isVisible = true
            }
        }
    }

    // sets data of marker to show in dialog
    private fun setData() {
        drawingViewModel.getMarkerByID(markerID)?.let { currentMarker = it }
        binding.apply {
            etMarkerTitle.setText(currentMarker.title)
            etMarkerDescription.setText(currentMarker.description)
            etAssigneeName.setText(currentMarker.assignee)
            etRemarks.setText(currentMarker.remarks)
        }
    }

    // Gets marker and drawing id from arguments
    private fun getArgs() {
        if (arguments != null) {
            markerID = requireArguments().getInt(EditMarkerDialogFragment.MARKER_ID_KEY)
            drawingId = requireArguments().getInt(AddMarkerDialogFragment.DRAWING_ID_KEY)
        }
    }

    companion object {
        const val MARKER_ID_KEY = "markerID"
        const val DRAWING_ID_KEY = "drawingID"

        fun instance(markerID: Int, drawingId: Int): AddMarkerDialogFragment {
            val args = Bundle().apply {
                putInt(MARKER_ID_KEY, markerID)
                putInt(DRAWING_ID_KEY, drawingId)
            }
            return AddMarkerDialogFragment().apply {
                arguments = args
            }
        }

        const val TAG = "EditMarkerDialogFragment"
    }
}