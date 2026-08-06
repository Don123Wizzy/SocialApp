package com.example.socialapp.feature_socialApp.feature_completeProfile.presentation

import android.net.Uri

sealed class CompleteProfileEvents  {
    data class JobTitle(val jobTitle : String) : CompleteProfileEvents()
    data class Bio (val bio :String) : CompleteProfileEvents()
    data object Continue  : CompleteProfileEvents()
    data class ProfileImageSelected(val uri : Uri) : CompleteProfileEvents()
    data object NavigateToMain : CompleteProfileEvents()

}