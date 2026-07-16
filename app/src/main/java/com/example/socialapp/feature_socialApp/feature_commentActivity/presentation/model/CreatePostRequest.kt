package com.example.socialapp.feature_socialApp.feature_commentActivity.presentation.model

data class CreatePostRequest(
    val content : String,
    val uploadedUrl : List<String>
)
