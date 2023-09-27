package it.project.houseoftasty.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.storage.StorageReference

data class Recipe(
    @get: DocumentId @set: Exclude var id: String? = null,
    @get: PropertyName("idCreatore") var idCreatore: String? = null,
    @get: PropertyName("titolo") var titolo: String? = null,
    @get: PropertyName("ingredienti") var ingredienti: String? = null,
    @get: PropertyName("numPersone") var numPersone: Int? = null,
    @get: PropertyName("preparazione") var preparazione: String? = null,
    @get: PropertyName("tempoPreparazione") var tempoPreparazione: Int? = null,
    @get: PropertyName("boolPubblicata") var boolPubblicata: Boolean = false,
    @get: PropertyName("boolPostPrivato") var boolPostPrivato: Boolean = false,
    @get: PropertyName("boolImmagine") var boolImmagine: Boolean = false,
    @get: PropertyName("timestampCreazione") var timestampCreazione: Timestamp? = null,
    @get: PropertyName("timestampPubblicazione") var timestampPubblicazione: Timestamp? = null,
    @get: PropertyName("views") var views: Int = 0,
    @get: PropertyName("downloads") var downloads: Int = 0,
    @get: PropertyName("likes") var likes: MutableList<String> = mutableListOf(),
    @get: PropertyName("likeCounter") var likeCounter: Int = 0,
    @get: PropertyName("commentCounter") var commentCounter: Int = 0,
    @get: Exclude @set: Exclude var imageReference: StorageReference? = null,
    @get: Exclude @set: Exclude var nomeCreatore: String? = null)