package com.cybershark.drawingsapp.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.data.room.entities.DrawingsWithMarkersAndMarkerImages
import com.cybershark.drawingsapp.databinding.DrawingsListItemBinding
import com.cybershark.drawingsapp.util.getFriendlyString


class MainAdapter(private val drawingItemListeners: DrawingItemListeners) :
    RecyclerView.Adapter<MainAdapter.DrawingsViewHolder>() {

    private val diffUtilItemCallback = object : DiffUtil.ItemCallback<DrawingsWithMarkersAndMarkerImages>() {

        //id is the primary key for the data class.
        override fun areItemsTheSame(oldItem: DrawingsWithMarkersAndMarkerImages, newItem: DrawingsWithMarkersAndMarkerImages): Boolean {
            return oldItem.drawingEntity.id == newItem.drawingEntity.id
        }

        override fun areContentsTheSame(oldItem: DrawingsWithMarkersAndMarkerImages, newItem: DrawingsWithMarkersAndMarkerImages): Boolean {
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

    override fun getItemCount(): Int = listDiffer.currentList.size

    fun submitList(list: List<DrawingsWithMarkersAndMarkerImages>) {
        listDiffer.submitList(list)
    }

    class DrawingsViewHolder(
        private val binding: DrawingsListItemBinding, private val drawingItemListeners: DrawingItemListeners
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DrawingsWithMarkersAndMarkerImages) {

            binding.root.setOnClickListener {
                //Passing drawing id.
                drawingItemListeners.onItemSelected(item.drawingEntity.id)
            }
            binding.ibMenu.setOnClickListener { anchor: View ->
                val popup = PopupMenu(anchor.context, anchor)
                popup.menuInflater.inflate(R.menu.popup_menu_drawing, popup.menu)
                popup.setOnMenuItemClickListener {
                    if (it.itemId == R.id.item_edit) drawingItemListeners.onMenuEditClick(item.drawingEntity.id)
                    else if (it.itemId == R.id.item_delete) drawingItemListeners.onMenuDeleteClick(item)
                    return@setOnMenuItemClickListener true
                }
                popup.show()
            }

            binding.tvDrawingTitle.text = item.drawingEntity.title
            binding.tvAddedTime.text = item.drawingEntity.timeAdded.getFriendlyString()
            binding.ivThumbnail.load(item.drawingEntity.imageURI) {
                transformations(RoundedCornersTransformation(4f))
                error(R.drawable.ic_error)
            }
            binding.btnMarkerCounter.text = item.markers.size.toString()
        }
    }

    // Interface for custom listeners
    interface DrawingItemListeners {
        fun onItemSelected(id: Int)
        fun onMenuEditClick(id: Int)
        fun onMenuDeleteClick(drawingsWithMarkersAndMarkerImages: DrawingsWithMarkersAndMarkerImages)
    }
}