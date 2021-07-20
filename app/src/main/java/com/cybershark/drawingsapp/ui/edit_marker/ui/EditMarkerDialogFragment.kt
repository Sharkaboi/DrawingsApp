package com.cybershark.drawingsapp.ui.edit_marker.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.drawingsapp.data.room.entities.MarkerEntity
import com.cybershark.drawingsapp.databinding.FragmentEditMarkerBinding
import com.cybershark.drawingsapp.ui.drawing.adapters.ImagesAdapter
import com.cybershark.drawingsapp.ui.drawing.viewmodel.DrawingViewModel
import com.cybershark.drawingsapp.ui.add_edit_drawing.ui.AddOrEditDrawingDialog
import com.cybershark.drawingsapp.ui.add_marker.ui.AddMarkerDialogFragment
import com.cybershark.drawingsapp.util.UIState
import com.cybershark.drawingsapp.util.longToast
import com.cybershark.drawingsapp.util.shortToast
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class EditMarkerDialogFragment : DialogFragment() {

    private val drawingViewModel by viewModels<DrawingViewModel>()
    private var drawingId by Delegates.notNull<Int>()
    private var markerID by Delegates.notNull<Int>()
    private lateinit var currentMarker: MarkerEntity
    private var _binding: FragmentEditMarkerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditMarkerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: edit dialog created")
        getArgs()
        setData()
        setupRecyclerView()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Gets marker and drawing id from arguments
    private fun getArgs() {
        if (arguments != null) {
            markerID = requireArguments().getInt(MARKER_ID_KEY)
            drawingId = requireArguments().getInt(DRAWING_ID_KEY)
        }
    }

    // sets data of marker to show in dialog
    private fun setData() {
        drawingViewModel.listOfMarkers.observe(viewLifecycleOwner) { listOfMarkers ->
            // filters through markers and gets current marker
            currentMarker = listOfMarkers.first { it.markerID == markerID }
            binding.apply {
                etMarkerTitle.setText(currentMarker.title)
                etMarkerDescription.setText(currentMarker.description)
                etAssigneeName.setText(currentMarker.assignee)
                etRemarks.setText(currentMarker.remarks)
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
        drawingViewModel.listOfMarkerImagesFromRoom.observe(viewLifecycleOwner) { listOfImagesFromRoom ->
            val filteredList = listOfImagesFromRoom.filter { it.markerID == markerID }
            val fullList = filteredList.map { it.imageURI }.plus(drawingViewModel.listOfImages.value ?: emptyList())
            binding.rvImages.isVisible = fullList.isNotEmpty()
            adapter.submitList(fullList)
        }
        drawingViewModel.listOfImages.observe(viewLifecycleOwner) { tempListOfImages ->
            val filteredList = drawingViewModel.listOfMarkerImagesFromRoom.value?.filter {
                it.markerID == markerID
            } ?: emptyList()
            val fullList = tempListOfImages.plus(filteredList.map { it.imageURI })
            binding.rvImages.isVisible = fullList.isNotEmpty()
            adapter.submitList(fullList)
        }
    }

    // Sets up listeners for click events
    private fun setupListeners() {
        binding.btnConfirm.setOnClickListener { updateMarker() }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnAttachImages.setOnClickListener { getImagesFromImagePicker() }

        // Ui state for giving messages
        drawingViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIState.COMPLETED -> {
                    context?.shortToast(uiState.message)
                    dismiss()
                }
                is UIState.ERROR -> {
                    context?.longToast(uiState.message)
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    // Gets Images From Imagepicker to attach
    private fun getImagesFromImagePicker() {
        ImagePicker.with(this)
            .crop()
            .start()
    }

    // ImagePicker intent result.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                // Gets image file
                val file = ImagePicker.getFile(data)!!
                Log.d(AddMarkerDialogFragment.TAG, "onActivityResult: $file")
                // inserts original uri to temporary list.
                drawingViewModel.insertMarkerImage(file.toUri())
            }
            ImagePicker.RESULT_ERROR -> {
                Log.d(AddOrEditDrawingDialog.TAG, "onActivityResult: ${ImagePicker.getError(data)}")
                context?.longToast("Error getting image.")
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
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
                drawingViewModel.updateMarker(
                    MarkerEntity(
                        markerID = markerID,
                        markerPositionX = currentMarker.markerPositionX,
                        markerPositionY = currentMarker.markerPositionY,
                        title = binding.etMarkerTitle.text.toString(),
                        assignee = binding.etAssigneeName.text.toString(),
                        description = binding.etMarkerDescription.text.toString(),
                        drawingID = drawingId,
                        remarks = binding.etRemarks.text.toString()
                    )
                )
            }
        }
    }

    companion object {
        private const val MARKER_ID_KEY = "markerID"
        private const val DRAWING_ID_KEY = "drawingID"

        fun instance(markerID: Int, drawingId: Int): EditMarkerDialogFragment {
            val args = Bundle().apply {
                putInt(MARKER_ID_KEY, markerID)
                putInt(DRAWING_ID_KEY, drawingId)
            }
            return EditMarkerDialogFragment().apply {
                arguments = args
            }
        }

        const val TAG = "EditMarkerDialog"
    }
}