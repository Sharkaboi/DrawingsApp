package com.cybershark.drawingsapp.ui.drawing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.data.models.MarkerEntity
import com.cybershark.drawingsapp.data.models.MarkerImagesEntity
import com.cybershark.drawingsapp.databinding.MarkerBottomsheetBinding
import com.cybershark.drawingsapp.ui.drawing.adapters.ImagesAdapter
import com.cybershark.drawingsapp.ui.drawing.viewmodel.DrawingViewModel
import com.cybershark.drawingsapp.util.UIState
import com.cybershark.drawingsapp.util.longToast
import com.cybershark.drawingsapp.util.shortToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class MarkerBottomSheet : BottomSheetDialogFragment() {

    private val drawingViewModel by viewModels<DrawingViewModel>()
    private var markerID by Delegates.notNull<Int>()
    private var drawingID by Delegates.notNull<Int>()
    private lateinit var imagesObserver: Observer<List<MarkerImagesEntity>>
    private lateinit var markerObserver: Observer<List<MarkerEntity>>
    private var _binding: MarkerBottomsheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MarkerBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgs()
        setDataFromLiveData()
        setupRecyclerView()
        setListeners()
    }

    // gets id's from arguments.
    private fun getArgs() {
        if (arguments != null) {
            drawingID = requireArguments().getInt(DRAWING_ID_KEY)
            markerID = requireArguments().getInt(MARKER_ID_KEY)
        }
    }

    // Observing live data to refresh when updated
    private fun setDataFromLiveData() {
        markerObserver = Observer { list ->
            val markerEntityFromLiveData = list.first { it.markerID == markerID }
            binding.tvMarkerTitle.text = markerEntityFromLiveData.title
            binding.tvMarkerDescription.text = markerEntityFromLiveData.description
            binding.tvMarkerAssignee.text = markerEntityFromLiveData.assignee
            binding.tvMarkerRemarks.text = markerEntityFromLiveData.remarks
            binding.tvRemarksHint.isVisible = markerEntityFromLiveData.remarks.isNotBlank()
            binding.tvMarkerRemarks.isVisible = markerEntityFromLiveData.remarks.isNotBlank()
        }
        drawingViewModel.listOfMarkers.observe(viewLifecycleOwner, markerObserver)

        // Close bottom sheet on finish task
        drawingViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIState.COMPLETED -> {
                    context?.shortToast(uiState.message)
                    dismiss()
                }
                is UIState.ERROR -> {
                    context?.longToast(uiState.message)
                    dismiss()
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun setupRecyclerView() {
        // Setup chosen images from live data
        val adapter = ImagesAdapter()
        binding.rvImages.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
        }
        imagesObserver = Observer { listOfMarkerImages ->
            val filteredList = listOfMarkerImages.filter { it.markerID == markerID }
            binding.rvImages.isVisible = filteredList.isNotEmpty()
            binding.tvImagesHint.isVisible = filteredList.isNotEmpty()
            adapter.submitList(filteredList.map { it.imageURI })
        }
        drawingViewModel.listOfMarkerImagesFromRoom.observe(viewLifecycleOwner, imagesObserver)
    }

    // setup Popup menu with destination functions.
    private fun setListeners() {
        binding.ibMenu.setOnClickListener { anchor: View ->
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

    // Open dialog fragment to edit data
    private fun openEditDialog(markerID: Int) {
        EditMarkerDialogFragment.instance(markerID, drawingID).show(parentFragmentManager, EditMarkerDialogFragment.TAG)
    }

    // Confirm delete of marker with alert dialog.
    private fun confirmMarkerDelete(markerID: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_marker)
            .setMessage(R.string.irreversible)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                drawingViewModel.listOfMarkerImagesFromRoom.removeObserver(imagesObserver)
                drawingViewModel.listOfMarkers.removeObserver(markerObserver)
                drawingViewModel.deleteMarker(markerID, drawingID)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    companion object {
        fun instance(markerID: Int, drawingID: Int): MarkerBottomSheet {
            val args = Bundle().apply {
                putInt(MARKER_ID_KEY, markerID)
                putInt(DRAWING_ID_KEY, drawingID)
            }
            return MarkerBottomSheet().apply {
                arguments = args
            }
        }

        const val TAG = "MarkerBottomSheet"
        private const val MARKER_ID_KEY = "markerID"
        private const val DRAWING_ID_KEY = "drawingID"
    }
}