package com.cybershark.drawingsapp.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.drawingsapp.databinding.ActivityMainBinding
import com.cybershark.drawingsapp.ui.drawing.DrawingDetailedActivity
import com.cybershark.drawingsapp.ui.main.adapters.MainAdapter
import com.cybershark.drawingsapp.ui.main.adapters.MainAdapter.DrawingItemListeners
import com.cybershark.drawingsapp.ui.main.viewmodel.MainViewModel
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
    }

    private fun setupRecyclerView() {
        binding.contentMain.rvDrawings.apply {
            adapter = MainAdapter(this@MainActivity)
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
        }
    }

    override fun onItemSelected(id: Int) {
        binding.contentLoading.isVisible = true
        startActivity(DrawingDetailedActivity.getIntent(this, id))
    }

    override fun onOptionsMenuClick(id: Int) {
        TODO("Not yet implemented")
    }

    companion object{
        const val TAG = "MainActivity"
    }
}