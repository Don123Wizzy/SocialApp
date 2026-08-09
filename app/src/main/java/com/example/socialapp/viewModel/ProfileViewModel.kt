package com.example.socialapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel(application: Application) : AndroidViewModel(application){

    private val _userProfileImage = MutableLiveData<String>()
    val userProfileImage: LiveData<String> get() = _userProfileImage

    fun updateProfileImage(newUrl: String) {
        _userProfileImage.value = newUrl
    }


}