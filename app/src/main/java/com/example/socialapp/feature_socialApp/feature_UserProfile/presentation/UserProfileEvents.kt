package com.example.socialapp.feature_socialApp.feature_UserProfile.presentation

sealed class UserProfileEvents {
    object EditUserWrittenDetailsButton : UserProfileEvents()
    object EditUserVisualDetailsButton : UserProfileEvents()
    data class FetchProfileFailedError (val errorMessage : String) : UserProfileEvents()
    object StartEditActivity : UserProfileEvents()

}