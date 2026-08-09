package com.example.socialapp.feature_socialApp.feature_completeProfile.domain.error

sealed interface CompleteProfileError  {
     data object CloudinaryUploadFailed : CompleteProfileError
     data object FireStoreUpdateFailed : CompleteProfileError
     data object UserNotLoggedIn : CompleteProfileError

}