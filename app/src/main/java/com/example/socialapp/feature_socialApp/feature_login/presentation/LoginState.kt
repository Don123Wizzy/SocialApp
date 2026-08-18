package com.example.socialapp.feature_socialApp.feature_login.presentation

data class LoginState(
    val email: String = "",
    val password: String = "",
    val userName : String = "",
    val errorMessage : String = "",
    val userLoggedIn : Boolean? = null
)
