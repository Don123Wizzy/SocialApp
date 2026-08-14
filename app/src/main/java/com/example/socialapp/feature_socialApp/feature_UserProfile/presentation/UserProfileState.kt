package com.example.socialapp.feature_socialApp.feature_UserProfile.presentation

data class UserProfileState (
    val profileImage : String? = null,
    val userName : String? = null,
    val jobTitle : String = "",
    val bio : String = ""

)