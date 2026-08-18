package com.example.socialapp.feature_socialApp.feature_profileFragment.presentation


sealed class ProfileFragmentEvents {
    object OpenProfileActivity : ProfileFragmentEvents()
    object OpenNotificationActivity : ProfileFragmentEvents()
    object OpenFriendListActivity : ProfileFragmentEvents()
    object OpenSettingsActivity : ProfileFragmentEvents()
    object OpenReportActivity : ProfileFragmentEvents()
    object Logout : ProfileFragmentEvents()
    data class FetchProfileFailedError(val errorMessage: String) : ProfileFragmentEvents()

    data class UserNotLoggedInError(val errorMessage: String) : ProfileFragmentEvents()

}