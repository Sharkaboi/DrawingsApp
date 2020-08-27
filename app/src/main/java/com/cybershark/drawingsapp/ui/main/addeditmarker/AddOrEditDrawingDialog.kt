package com.cybershark.drawingsapp.ui.main.addeditmarker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
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
        Log.e(TAG, "getNewImageUriFromGallery: entered")
        //above marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionDenied()) {
                //permission not given
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //ask permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already given
                pickImageFromGallery()
            }
        } else {
            //below api 23
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        //open image picker
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun isPermissionDenied(): Boolean =
        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            newImageUri = uri ?: Uri.EMPTY
            Log.e(TAG, "onActivityResult: $uri")
            binding.ivDrawing.load(newImageUri) {
                error(R.drawable.ic_error)
                transformations(RoundedCornersTransformation(4f))
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val TAG = "FragmentAddMarker"
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