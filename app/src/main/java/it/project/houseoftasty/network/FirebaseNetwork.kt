package it.project.houseoftasty.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

open class FirebaseNetwork(initialCollectionPath: String) {

    protected val firestoreReference : CollectionReference = FirebaseFirestore.getInstance().collection(initialCollectionPath)
    private val storageReference : StorageReference = FirebaseStorage.getInstance().reference
    protected val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    open suspend fun getFileReference(path: String): StorageReference? {
        return storageReference.child(path)
    }

    open suspend fun deleteFile(url: String) {

    }

    open suspend fun updateFile(url: String) {

    }

    open suspend fun saveFile(url: String) {

    }


}