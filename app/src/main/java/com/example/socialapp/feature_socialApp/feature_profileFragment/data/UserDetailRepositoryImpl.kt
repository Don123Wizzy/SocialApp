package com.example.socialapp.feature_socialApp.feature_profileFragment.data

import com.example.socialapp.feature_socialApp.config.FireStoreCollections
import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.model.ProfileImageAndUserNameModel
import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.repository.UserDetailRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDetailRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserDetailRepository {
    override suspend fun getUserProfilePicAndUserName() : Result<ProfileImageAndUserNameModel>{

        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Unknown Message"))
        return try {

            val userDocument = firestore.collection(FireStoreCollections.USERS).document(uid).get().await()
            val userName = userDocument.getString("name") ?: "User"
            val userprofilePic = userDocument.getString("userProfileImage")
            Result.success(
                ProfileImageAndUserNameModel(
                    profileImage = userprofilePic,
                    userName = userName
                )
            )
        }catch (e : FirebaseAuthInvalidCredentialsException){
            Result.failure(e)
        } catch (e: FirebaseAuthException) {
            Result.failure(e)

        } catch (e: FirebaseFirestoreException) {
            Result.failure(e)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}