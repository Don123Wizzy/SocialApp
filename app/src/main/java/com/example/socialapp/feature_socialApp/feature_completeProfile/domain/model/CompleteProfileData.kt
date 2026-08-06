package com.example.socialapp.feature_socialApp.feature_completeProfile.domain.model

import android.net.Uri

data class CompleteProfileData(
    val uri: Uri,
    val jobTitle: String,
    val bio: String
)
