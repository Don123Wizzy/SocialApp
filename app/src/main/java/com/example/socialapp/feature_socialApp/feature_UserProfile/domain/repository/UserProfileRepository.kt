package com.example.socialapp.feature_socialApp.feature_UserProfile.domain.repository

import com.example.socialapp.feature_socialApp.feature_UserProfile.domain.model.UserProfileData

interface UserProfileRepository {
    suspend fun getUserProfile () : Result<UserProfileData>

}