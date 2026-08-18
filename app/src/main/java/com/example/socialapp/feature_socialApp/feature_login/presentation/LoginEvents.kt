package com.example.socialapp.feature_socialApp.feature_login.presentation

import android.net.Uri

sealed class LoginEvents {
    data class UserEmail(val email : String) : LoginEvents()
    data class UserPassword(val password : String) : LoginEvents()
    data class LoginClicked(val email : String, val password : String ) : LoginEvents()
    object ForgotPasswordClicked : LoginEvents()
    data class SignInWithGoogleClicked(val googleToken : String?) : LoginEvents()
    sealed class OneTimeEvents : LoginEvents(){
        data class ShowToast(val message: String) : OneTimeEvents()
        data class ShowError(val error: String) : OneTimeEvents()
        object NavigateToMain : OneTimeEvents()
        data class NavigateToCompleteProfileScreenUsingGoogleSIgnIn(val uri : Uri?) : OneTimeEvents()
        object CreateAcct : OneTimeEvents()

    }

}