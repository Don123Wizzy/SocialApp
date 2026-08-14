package com.example.socialapp.feature_socialApp.feature_UserProfile.domain.model

data class UserProfileData(
    val userProfileImage : String? = null,
    val userName : String = "",
    val jobTitle : String = "",
    val bio : String = ""
)
