package com.example.socialapp.feature_socialApp.feature_UserProfile.domain.error

sealed interface UserProfileError {
    object FetchProfileFailed : UserProfileError
}