package com.example.socialapp.feature_socialApp.di

import com.example.socialapp.feature_socialApp.feature_commentActivity.data.PostRepositoryImpl
import com.example.socialapp.feature_socialApp.feature_commentActivity.domain.repository.PostRepository
import com.example.socialapp.feature_socialApp.feature_create_acct.data.repository.CreateAcctRepositoryImpl
import com.example.socialapp.feature_socialApp.feature_create_acct.domain.repository.CreateAcctRepository
import com.example.socialapp.feature_socialApp.feature_imageEditActivity.data.repository.ImageRepositoryImpl
import com.example.socialapp.feature_socialApp.feature_imageEditActivity.domain.repository.ImageRepository
import com.example.socialapp.feature_socialApp.feature_login.data.repository.LoginRepositoryImpl
import com.example.socialapp.feature_socialApp.feature_login.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


// a lot of abstract function yh, hilt to the rescue providing implementation for them😂
@Module
@InstallIn(SingletonComponent::class)

abstract class RepositoryModule {

    @Binds
    abstract fun bindLoginRepository(
        impl: LoginRepositoryImpl
    ): LoginRepository


    @Binds
    abstract fun bindCreateAcctRepository(
        impl: CreateAcctRepositoryImpl
    ): CreateAcctRepository

    // This teaches how to provide to bind ImageRepositoryImpl to ImageRepository (check your note(eko excel) for more details)
    // Tells Hilt that whenever an ImageRepository is requested,
    // it should provide an ImageRepositoryImpl.
    @Binds
    abstract fun bindImageRepository(impl: ImageRepositoryImpl) : ImageRepository

    @Binds
    abstract fun bindPostRepository (impl : PostRepositoryImpl) : PostRepository


}