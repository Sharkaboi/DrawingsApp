package com.cybershark.drawingsapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.databinding.ActivityMainBinding
import com.cybershark.drawingsapp.ui.drawing.DrawingDetailedActivity
import com.cybershark.drawingsapp.ui.main.adapters.MainAdapter
import com.cybershark.drawingsapp.ui.main.adapters.MainAdapter.DrawingItemListeners
import com.cybershark.drawingsapp.ui.main.viewmodel.MainViewModel
import com.cybershark.drawingsapp.ui.settings.SettingsActivity
import com.cybershark.drawingsapp.util.UIState
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
        mainViewModel.uiState.observe(this) { uiState: UIState? ->
            if (uiState is UIState.LOADING) {
                binding.contentLoading.isVisible = true
            } else {
                binding.contentLoading.isGone = true
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
        mainViewModel.drawingsList.observe(this) { listOfDrawings: List<DrawingEntity>? ->
            if (listOfDrawings.isNullOrEmpty()) {
                binding.tvEmptyHint.isVisible = true
                adapter.submitList(emptyList())
            } else {
                binding.tvEmptyHint.isGone = true
                Log.d(TAG, "setupRecyclerView: $listOfDrawings")
                adapter.submitList(listOfDrawings)
            }
        }
    }

    // RecyclerView click listeners callback
    override fun onItemSelected(id: Int) {
        binding.contentLoading.isVisible = true
        startActivity(DrawingDetailedActivity.getIntent(this, id))
        setCustomAnims()
        binding.contentLoading.isGone = true
    }

    // RecyclerView click listeners callback for edit menu
    override fun onMenuEditClick(id: Int) {
        openEditDialogFragment(id)
    }

    // Asks confirmation and deletes the drawing, nested markers and marker images
    override fun onMenuDeleteClick(id: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_drawing)
            .setMessage(R.string.irreversible)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                mainViewModel.deleteDrawing(id)
            }
            .setNegativeButton(android.R.string.no) { dialog, _ ->
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

    companion object {
        const val TAG = "MainActivity"
    }
}