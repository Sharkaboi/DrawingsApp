package com.cybershark.drawingsapp.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.RoundedCornersTransformation
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.databinding.FragmentAddEditDrawingBinding
import com.cybershark.drawingsapp.ui.main.viewmodel.MainViewModel
import com.cybershark.drawingsapp.util.UIState
import com.cybershark.drawingsapp.util.longToast
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
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
        mainViewModel.uiState.observe(viewLifecycleOwner) { uiState: UIState? ->
            when (uiState) {
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
            getNewImageUriFromGallery()
        }
        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            if (!::newImageUri.isInitialized) {
                context?.longToast("No image Added!")
            } else if (binding.etDrawingTitle.text.isNullOrBlank()) {
                context?.longToast("Add title!")
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
            getNewImageUriFromGallery()
        }
        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            if (!::newImageUri.isInitialized) {
                context?.longToast("No image Added!")
            } else if (binding.etDrawingTitle.text.isNullOrBlank()) {
                context?.longToast("Add title!")
            } else {
                val updatedDrawingEntity = itemToEdit.copy(
                    title = binding.etDrawingTitle.text.toString(),
                    imageURI = newImageUri,
                    timeAdded = Date()
                )
                mainViewModel.updateDrawing(updatedDrawingEntity)
            }
        }
    }

    private fun getNewImageUriFromGallery() {
        ImagePicker.with(this)
            .crop()
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val file = ImagePicker.getFile(data)!!
                Log.e(TAG, "onActivityResult: $file")
                binding.ivDrawing.load(file) {
                    error(R.drawable.ic_error)
                    transformations(RoundedCornersTransformation(4f))
                }
                val destination = "${context?.getExternalFilesDir(null)}${File.separator}Drawings${File.separator}"
                val destinationFolder = File(destination)
                Log.d(TAG, "onActivityResult: $destinationFolder")
                copyImage(file, destinationFolder)
            }
            ImagePicker.RESULT_ERROR -> {
                Log.d(TAG, "onActivityResult: ${ImagePicker.getError(data)}")
                context?.longToast("Error getting image.")
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun copyImage(inputFile: File, destinationFolder: File) {
        try {
            val exportFile = File("${destinationFolder.path}${File.separator}${inputFile.name}")

            if (!destinationFolder.exists()) {
                destinationFolder.mkdir()
            }
            if (!destinationFolder.canWrite()) {
                Log.e(TAG, "copyImage: No write perm")
                return
            }
            var inputChannel: FileChannel? = null
            var outputChannel: FileChannel? = null
            try {
                inputChannel = FileInputStream(inputFile).channel
                outputChannel = FileOutputStream(exportFile).channel
            } catch (e: Exception) {
                Log.d(TAG, "copyImage: ${e.message}")
            }
            inputChannel?.transferTo(0, inputChannel.size(), outputChannel)
            inputChannel?.close()
            outputChannel?.close()

            newImageUri = exportFile.toUri()

        } catch (e: Exception) {
            context?.longToast("Error copying image!")
            Log.e(TAG, "copyImage: ${e.printStackTrace()}")
        }
    }

    companion object {
        const val TAG = "AddOrEditDrawingDialog"
        private const val EXTRAS_KEY = "drawingID"
        const val EDIT = true
        const val ADD = false
        const val PERMISSION_CODE = 1001
        const val IMAGE_PICK_CODE = 1000

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