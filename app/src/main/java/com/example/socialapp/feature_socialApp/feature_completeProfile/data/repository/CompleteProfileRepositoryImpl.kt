package com.example.socialapp.feature_socialApp.feature_completeProfile.data.repository

import android.net.Uri
import android.util.Log
import com.example.socialapp.feature_socialApp.config.CloudinaryConfig
import com.example.socialapp.feature_socialApp.config.FireStoreCollections
import com.example.socialapp.feature_socialApp.data.remote.cloudinary.CloudinaryApi
import com.example.socialapp.feature_socialApp.data.remote.cloudinary.MultiPartConverter
import com.example.socialapp.feature_socialApp.feature_completeProfile.domain.error.CompleteProfileError
import com.example.socialapp.feature_socialApp.feature_completeProfile.domain.error.CompleteProfileErrorException
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
) : CompleteProfileRepository {

    private var uploadedProfileImageUrl: String? = null

    override suspend fun createProfile(completeProfileData: CompleteProfileData) : Result<Unit> {

        return try {
            val profileImageUri = completeProfileData.uri
            profileImageUri?.let {
                uploadedProfileImageUrl = uploadToCloudinary(it).getOrThrow()
            }

            val bio = completeProfileData.bio
            val jobTitle = completeProfileData.jobTitle
            updateFireStore(bio, jobTitle).getOrThrow()

            Result.success(Unit)

        }catch (e: Exception){
            Result.failure(e)
        }
    }

    private suspend fun updateFireStore(bio: String, jobTitle: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?:return Result.failure(Exception("User not logged in"))
            val userDocument = firestore.collection(FireStoreCollections.USERS).document(uid)
            userDocument.update(
                mapOf(
                    "jobTitle" to jobTitle,
                    "bio" to bio,
                    "userProfileImage" to uploadedProfileImageUrl
                )
            ).await()
            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e("updateFireStore", "something went wrong!", e)
            Result.failure(CompleteProfileErrorException(CompleteProfileError.FireStoreUpdateFailed))
        }

    }


    private suspend fun uploadToCloudinary(uri: Uri): Result<String> {

        return try {
            val multipartConverterFile = multipartConverter.convert(uri)
            val uploadPreset = CloudinaryConfig.UPLOAD_PRESET.toRequestBody(
                "text/plain".toMediaType()
            )
            val cloudName = CloudinaryConfig.CLOUD_NAME
            val cloudinaryUpload =
                cloudinaryApi.uploadImage(cloudName, multipartConverterFile, uploadPreset)

            Result.success(cloudinaryUpload.secureUrl)
        }catch (e : Exception){
            Log.e("uploadToCloudinary","Something went wrong!", e)
            Result.failure(CompleteProfileErrorException(CompleteProfileError.CloudinaryUploadFailed))
        }
    }
}