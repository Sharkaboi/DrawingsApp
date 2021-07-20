package com.cybershark.drawingsapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.data.room.entities.DrawingsWithMarkersAndMarkerImages
import com.cybershark.drawingsapp.databinding.ActivityMainBinding
import com.cybershark.drawingsapp.ui.add_edit_drawing.ui.AddOrEditDrawingDialog
import com.cybershark.drawingsapp.ui.drawing.ui.DrawingDetailedActivity
import com.cybershark.drawingsapp.ui.main.adapters.MainAdapter
import com.cybershark.drawingsapp.ui.main.adapters.MainAdapter.DrawingItemListeners
import com.cybershark.drawingsapp.ui.main.viewmodel.MainViewModel
import com.cybershark.drawingsapp.ui.settings.SettingsActivity
import com.cybershark.drawingsapp.util.UIState
import com.cybershark.drawingsapp.util.observe
import com.cybershark.drawingsapp.util.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DrawingItemListeners {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.contentMain.toolbar)
        setupRecyclerView()
        setupListeners()
        setupLiveData()
    }

    // Setting up UI state observers for progress bar
    private fun setupLiveData() {
        observe(mainViewModel.uiState) { uiState: UIState ->
            binding.contentLoading.isVisible = uiState is UIState.LOADING
            when (uiState) {
                is UIState.COMPLETED -> showToast(uiState.message)
                is UIState.ERROR -> showToast(uiState.message)
                else -> Unit
            }
        }
    }

    // Setting up click listeners
    private fun setupListeners() {
        binding.fabAddDrawing.setOnClickListener { openAddDrawingDialogFragment() }
    }

    // Opens add drawing dialog fragment
    private fun openAddDrawingDialogFragment() {
        AddOrEditDrawingDialog.getInstance(AddOrEditDrawingDialog.ADD)
            .show(supportFragmentManager, AddOrEditDrawingDialog.TAG)
    }

    // Sets up recycler view for showing drawings
    private fun setupRecyclerView() {
        val adapter = MainAdapter(this)
        binding.contentMain.rvDrawings.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
        }
        // Observers list of drawings to show data
        observe(mainViewModel.drawingsList) { listOfDrawings: List<DrawingsWithMarkersAndMarkerImages>? ->
            if (listOfDrawings.isNullOrEmpty()) {
                binding.tvEmptyHint.isVisible = true
                adapter.submitList(emptyList())
            } else {
                binding.tvEmptyHint.isGone = true
                adapter.submitList(listOfDrawings)
            }
        }
    }

    // RecyclerView click listeners callback
    override fun onItemSelected(id: Int) {
        startActivity(DrawingDetailedActivity.getIntent(this, id))
        setCustomAnims()
    }

    // RecyclerView click listeners callback for edit menu
    override fun onMenuEditClick(id: Int) {
        openEditDialogFragment(id)
    }

    // Asks confirmation and deletes the drawing, nested markers and marker images
    override fun onMenuDeleteClick(drawingsWithMarkersAndMarkerImages: DrawingsWithMarkersAndMarkerImages) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_drawing)
            .setMessage(R.string.irreversible)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                mainViewModel.deleteDrawing(drawingsWithMarkersAndMarkerImages)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Opens Edit Dialog fragment for drawing
    private fun openEditDialogFragment(id: Int) {
        AddOrEditDrawingDialog.getInstance(AddOrEditDrawingDialog.EDIT, id)
            .show(supportFragmentManager, AddOrEditDrawingDialog.TAG)
    }

    // Sets custom animations for activity
    private fun setCustomAnims() = overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

    // Overflow Menu for settings
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_overflow_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_settings -> openSettingsActivity()
        }
        return true
    }

    private fun openSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
        setCustomAnims()
    }
}