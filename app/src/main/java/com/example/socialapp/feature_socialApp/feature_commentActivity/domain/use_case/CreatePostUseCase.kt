package com.example.socialapp.feature_socialApp.feature_commentActivity.domain.use_case

import com.example.socialapp.feature_socialApp.feature_commentActivity.domain.repository.PostRepository
import com.example.socialapp.feature_socialApp.feature_commentActivity.presentation.model.CreatePostRequest
import com.example.socialapp.feature_socialApp.feature_commentActivity.result_model.CreatePostResult
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend operator fun invoke (post : CreatePostRequest) : CreatePostResult {
        return postRepository.createPost(post)
    }
}