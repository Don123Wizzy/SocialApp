package com.example.socialapp.feature_socialApp.feature_imageEditActivity.presentation

import android.net.Uri
import com.example.socialapp.feature_socialApp.feature_imageEditActivity.presentation.model.EditableImage

sealed class ImageEditActivityEvents {
    data class AddedImages(val uris : MutableList<Uri>) : ImageEditActivityEvents()
    data class EditedImage(val uri : Uri, val id : Long) : ImageEditActivityEvents()
    data class ReceivedImagesFromCommentActivity(val receivedUris : MutableList<Uri>) : ImageEditActivityEvents()
    sealed class OneTime : ImageEditActivityEvents() {
        data class EditButton(val editableImage: EditableImage) : OneTime()
        data class DeleteButton(val imageToBeDeleted : EditableImage) : OneTime()
        object Next : OneTime()
        object CloseActivity : OneTime()
        object GalleryImageOneTimeEvent : OneTime()

    }
}