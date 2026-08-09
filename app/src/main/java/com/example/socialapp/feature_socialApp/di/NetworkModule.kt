package com.example.socialapp.feature_socialApp.di

import com.example.socialapp.feature_socialApp.data.remote.cloudinary.CloudinaryApi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    @Provides
    @Singleton
    fun provideFireBaseAuth() : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }


    // The function below teaches hilt how to build the retrofit object and then provide it to lass
    // or functions that need it e.g provideCloudinaryApi()
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    // The same here, but here, the retrofit object is used to provide the implementation of
    // the cloudinary interface
    @Provides
    @Singleton
    fun provideCloudinaryApi(
        retrofit: Retrofit
    ): CloudinaryApi {
        return retrofit.create(CloudinaryApi::class.java)
    }

    // This is how the retrofit objet provides the implementation behind the scene
    //class GeneratedCloudinaryApi : CloudinaryApi {
        //override suspend fun uploadImage(...) : CloudinaryUploadResponse {
            // Create HTTP request
            // Send request to Cloudinary
            // Receive JSON
            // Convert JSON to CloudinaryUploadResponse
            // Return it
        //}
    //}



}