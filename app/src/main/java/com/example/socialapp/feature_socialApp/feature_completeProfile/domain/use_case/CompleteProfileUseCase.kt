package com.example.socialapp.feature_socialApp.feature_completeProfile.domain.use_case

import android.net.Uri
import com.example.socialapp.feature_socialApp.feature_completeProfile.domain.model.CompleteProfileData
import com.example.socialapp.feature_socialApp.feature_completeProfile.domain.repository.CompleteProfileRepository
import javax.inject.Inject

class CompleteProfileUseCase @Inject constructor(
    val completeProfileRepository: CompleteProfileRepository
) {
    suspend operator fun invoke (jobTitle : String, bio : String, uri : Uri){
        val completeProfileData = CompleteProfileData(
            jobTitle = jobTitle,
            bio = bio,
            uri = uri
        )
        completeProfileRepository.createProfile(completeProfileData)
    }
}