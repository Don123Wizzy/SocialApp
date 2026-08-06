package com.example.socialapp.feature_socialApp.feature_completeProfile.data.repository

import com.example.socialapp.feature_socialApp.config.CloudinaryConfig
import com.example.socialapp.feature_socialApp.config.FireStoreCollections
import com.example.socialapp.feature_socialApp.config.FireStoreFields
import com.example.socialapp.feature_socialApp.data.remote.cloudinary.CloudinaryApi
import com.example.socialapp.feature_socialApp.data.remote.cloudinary.MultiPartConverter
import com.example.socialapp.feature_socialApp.feature_completeProfile.domain.model.CompleteProfileData
import com.example.socialapp.feature_socialApp.feature_completeProfile.domain.repository.CompleteProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class CompleteProfileRepositoryImpl @Inject constructor(
    private val cloudinaryApi: CloudinaryApi,
    private val multipartConverter: MultiPartConverter,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) :  CompleteProfileRepository {

    private lateinit var uploadedProfileImageUrl : String

    override suspend fun createProfile(completeProfileData: CompleteProfileData) {

        val profileImageUri = completeProfileData.uri
        val multipartConverterFile = multipartConverter.convert(profileImageUri)
        val uploadPreset = CloudinaryConfig.UPLOAD_PRESET.toRequestBody(
            "text/plain".toMediaType()
        )
        val cloudName = CloudinaryConfig.CLOUD_NAME
        val cloudinaryUpload = cloudinaryApi.uploadImage(cloudName, multipartConverterFile, uploadPreset)
        uploadedProfileImageUrl = cloudinaryUpload.secureUrl

        val uid = auth.currentUser?.uid
        val bio = completeProfileData.bio
        val jobTitle = completeProfileData.jobTitle
        uid?.let {
            val userDocument = firestore.collection(FireStoreCollections.USERS).document(it)
            userDocument.update( mapOf(
                "jobTitle" to jobTitle,
                "bio" to bio,
                "userProfileImage" to uploadedProfileImageUrl
            )).await()


        }


    }
}