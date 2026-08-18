package com.example.socialapp.feature_socialApp.feature_profileFragment.domain.repository

import com.example.socialapp.data.Users
import kotlinx.coroutines.flow.Flow

interface UserDetailRepository {
    suspend fun getUserProfilePicAndUserName() : Flow<Users>
    fun logOut ()
}