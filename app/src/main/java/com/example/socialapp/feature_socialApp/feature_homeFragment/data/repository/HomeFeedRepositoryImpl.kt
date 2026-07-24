package com.example.socialapp.feature_socialApp.feature_homeFragment.data.repository

import com.example.socialapp.data.Post
import com.example.socialapp.feature_socialApp.config.FireStoreCollections
import com.example.socialapp.feature_socialApp.config.FireStoreFields
import com.example.socialapp.feature_socialApp.feature_homeFragment.domain.repository.HomeFeedRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeFeedRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HomeFeedRepository {
    // My function does not have a return type because
    // a senior Android developer would typically keep Firestore listeners inside the Repository,
    // convert them into a Flow<List<Post>>, and let the ViewModel collect that flow.
    // below is just one of the write operations


    //read
    override fun getPost(): Flow<List<Post>> {
        return firestore.collection(FireStoreCollections.POSTS)
            .orderBy(FireStoreFields.TIME_STAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapShot ->
                querySnapShot.documents.map { document ->
                    document.toObject(Post::class.java)!!.apply {
                        documentId = document.id
                    }

                }
            }
    }
    //This is query firestore for document list when a change(editing, deleting) in the list occurs
    // we get the current document list when the change happened
    // we converts eah documents in the list to Post objet and also obtained their document.Id


    // Listen to changes in the posts collection.
    // Whenever a document is added, modified, or removed,
    // Firestore emits the current snapshot of the collection.
    // Each DocumentSnapshot is converted into a Post object,
    // and the Firestore document ID is assigned to the Post object
    // because it is needed for operations like update and delete.


    // write
    override suspend fun likePost(post: Post) {
        firestore.collection(FireStoreCollections.POSTS)
            .document(post.documentId)
            .update(FireStoreFields.LIKE_COUNT, FieldValue.increment(1)).await()
    }

    override suspend fun deletePost(post: Post) {
        firestore.collection(FireStoreCollections.POSTS)
            .document(post.documentId)
            .delete()
            .await()
    }

    override suspend fun editPost(post: Post) {
        firestore.collection(FireStoreCollections.POSTS)
            .document(post.documentId).update(
                FireStoreFields.CONTENT, post.content
            ).await()

    }
}