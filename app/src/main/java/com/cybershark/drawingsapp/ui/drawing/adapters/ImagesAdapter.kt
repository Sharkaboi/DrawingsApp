package com.cybershark.drawingsapp.ui.drawing.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.databinding.MarkerAttachedImagesItemBinding

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    private val diffUtilItemCallback = object : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }
    }

    private val listDiffer = AsyncListDiffer(this, diffUtilItemCallback)

    private lateinit var binding: MarkerAttachedImagesItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        binding = MarkerAttachedImagesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.bind(listDiffer.currentList[position])
    }

    override fun getItemCount(): Int = listDiffer.currentList.size

    fun submitList(list: List<Uri>) = listDiffer.submitList(list)

    class ImagesViewHolder(private val binding: MarkerAttachedImagesItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {
            binding.ivImage.load(uri) {
                error(R.drawable.ic_error)
                transformations(RoundedCornersTransformation(4f))
            }

        }
    }
}