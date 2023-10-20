package it.project.houseoftasty.network

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.model.RecipeCollection
import it.project.houseoftasty.utility.removeIfContains
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RecipeCollectionNetwork: RecipeNetwork() {
    private var recipeCollectionsReference: CollectionReference =
        FirebaseFirestore.getInstance().collection("users").document(currentUserId)
            .collection("collections")

    // Ritorna tutti i documenti sulle raccolte dell'utente attuale
    suspend fun getRecipeCollectionDocumentsByCurrentUser(): MutableList<DocumentSnapshot> {
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sulle raccolte (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = recipeCollectionsReference.get().await().documents
        }

        return documents
    }

    // Ritorna tutti i dati sulle raccolte dell'utente attuale
    @Suppress("UNCHECKED_CAST")
    suspend fun getRecipeCollectionsByCurrentUser(): MutableList<RecipeCollection> {
        lateinit var recipeCollectionList: MutableList<RecipeCollection>
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sulle raccolte (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = recipeCollectionsReference.get().await().documents
        }

        // Converte gli snapshot dei documenti in oggetti Recipe e li aggiunge alla lista "recipeCollectionList"
        withContext(Dispatchers.Default) {

            // Inizializzo la lista
            recipeCollectionList = mutableListOf()

            for (document in documents) {
                Log.d("Document", document.toString())
                document.toObject(RecipeCollection::class.java).also {
                    if (it != null) {
                        val recipeIdList: List<String> = document
                            .get("listaRicette") as List<String>

                        for(recipeId in recipeIdList)
                            it.listaRicette.add(getRecipeById(recipeId))
                        recipeCollectionList.add(it)
                    }
                }
            }
        }

        return recipeCollectionList
    }

    // Ritorna tutti i dati sulla ricetta di cui è noto l'id
    @Suppress("UNCHECKED_CAST")
    suspend fun getCollectionById(collectionId: String): RecipeCollection {
        lateinit var recipeCollection : RecipeCollection
        lateinit var document : DocumentSnapshot

        // Recupera i dati sulla raccolta (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = recipeCollectionsReference.document(collectionId).get().await()
        }

        // Converte lo snapshot del documento in un oggetto RecipeCollection
        withContext(Dispatchers.Default) {

            document.toObject(RecipeCollection::class.java).also {
                if (it != null) {
                    recipeCollection = it
                    val recipeIdList: List<String> = document.get("listaRicette") as List<String>
                    for(recipeId in recipeIdList)
                        recipeCollection.listaRicette.add(getRecipeById(recipeId, getCreatorName = true))


                }
            }
        }

        return recipeCollection
    }

    // Ritorna il nome di una collection dato il suo id
    @Suppress("UNCHECKED_CAST")
    suspend fun getCollectionRawDataById(collectionId: String): MutableMap<String, Any> {
        lateinit var document: DocumentSnapshot
        val collectionMap: MutableMap<String, Any> = mutableMapOf()

        // Recupera i dati sulle raccolte (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = recipeCollectionsReference.document(collectionId).get().await()
        }

        collectionMap["nome"] = document.get("nome") as String
        collectionMap["ricette"] = document.get("listaRicette") as MutableList<String>

        return collectionMap
    }

    // Ritorna tutti i dati sulla ricetta di cui è noto l'id
    @Suppress("UNCHECKED_CAST")
    suspend fun getMissingRecipesInCollection(collectionId: String): MutableList<Recipe> {
        var recipeIds: MutableList<String>
        lateinit var document : DocumentSnapshot

        // Recupera i dati sulla raccolta (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = recipeCollectionsReference.document(collectionId).get().await()
        }

        // Recupera gli Id delle ricette contenute nella raccolta
        withContext(Dispatchers.Default) {
            recipeIds = document.get("listaRicette") as MutableList<String>
        }

        return getMissingRecipesByUser(recipeIds)
    }

    suspend fun addCollection(collection: RecipeCollection): String {
        lateinit var newCollectionId: String

        withContext(Dispatchers.IO) {
            newCollectionId = recipeCollectionsReference.add(collection).await().id
        }

        return newCollectionId
    }

    private suspend fun createSaveCollection(collection: RecipeCollection) {
        withContext(Dispatchers.IO) {
            recipeCollectionsReference.document("saveCollection").set(collection).await()
        }
    }

    suspend fun updateCollectionName(collectionId: String, name: String) {
        withContext(Dispatchers.IO) {
            recipeCollectionsReference.document(collectionId)
                .update("nome", name).await()
        }
    }

    suspend fun updateCollectionRecipeList(collectionId: String, recipeList: MutableList<String>) {
        withContext(Dispatchers.IO) {
            recipeCollectionsReference.document(collectionId)
                .update("listaRicette", recipeList).await()
        }
    }

    suspend fun deleteCollectionById(collectionId: String) {
        withContext(Dispatchers.IO) {
            recipeCollectionsReference.document(collectionId).delete().await()
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun removeRecipeFromCollections(recipeId: String) {
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sulle raccolte (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = recipeCollectionsReference.get().await().documents
        }

        for (document in documents) {
            val recipeIds = document.get("listaRicette") as MutableList<String>
            recipeIds.removeIfContains(recipeId)
            recipeCollectionsReference.document(document.id).update("listaRicette", recipeIds)
        }
    }

    suspend fun removeRecipeFromSaveCollections(recipeId: String) {
        lateinit var userDocuments: MutableList<DocumentSnapshot>
        val usersReference = FirebaseFirestore.getInstance().collection("users")

        withContext(Dispatchers.IO) {
            userDocuments = usersReference.get().await().documents
        }

        for (userDocument in userDocuments) {

            // Recupera i dati sulle raccolte (richiede la connessione a Firestore)
            withContext(Dispatchers.IO) {
                usersReference.document(userDocument.id).collection("collections")
                    .document("saveCollection")
                    .update("listaRicette", FieldValue.arrayRemove(recipeId))
            }
        }
    }

    override suspend fun deleteRecipeById(recipeId: String) {
        super.deleteRecipeById(recipeId)
        removeRecipeFromCollections(recipeId)
        removeRecipeFromSaveCollections(recipeId)
    }

    // Elimina tutte le ricette
    suspend fun deleteAllRecipes() {
        getRecipeDocumentsByUser().also {
            for (document in it)
                deleteRecipeById(document.id)
        }
    }

    suspend fun deleteAllCollections() {
        getRecipeCollectionDocumentsByCurrentUser().also {
            for (document in it)
                deleteCollectionById(document.id)
        }
    }

    private suspend fun removeRecipeFromCollection(recipeId: String) {
        withContext(Dispatchers.IO){
            recipeCollectionsReference.document("saveCollection")
                .update("listaRicette",FieldValue.arrayRemove(recipeId))
        }
    }

    suspend fun removeRecipeById(recipeId: String){
        removeRecipeFromCollection(recipeId)
    }

    suspend fun addRecipeToSaveCollection(recipeId: String){
        val saveCollectionReference = recipeCollectionsReference.document("saveCollection")

        withContext(Dispatchers.IO) {
            saveCollectionReference.get().await().exists().also {
                if (!it)
                    createSaveCollection(RecipeCollection(null, "Salvati", Timestamp.now()))
            }
            saveCollectionReference.update("listaRicette", FieldValue.arrayUnion(recipeId))
        }
    }

    /* Factory method */
    companion object {
        private var INSTANCE: RecipeCollectionNetwork? = null
        private var NEW_INSTANCE = RecipeCollectionNetwork()

        fun getDataSource(newInstance: Boolean = true): RecipeCollectionNetwork {
            return synchronized(RecipeCollectionNetwork::class) {
                if (newInstance) NEW_INSTANCE
                else {
                    val instance = INSTANCE ?: NEW_INSTANCE
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
}