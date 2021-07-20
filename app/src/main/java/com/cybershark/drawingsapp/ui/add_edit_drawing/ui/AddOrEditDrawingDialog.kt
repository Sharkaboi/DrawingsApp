package com.cybershark.drawingsapp.ui.add_edit_drawing.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.RoundedCornersTransformation
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.databinding.FragmentAddEditDrawingBinding
import com.cybershark.drawingsapp.ui.add_edit_drawing.viewmodel.AddEditViewModel
import com.cybershark.drawingsapp.util.UIState
import com.cybershark.drawingsapp.util.observe
import com.cybershark.drawingsapp.util.showToast
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class AddOrEditDrawingDialog : DialogFragment() {

    private val addEditViewModel by viewModels<AddEditViewModel>()
    private var _binding: FragmentAddEditDrawingBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditDrawingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgsAndSetup()
        setupLiveData()
    }

    // Gets arguments based on which either edit or add dialog is setup.
    private fun getArgsAndSetup() {
        if (arguments != null && arguments?.containsKey(EXTRAS_KEY) == true) {
            val drawingID = requireArguments().getInt(EXTRAS_KEY)
            setupEditDialog(drawingID)
        } else {
            setupAddDialog()
        }
    }

    //Sets up edit option dialog
    private fun setupEditDialog(drawingID: Int) = addEditViewModel.initDrawing(drawingID)

    // Observing ui state to show errors and loading bars.
    private fun setupLiveData() {
        observe(addEditViewModel.uiState) { state ->
            when (state) {
                is UIState.ERROR -> {
                    showToast(state.message)
                }
                is UIState.COMPLETED -> {
                    this.dismiss()
                }
                else -> Unit
            }
        }
        observe(addEditViewModel.currentDrawing) { state ->
            binding.etDrawingTitle.setText(state.title)
            currentImageUri = state.imageURI
            binding.ivDrawing.load(state.imageURI) {
                error(R.drawable.ic_error)
                transformations(RoundedCornersTransformation(4f))
            }
            setupEditListeners()
        }
    }

    //Sets up click listeners for edit dialog.
    private fun setupEditListeners() {
        binding.ivDrawing.setOnClickListener {
            getNewImageUriFromGallery()
        }
        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            val title = binding.etDrawingTitle.text
            when {
                currentImageUri == null -> showToast("No image Added!")
                title.isNullOrBlank() -> showToast("Add title!")
                else -> addEditViewModel.confirmUpdateDrawing(title.toString())
            }
        }
    }

    // Setting up add drawing dialog
    private fun setupAddDialog() {
        binding.btnAttachDrawing.isVisible = true
        binding.ivDrawing.isVisible = false
        setupAddListeners()
    }

    // Sets up click listeners
    private fun setupAddListeners() {
        binding.btnAttachDrawing.setOnClickListener {
            getNewImageUriFromGallery()
        }
        binding.ivDrawing.setOnClickListener {
            getNewImageUriFromGallery()
        }
        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            val title = binding.etDrawingTitle.text
            when {
                currentImageUri == null -> showToast("No image Added!")
                title.isNullOrBlank() -> showToast("Add title!")
                else -> addEditViewModel.confirmAddDrawing(title.toString())
            }
        }
    }

    // Loads Imagepicker
    private fun getNewImageUriFromGallery() {
        ImagePicker.with(this)
            .crop()
            .start()
    }

    // Activity result from image picker.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                // Gets image file
                val file = ImagePicker.getFile(data)!!
                //Shows imageView and hides attach button.
                binding.btnAttachDrawing.isVisible = false
                binding.ivDrawing.isVisible = true
                //Loads image
                addEditViewModel.setCurrentImageUri(file.toUri())
            }
            ImagePicker.RESULT_ERROR -> {
                Timber.d("onActivityResult: ${ImagePicker.getError(data)}")
                showToast("Error getting image.")
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    companion object {
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