package com.example.socialapp.feature_socialApp.feature_profileFragment.domain.repository

import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.model.ProfileImageAndUserNameModel

interface UserDetailRepository {
    suspend fun getUserProfilePicAndUserName() : Result<ProfileImageAndUserNameModel>
}