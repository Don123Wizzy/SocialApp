package com.example.socialapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.databinding.ItemPostImageBinding

class PostPreviewAdapter :
    ListAdapter<String, PostPreviewAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemPostImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemPostImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = getItem(position)    // ✅ No need for your own list
        // using glide here because setImageUri() only works for Uri and not cloudinaryUrl
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.binding.imImageVisualPreview)
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            // Compare the actual content (Uri string)
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }
}