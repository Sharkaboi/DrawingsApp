package com.cybershark.drawingsapp.ui.main

import android.os.Bundle
import android.util.Log
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
import com.cybershark.drawingsapp.ui.main.addeditmarker.AddOrEditDrawingDialog
import com.cybershark.drawingsapp.ui.main.viewmodel.MainViewModel
import com.cybershark.drawingsapp.util.UIState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DrawingItemListeners {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupListeners()
        setupLiveData()
    }

    private fun setupLiveData() {
        mainViewModel.uiState.observe(this) { uiState: UIState? ->
            if (uiState is UIState.LOADING) {
                binding.contentLoading.isVisible = true
            } else {
                binding.contentLoading.isGone = true
            }
        }
    }

    private fun setupListeners() {
        binding.fabAddDrawing.setOnClickListener {
            AddOrEditDrawingDialog.getInstance(AddOrEditDrawingDialog.ADD)
                .show(supportFragmentManager, AddOrEditDrawingDialog.TAG)
        }
    }

    private fun setupRecyclerView() {
        val adapter = MainAdapter(this)
        binding.contentMain.rvDrawings.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
        }
        mainViewModel.drawingsList.observe(this) { listOfDrawings: List<DrawingEntity>? ->
            if (listOfDrawings.isNullOrEmpty()) {
                binding.tvEmptyHint.isVisible = true
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

    override fun onOptionsMenuClick(id: Int) = openPopupMenu(id)

    private fun openPopupMenu(id: Int) {
        AddOrEditDrawingDialog.getInstance(AddOrEditDrawingDialog.EDIT, id)
            .show(supportFragmentManager, AddOrEditDrawingDialog.TAG)
    }

    private fun setCustomAnims() = overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

    companion object {
        const val TAG = "MainActivity"
    }
}