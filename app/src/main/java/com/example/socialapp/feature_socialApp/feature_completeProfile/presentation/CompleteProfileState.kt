package com.example.socialapp.feature_socialApp.feature_completeProfile.presentation

import android.net.Uri

data class CompleteProfileState(
    val uri : Uri? = null,
    val jobTitle : String = "",
    val bio : String = ""
)