package com.example.socialapp.feature_socialApp.feature_commentActivity.presentation

import android.net.Uri

sealed class CommentActivityEvents {
    data class ShareIdea(val message : String): CommentActivityEvents()
    data class OnImageSelected (val uris : List<Uri>) : CommentActivityEvents()
    data class OnUploadedUrls (val uploadedUrls : List<String>) : CommentActivityEvents()

    sealed class OneTime : CommentActivityEvents() {
        object CloseCommentActivity : OneTime()
        object GalleryImagePicker : OneTime()
        object CloseImageViewButtonIcon : OneTime()
        object ReselectGalleryImagePicker : OneTime()
        object PostUserContent : OneTime()
        data class PostUserContentSuccess(val message: String) : OneTime()
        data class PostUserContentError(val error: String) : OneTime()

        data class NavigatingToEditingScreen(val receivedUris : List<Uri>) : OneTime()
    }
}