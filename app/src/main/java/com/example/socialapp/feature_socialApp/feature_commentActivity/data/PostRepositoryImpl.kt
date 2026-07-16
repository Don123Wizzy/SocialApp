package com.example.socialapp.feature_socialApp.feature_commentActivity.data

import com.example.socialapp.feature_socialApp.config.FireStoreCollections
import com.example.socialapp.feature_socialApp.feature_commentActivity.data.model.Post
import com.example.socialapp.feature_socialApp.feature_commentActivity.domain.repository.PostRepository
import com.example.socialapp.feature_socialApp.feature_commentActivity.presentation.model.CreatePostRequest
import com.example.socialapp.feature_socialApp.feature_commentActivity.result_model.CreatePostResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PostRepository {
    private lateinit var name: String
    override suspend fun createPost(post: CreatePostRequest): CreatePostResult {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            name = firestore.collection(FireStoreCollections.USERS).document(uid).get()
                .await().getString("name").toString()
        }

        val documentId = firestore.collection(FireStoreCollections.POSTS).document()
        val content = post.content
        val uploadedUrls = post.uploadedUrl
        val timeStamp = System.currentTimeMillis()

        val userPost = Post(
            content = content,
            userId = uid,
            imagePostList = uploadedUrls,
            name = name,
            timestamp = timeStamp,
            documentId = documentId.id
        )
        return try {
            documentId.set(userPost)
                .await()
            CreatePostResult.Success(message = "Post Successful")

        } catch (e: Exception) {
            CreatePostResult.Error(error = e.message.toString())
        }


    }
}