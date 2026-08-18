package com.example.socialapp.feature_socialApp.feature_login.domain.use_cases

data class LoginUseCase (
    val login: Login,
    val createAcct : CreateAcct,
    val forgotPassword: ForgotPassword,
    val signInWithGoogle: SignInWithGoogle
)
