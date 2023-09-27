package it.project.houseoftasty.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.storage.StorageReference

data class Comment(
    @get: DocumentId @set: Exclude var id: String? = null,
    @get: PropertyName("userId") var userId: String? = null,
    @get: PropertyName("text") var commentData: String? = null,
    @get: PropertyName("timestamp") var timestamp: Timestamp? = null,
    @get: Exclude @set: Exclude var userUsername: String? = null,
    @get: Exclude @set: Exclude var imageReference: StorageReference? = null)