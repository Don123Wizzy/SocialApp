package com.example.socialapp.feature_socialApp.feature_UserProfile.data.repository

import com.example.socialapp.feature_socialApp.config.FireStoreCollections
import com.example.socialapp.feature_socialApp.feature_UserProfile.domain.error.UserProfileError
import com.example.socialapp.feature_socialApp.feature_UserProfile.domain.error.UserProfileException
import com.example.socialapp.feature_socialApp.feature_UserProfile.domain.model.UserProfileData
import com.example.socialapp.feature_socialApp.feature_UserProfile.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserProfileRepository {
    override suspend fun getUserProfile(): Result<UserProfileData> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not available"))
        return try {
            val document =
                fireStore.collection(FireStoreCollections.USERS).document(uid).get().await()
            val userProfilePic = document.getString("userProfileImage")
            val userName = document.getString("name") ?: "User"
            val jobTitle = document.getString("jobTitle") ?: ""
            val bio = document.getString("bio") ?: ""
            Result.success(
                UserProfileData(
                    userName = userName,
                    userProfileImage = userProfilePic,
                    jobTitle = jobTitle,
                    bio = bio
                )
            )

        } catch (e: Exception) {
            Result.failure(UserProfileException(UserProfileError.FetchProfileFailed))
        }
    }
}