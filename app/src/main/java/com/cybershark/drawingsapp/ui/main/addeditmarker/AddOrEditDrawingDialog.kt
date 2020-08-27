package com.cybershark.drawingsapp.ui.main.addeditmarker

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.RoundedCornersTransformation
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.databinding.FragmentAddEditDrawingBinding
import com.cybershark.drawingsapp.ui.main.viewmodel.MainViewModel
import com.cybershark.drawingsapp.util.UIState
import com.cybershark.drawingsapp.util.longToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.properties.Delegates

@AndroidEntryPoint
class AddOrEditDrawingDialog : DialogFragment() {

    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentAddEditDrawingBinding
    private var drawingID by Delegates.notNull<Int>()
    private val itemToEdit by lazy { mainViewModel.getDrawingByID(drawingID) }
    private lateinit var newImageUri: Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddEditDrawingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null && arguments?.containsKey(EXTRAS_KEY) == true) {
            drawingID = requireArguments().getInt(EXTRAS_KEY)
            setupEditDialog()
        } else {
            setupAddDialog()
        }
        setupLiveData()
    }

    private fun setupLiveData() {
        mainViewModel.uiState.observe(viewLifecycleOwner){ uiState: UIState? ->
            when(uiState){
                is UIState.COMPLETED -> {
                    context?.longToast(uiState.message)
                    this.dismiss()
                }
                is UIState.ERROR -> {
                    context?.longToast(uiState.message)
                    this.dismiss()
                }
            }
        }
    }

    private fun setupAddDialog() {
        binding.ivDrawing.load(R.drawable.ic_attach_image)
        setupAddListeners()
    }

    private fun setupAddListeners() {
        binding.ivDrawing.setOnClickListener {
            newImageUri = getNewImageUriFromGallery()
            binding.ivDrawing.load(newImageUri) {
                error(R.drawable.ic_error)
                transformations(RoundedCornersTransformation(4f))
            }
        }
        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            if (!::newImageUri.isInitialized) {
                context?.longToast("No image Added!")
            } else {
                mainViewModel.insertDrawing(
                    title = binding.etDrawingTitle.text.toString(),
                    uri = newImageUri,
                    date = Date()
                )
            }
        }
    }

    private fun setupEditDialog() {
        binding.etDrawingTitle.setText(itemToEdit.title)
        binding.ivDrawing.load(itemToEdit.imageURI) {
            error(R.drawable.ic_error)
            transformations(RoundedCornersTransformation(4f))
        }
        newImageUri = itemToEdit.imageURI
        setupEditListeners()
    }

    private fun setupEditListeners() {
        binding.ivDrawing.setOnClickListener {
            newImageUri = getNewImageUriFromGallery()
            binding.ivDrawing.load(newImageUri) {
                error(R.drawable.ic_error)
                transformations(RoundedCornersTransformation(4f))
            }
        }
        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            val updatedDrawingEntity = itemToEdit.copy(
                title = binding.etDrawingTitle.text.toString(),
                imageURI = newImageUri,
                timeAdded = Date()
            )
            mainViewModel.updateDrawing(updatedDrawingEntity)
        }
    }

    private fun getNewImageUriFromGallery(): Uri {
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "FragmentAddMarker"
        private const val EXTRAS_KEY = "drawingID"
        const val EDIT = true
        const val ADD = false

        fun getInstance(action: Boolean, id: Int = 0): AddOrEditDrawingDialog {
            return if (action == EDIT) {
                val args = Bundle().apply {
                    putInt(EXTRAS_KEY, id)
                }
                AddOrEditDrawingDialog().apply {
                    arguments = args
                }
            } else {
                AddOrEditDrawingDialog()
            }
        }
    }
}