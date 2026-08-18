package com.example.socialapp.feature_socialApp.feature_login.domain.repository



import com.example.socialapp.feature_socialApp.feature_login.domain.model.GoogleUserNameAndProfile
import com.example.socialapp.feature_socialApp.feature_login.domain.model.SignInWithEmailAndPasswordResult

interface LoginRepository  {

     suspend fun login(email : String , password : String) : Result<SignInWithEmailAndPasswordResult>
     suspend fun googleIdTokenReceiver( idToken : String) : Result<GoogleUserNameAndProfile>

     fun isUserAuthenticated(): Boolean

}