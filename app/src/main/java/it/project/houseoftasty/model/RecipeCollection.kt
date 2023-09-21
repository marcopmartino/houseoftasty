package it.project.houseoftasty.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class RecipeCollection(
    @get: DocumentId @set: Exclude var id: String? = null,
    @get: PropertyName("nome") var nome: String? = null,
    @get: PropertyName("timestampCreazione") var timestampCreazione: Timestamp? = null,
    @get: Exclude @set: Exclude var listaRicette: MutableList<Recipe> = mutableListOf())