package com.example.socialapp.feature_socialApp.feature_create_acct.data.repository

import com.example.socialapp.data.Users
import com.example.socialapp.feature_socialApp.feature_create_acct.domain.model.CreateAcctModel
import com.example.socialapp.feature_socialApp.feature_create_acct.domain.repository.CreateAcctRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CreateAcctRepositoryImpl @Inject constructor(): CreateAcctRepository {
    override suspend fun createAcct( fullName: String, email: String, password: String): Result<CreateAcctModel> {

        return try {
            val result = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("Unknown User"))

            val userDocument = Users(
                email = email,
                name = fullName,
                userProfileImage = null
            )

            FirebaseFirestore.getInstance().collection("users").document(uid).set(userDocument).await()

            Result.success(
                CreateAcctModel(
                    fullName = fullName
                )

            )
        }catch(e: FirebaseFirestoreException){
            Result.failure(
                e
            )

        }catch(e: Exception){
            Result.failure(
                e
            )
        }







    }
}