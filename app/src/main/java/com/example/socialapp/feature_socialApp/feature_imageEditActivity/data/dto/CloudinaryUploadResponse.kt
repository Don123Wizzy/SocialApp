package com.example.socialapp.feature_socialApp.feature_imageEditActivity.data.dto
import com.google.gson.annotations.SerializedName

//Represent the response Cloudinary sends back in a form my Kotlin code understands
//check note for more detail
data class CloudinaryUploadResponse(
    @SerializedName("secure_url")
    val secureUrl: String
)