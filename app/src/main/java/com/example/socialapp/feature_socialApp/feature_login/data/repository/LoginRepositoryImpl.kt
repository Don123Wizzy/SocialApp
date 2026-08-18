package com.example.socialapp.feature_socialApp.feature_login.data.repository

import android.net.Uri
import com.example.socialapp.data.Users
import com.example.socialapp.feature_socialApp.config.FireStoreCollections
import com.example.socialapp.feature_socialApp.feature_login.domain.model.GoogleUserNameAndProfile
import com.example.socialapp.feature_socialApp.feature_login.domain.model.SignInWithEmailAndPasswordResult
import com.example.socialapp.feature_socialApp.feature_login.domain.repository.LoginRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


//@Inject constructor() allows hilt construct this class
open class LoginRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : LoginRepository {

    private var googleUserName: String? = null
    private var googlePhotoUrl: Uri? = null

    override suspend fun login(
        email: String,
        password: String
    ): Result<SignInWithEmailAndPasswordResult> {
        return try {

            val result = auth.signInWithEmailAndPassword(email, password)
                .await()

            val uid = result.user?.uid ?: return Result.failure(Exception("uid not available"))


            val firebaseResult =
                firestore.collection(FireStoreCollections.USERS).document(uid).get().await()


            val username = firebaseResult.getString("name") ?: "User"

            Result.success(
                SignInWithEmailAndPasswordResult(
                    userId = uid,
                    userName = username
                )
            )


        } catch (e: Exception) {
            Result.failure(Exception("Login failed, Something went wrong!"))
        }

    }

    override suspend fun googleIdTokenReceiver(idToken: String): Result<GoogleUserNameAndProfile> {
        return try {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult =
                FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()

            val loginInUser: FirebaseUser = authResult.user
                ?: return Result.failure(Exception("Authentication failed"))

            val uid = loginInUser.uid
            googleUserName = loginInUser.displayName ?: "User"
            val googleEmail = loginInUser.email
            googlePhotoUrl = loginInUser.photoUrl

            val userDocument = Users(
                name = googleUserName,
                email = googleEmail,
                userProfileImage = googlePhotoUrl?.toString()
            )
            FirebaseFirestore.getInstance().collection(FireStoreCollections.USERS).document(uid)
                .set(userDocument).await()
            Result.success(
                GoogleUserNameAndProfile(
                    googleUserName = googleUserName!!,
                    googleProfileUrl = googlePhotoUrl
                )
            )

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(e)
        } catch (e : FirebaseAuthException){
            Result.failure(e)
        }catch (e : FirebaseException){
            Result.failure(e)
        }catch (e : Exception){
            Result.failure(e)
        }
    }

    override fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }
}