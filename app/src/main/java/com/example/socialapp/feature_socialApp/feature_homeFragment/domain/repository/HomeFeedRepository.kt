package com.example.socialapp.feature_socialApp.feature_homeFragment.domain.repository
import com.example.socialapp.data.Post
import kotlinx.coroutines.flow.Flow

interface HomeFeedRepository {

    fun getPost() : Flow<List<Post>>
    suspend fun likePost (post : Post)
    suspend fun deletePost(post: Post)
    suspend fun editPost(post: Post)
}
