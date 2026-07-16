package com.example.socialapp.feature_socialApp.feature_commentActivity.domain.repository

import com.example.socialapp.feature_socialApp.feature_commentActivity.presentation.model.CreatePostRequest
import com.example.socialapp.feature_socialApp.feature_commentActivity.result_model.CreatePostResult
interface PostRepository {
    suspend fun createPost(post : CreatePostRequest) : CreatePostResult

}