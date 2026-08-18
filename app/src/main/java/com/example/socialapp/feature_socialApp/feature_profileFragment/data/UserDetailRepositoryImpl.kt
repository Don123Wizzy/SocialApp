package com.example.socialapp.feature_socialApp.feature_profileFragment.data

import com.example.socialapp.data.Users
import com.example.socialapp.feature_socialApp.config.FireStoreCollections
import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.model.UserDetailError
import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.model.UserDetailException
import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.repository.UserDetailRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDetailRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserDetailRepository {
    override suspend fun getUserProfilePicAndUserName(): Flow<Users> {
        val uid =
            auth.currentUser?.uid ?: throw UserDetailException(UserDetailError.UserNotLoggedIn)

        return firestore.collection(FireStoreCollections.USERS).document(uid)
            .snapshots()
            .map { usersDocument ->
                usersDocument.toObject(Users::class.java)!!
            }.catch { exception ->
                if (exception is UserDetailException) {
                    throw exception
                }
                throw UserDetailException(UserDetailError.FetchUserDetailsFailed)
            }


    }

    override fun logOut() {
        auth.signOut()
    }
}