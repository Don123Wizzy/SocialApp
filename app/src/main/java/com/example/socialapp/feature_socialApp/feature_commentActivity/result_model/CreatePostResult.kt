package com.example.socialapp.feature_socialApp.feature_commentActivity.result_model

sealed class CreatePostResult {
    data class Success(val message : String) : CreatePostResult()
    data class Error (val error : String) : CreatePostResult()
}