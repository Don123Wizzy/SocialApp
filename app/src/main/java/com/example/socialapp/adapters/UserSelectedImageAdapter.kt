package com.example.socialapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ItemPostImageBinding
import com.example.socialapp.feature_socialApp.feature_imageEditActivity.presentation.model.EditableImage

class UserSelectedImageAdapter() :
    ListAdapter<EditableImage, UserSelectedImageAdapter.ItemViewHolder>(diffCallback) {

    inner class ItemViewHolder(val binding: ItemPostImageBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view = ItemPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        val individualLocalImageUri = getItem(position)
        holder.binding.imImageVisualPreview.setImageURI(individualLocalImageUri.uri)

        //imagesUrlRetrieved this was gotten from the inspiration that we obtained
        // the firebase storage Uri(URL) and converted to a string which we then stored in firestore
        // i made a recording for this in your phone

    }

    // DiffUtil.ItemCallback<>() is an abstract class
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<EditableImage>() {
            override fun areItemsTheSame(oldItem: EditableImage, newItem: EditableImage): Boolean {
                return oldItem.id == newItem.id

            }

            override fun areContentsTheSame(oldItem: EditableImage, newItem: EditableImage): Boolean {
                return oldItem == newItem
            }
        }
    }


}