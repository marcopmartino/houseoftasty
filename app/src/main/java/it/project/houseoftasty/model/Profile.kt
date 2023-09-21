package it.project.houseoftasty.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.storage.StorageReference

data class Profile(
    @get: DocumentId @set: Exclude var id: String? = null,
    @get: PropertyName("email") var email: String? = null,
    @get: Exclude @set: Exclude var password: String? = null,
    @get: Exclude @set: Exclude var newPassword: String? = null,
    @get: PropertyName("nome") var nome: String? = null,
    @get: PropertyName("cognome") var cognome: String? = null,
    @get: PropertyName("username") var username: String? = null,
    @get: PropertyName("boolImmagine") var boolImmagine: Boolean = false,
    @get: Exclude @set: Exclude var imageReference: StorageReference? = null)