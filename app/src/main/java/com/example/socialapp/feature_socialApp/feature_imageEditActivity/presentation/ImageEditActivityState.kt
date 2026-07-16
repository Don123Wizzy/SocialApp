package com.example.socialapp.feature_socialApp.feature_imageEditActivity.presentation

import com.example.socialapp.feature_socialApp.feature_imageEditActivity.presentation.model.EditableImage

data class ImageEditActivityState (
    val editableImages : List<EditableImage> = emptyList(),
    val currentEditImageId : Long = 0,
    val uploadedUrls : List<String> = emptyList()
)
