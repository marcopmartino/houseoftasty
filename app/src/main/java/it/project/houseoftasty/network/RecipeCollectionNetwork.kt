package it.project.houseoftasty.network

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
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


    // Ritorna tutti i dati sulle raccolte di un dato utente
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
                        recipeCollection.listaRicette.add(getRecipeById(recipeId))


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

    override suspend fun deleteRecipeById(recipeId: String) {
        super.deleteRecipeById(recipeId)
        removeRecipeFromCollections(recipeId)
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun removeRecipeFromCollection(recipeId: String) {
        lateinit var documents: MutableList<DocumentSnapshot>
        var collection = ""

        withContext(Dispatchers.IO){
            documents = recipeCollectionsReference.get().await().documents
        }

        for(document in documents){
            val temp = document.toObject(RecipeCollection::class.java)
            if(temp!!.nome.equals("Salvati")){
                recipeCollectionsReference.document(temp.id!!)
                    .update("listaRicette", FieldValue.arrayRemove(recipeId))
                break
            }
        }
    }

    suspend fun removeRecipeById(recipeId: String){
        removeRecipeFromCollection(recipeId)
    }

    private suspend fun addRecipeIntoCollection(recipeId: String){
        lateinit var documents: MutableList<DocumentSnapshot>
        var collection = ""

        withContext(Dispatchers.IO){
            documents = recipeCollectionsReference.get().await().documents
        }

        for(document in documents){
            val temp = document.toObject(RecipeCollection::class.java)
            if(temp!!.nome.equals("Salvati")){
                collection = temp.id!!
                break
            }
        }

        if(collection.isEmpty()){
            collection = addCollection(RecipeCollection(
                null,
                "Salvati",
                Timestamp.now()))
        }

        withContext(Dispatchers.IO){
            recipeCollectionsReference.document(collection)
                .update("listaRicette", FieldValue.arrayUnion(recipeId))
        }
    }

    suspend fun addRecipeById(recipeId: String){
        addRecipeIntoCollection(recipeId)
    }


    /* Factory method */
    companion object {
        private var INSTANCE: RecipeCollectionNetwork? = null

        fun getDataSource(): RecipeCollectionNetwork {
            return synchronized(RecipeCollectionNetwork::class) {
                val newInstance = INSTANCE ?: RecipeCollectionNetwork()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}