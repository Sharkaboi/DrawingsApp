package com.cybershark.drawingsapp.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.data.models.DrawingEntity
import com.cybershark.drawingsapp.databinding.DrawingsListItemBinding
import com.cybershark.drawingsapp.util.getFriendlyString

class MainAdapter(private val drawingItemListeners: DrawingItemListeners) :
    RecyclerView.Adapter<MainAdapter.DrawingsViewHolder>() {

    private val diffUtilItemCallback = object : DiffUtil.ItemCallback<DrawingEntity>() {

        //id is the primary key for the data class.
        override fun areItemsTheSame(oldItem: DrawingEntity, newItem: DrawingEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DrawingEntity, newItem: DrawingEntity): Boolean {
            return oldItem == newItem
        }

    }

    private val listDiffer = AsyncListDiffer(this, diffUtilItemCallback)

    private lateinit var binding: DrawingsListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawingsViewHolder {
        binding = DrawingsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrawingsViewHolder(binding, drawingItemListeners)
    }

    override fun onBindViewHolder(holder: DrawingsViewHolder, position: Int) {
        holder.bind(listDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return listDiffer.currentList.size
    }

    fun submitList(list: List<DrawingEntity>) {
        listDiffer.submitList(list)
    }

    class DrawingsViewHolder(
        private val binding: DrawingsListItemBinding, private val drawingItemListeners: DrawingItemListeners
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DrawingEntity) {

            binding.root.setOnClickListener {
                //Passing drawing id.
                drawingItemListeners.onItemSelected(item.id)
            }
            binding.ibMenu.setOnClickListener {
                drawingItemListeners.onOptionsMenuClick(item.id)
            }

            binding.tvDrawingTitle.text = item.title
            binding.tvAddedTime.text = item.timeAdded.getFriendlyString()
            binding.ivThumbnail.load(item.imageURI) {
                transformations(RoundedCornersTransformation(4f))
                error(R.drawable.ic_error)
            }
            binding.btnMarkerCounter.text = item.markerCount.toString()
        }
    }

    // Interface for custom listeners
    interface DrawingItemListeners {
        fun onItemSelected(id: Int)
        fun onOptionsMenuClick(id: Int)
    }
}