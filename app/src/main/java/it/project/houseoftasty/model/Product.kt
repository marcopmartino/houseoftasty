package it.project.houseoftasty.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Product(
    @get: DocumentId @set: Exclude var id: String? = null,
    @get: PropertyName("nome") var nome: String? = null,
    @get: PropertyName("quantita") var quantita: String? = null,
    @get: PropertyName("misura") var misura: String? = null,
    @get: PropertyName("scadenza") var scadenza: String? = null)