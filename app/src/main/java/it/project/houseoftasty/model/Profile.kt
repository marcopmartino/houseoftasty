package it.project.houseoftasty.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Profile(
    @get: DocumentId @set: Exclude var id: String? = null,
    @get: PropertyName("mail") var mail: String? = null,
    @get: PropertyName("password") var password: String? = null,
    @get: PropertyName("chkPsw") var chkPsw: String? = null,
    @get: PropertyName("newPsw") var newPsw: String? =null,
    @get: PropertyName("chkNewPsw") var chkNewPsw: String? = null,
    @get: PropertyName("nome") var nome: String? = null,
    @get: PropertyName("cognome") var cognome: String? = null,
    @get: PropertyName("username") var username: String? = null)