package com.example.socialapp.feature_socialApp.feature_imageEditActivity.data.remote

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class MultiPartConverter @Inject constructor(
    @ApplicationContext private val context : Context
) {

    fun convert(uri: Uri): MultipartBody.Part{
        // The inputStream is like a pipe through wih we read the actual data(byteArray) of a uri
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Unable to open Uri: $uri")

        val byteArray = inputStream.readBytes()  //Read everything flowing through this stream and collect it into a ByteArray

        val requestBody = byteArray.toRequestBody(
            "image/*".toMediaType()
        )
        // The "image/*" means any type of image is acceptable
        // .toMediaType means it converts it to a MediaType Objet that OKhttp understands
        // Cloudinary understands HTTP requests
        // OkHttp understands how to build and send those HTTP requests.
        val multipartBody = MultipartBody.Part.createFormData(
            "file",
            "image/jpg",
            requestBody
        )
        return multipartBody
    }
}