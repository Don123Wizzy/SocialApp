package com.example.socialapp.feature_socialApp.feature_commentActivity.presentation

import android.net.Uri

data class CommentActivityState (
    val ideaMessage : String = "",
    val selectedUri : List<Uri>? = emptyList(),
    val uploadedUrls : List<String> = emptyList()
)
