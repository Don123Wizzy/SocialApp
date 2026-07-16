package com.example.socialapp.feature_socialApp.feature_imageEditActivity.domain.use_case

import android.net.Uri
import android.util.Log
import com.example.socialapp.feature_socialApp.feature_imageEditActivity.domain.repository.ImageRepository
import javax.inject.Inject

class UploadImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    // The return was a list of String because cloudinary return URL and a URL is typically represented as a String
    suspend operator fun invoke (imageList: List<Uri>) : List<String> {
        Log.d("small", "UseCase started")
        val result = imageRepository.uploadImage(imageList)
        Log.d("small", "Repository returned: ${result.size}")
        return result
    }
}