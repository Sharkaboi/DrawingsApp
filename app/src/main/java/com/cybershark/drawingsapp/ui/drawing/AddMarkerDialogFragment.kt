package com.cybershark.drawingsapp.ui.drawing

import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.drawingsapp.databinding.FragmentAddEditMarkerBinding
import com.cybershark.drawingsapp.ui.drawing.adapters.ImagesAdapter
import com.cybershark.drawingsapp.ui.drawing.viewmodel.DrawingViewModel
import com.cybershark.drawingsapp.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class AddMarkerDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentAddEditMarkerBinding
    private val drawingViewModel by viewModels<DrawingViewModel>()
    private lateinit var coordinatePoints: PointF
    private var drawingId by Delegates.notNull<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddEditMarkerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgs()
        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        val adapter = ImagesAdapter()
        binding.rvImages.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
        }
        drawingViewModel.listOfImagesRefreshState.observe(viewLifecycleOwner) { shouldRefresh ->
            if (shouldRefresh) {
                adapter.submitList(drawingViewModel.listOfImages)
                binding.rvImages.isVisible = drawingViewModel.listOfImages.isNotEmpty()
            }
        }
    }

    private fun getArgs() {
        if (arguments != null) {
            coordinatePoints = PointF(
                requireArguments().getFloat(X_COORDINATE_KEY),
                requireArguments().getFloat(Y_COORDINATE_KEY)
            )
            drawingId = requireArguments().getInt(DRAWING_ID_KEY)
        }
    }

    private fun setupListeners() {
        binding.btnConfirm.setOnClickListener { confirmMarker() }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnAttachImages.setOnClickListener { getImagesFromGallery() }
    }

    private fun getImagesFromGallery() {
        TODO("Get images from gallery and store in list in viewmodel")
    }

    private fun confirmMarker() {
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
                drawingViewModel.insertMarker(
                    coordinatePoints.x,
                    coordinatePoints.y,
                    binding.etMarkerTitle.text.toString(),
                    binding.etAssigneeName.text.toString(),
                    binding.etMarkerDescription.text.toString(),
                    drawingId,
                    binding.etRemarks.text.toString()
                )
                dismiss()
            }
        }
    }

    companion object {
        const val X_COORDINATE_KEY = "xCoor"
        const val Y_COORDINATE_KEY = "yCoor"
        const val DRAWING_ID_KEY = "drawingID"

        fun instance(sourceCoordinates: PointF, drawingId: Int): AddMarkerDialogFragment {
            val args = Bundle().apply {
                putFloat(X_COORDINATE_KEY, sourceCoordinates.x)
                putFloat(Y_COORDINATE_KEY, sourceCoordinates.y)
                putInt(DRAWING_ID_KEY, drawingId)
            }
            return AddMarkerDialogFragment().apply {
                arguments = args
            }
        }

        const val TAG = "AddMarkerDialogFragment"
    }
}