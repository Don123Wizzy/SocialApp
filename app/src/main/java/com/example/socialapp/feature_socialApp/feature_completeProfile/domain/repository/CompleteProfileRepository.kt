package com.example.socialapp.feature_socialApp.feature_completeProfile.domain.repository

import com.example.socialapp.feature_socialApp.feature_completeProfile.domain.model.CompleteProfileData

interface CompleteProfileRepository {
    suspend fun createProfile (completeProfileData: CompleteProfileData)
}