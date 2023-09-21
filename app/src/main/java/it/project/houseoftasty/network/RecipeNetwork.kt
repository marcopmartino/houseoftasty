package it.project.houseoftasty.network

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

open class RecipeNetwork : StorageNetwork("immagini_ricette/") {

    protected val currentUserId: String = FirebaseAuth.getInstance().currentUser!!.uid
    private val recipesReference: CollectionReference =
        FirebaseFirestore.getInstance().collection("recipes")

    // Ritorna tutti i dati sulle ricette di un dato utente
    suspend fun getRecipesByUser(userId: String = currentUserId, getImageReferences: Boolean = true): MutableList<Recipe> {
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sulle ricette (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = recipesReference.whereEqualTo("idCreatore", userId).get().await().documents
        }

        return documents.toRecipeList(getImageReferences)
    }

    // Ritorna tutti i dati sulle ricette non pubblicate di un dato utente
    suspend fun getUnpublishedRecipesByUser(userId: String = currentUserId, getImageReferences: Boolean = true): MutableList<Recipe> {
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sulle ricette non pubblicata (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = recipesReference.whereEqualTo("idCreatore", userId)
                .whereEqualTo("boolPubblicata", false).get().await().documents
        }

        return documents.toRecipeList(getImageReferences)
    }

    // Ritorna tutti i dati sulle ricette di un dato utente
    suspend fun getMissingRecipesByUser(recipeIds: MutableList<String>): MutableList<Recipe> {
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sulle ricette mancanti dalla lista (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = recipesReference.whereEqualTo("idCreatore", currentUserId).get().await().documents
        }

        //Inizializzo la lista di ricette
        val recipeList: MutableList<Recipe> = mutableListOf()

        withContext(Dispatchers.Default) {

            for (document in documents) {
                // Prendo solo le ricette che non sono tra quelle in lista
                if(!recipeIds.contains(document.id)) {

                    // Converto il documento in un oggetto Recipe
                    val recipe = document.toObject(Recipe::class.java)

                    if (recipe != null) {
                        // Prende un riferimento al file immagine della ricetta (non scarica il file)
                        recipe.imageReference = getFileReference(recipe.id.toString())
                        recipeList.add(recipe)
                    }
                }

            }
        }

        return recipeList
    }

    // Ritorna tutti i dati sulla ricetta di cui è noto l'id
    suspend fun getRecipeById(recipeId: String, getImageReference: Boolean = true): Recipe {
        lateinit var recipe : Recipe
        lateinit var document : DocumentSnapshot

        // Recupera i dati sulla ricetta (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = recipesReference.document(recipeId).get().await()
        }

        // Converte lo snapshot del documento in un oggetto Recipe
        document.toObject(Recipe::class.java).also {
            if (it != null) {
                recipe = it

                // Prende un riferimento al file immagine della ricetta (non scarica il file)
                if (getImageReference && recipe.boolImmagine)
                    recipe.imageReference = getFileReference(recipe.id.toString())
            }
        }

        return recipe
    }

    // Ritorna tutti i dati sulla ricetta di cui è noto l'id
    suspend fun getRecipesByIds(recipeIds: MutableList<String>, getImageReferences: Boolean = true): MutableList<Recipe> {
        val documents: MutableList<DocumentSnapshot> = mutableListOf()

        // Recupera i dati sulle ricette (richiede la connessione a Firestore)
        for (recipeId in recipeIds) {
            withContext(Dispatchers.IO) {
                // Ottiene il documento e lo inserisce in lista
                documents.add(recipesReference.document(recipeId).get().await())
            }
        }

        return documents.toRecipeList(getImageReferences)
    }

    // Ritorna una MutableMap con gli id e i titoli delle ricette
    suspend fun getRecipeNamesByIds(recipeIds: MutableList<String>): MutableMap<String, String> {
        val recipeIdNameMap : MutableMap<String, String> = mutableMapOf()
        lateinit var document : DocumentSnapshot

        for (recipeId in recipeIds) {
            // Recupera i dati sulla ricetta (richiede la connessione a Firestore)
            withContext(Dispatchers.IO) {
                document = recipesReference.document(recipeId).get().await()
            }
            recipeIdNameMap[recipeId] = document.get("titolo") as String
        }

        return recipeIdNameMap
    }

    suspend fun addRecipe(recipe: Recipe): String {
        lateinit var newRecipeId: String

        recipe.idCreatore = currentUserId

        withContext(Dispatchers.IO) {
            newRecipeId = recipesReference.add(recipe).await().id
        }

        return newRecipeId
    }

    // Aggiorna i dati di una ricetta
    suspend fun updateRecipe(recipe: Recipe) {
        withContext(Dispatchers.IO) {
            recipesReference.document(recipe.id.toString()).set(recipe).await()
        }
    }

    // Elimina una ricetta a partire dal suo id
    open suspend fun deleteRecipeById(recipeId: String) {
        withContext(Dispatchers.IO) {
            recipesReference.document(recipeId).delete().await()
            deleteFile(recipeId)
        }
    }

    // Converte una lista di DocumentSnapshot in una lista di oggetti Recipe e la ritorna
    private suspend fun MutableList<DocumentSnapshot>.toRecipeList(getImageReferences: Boolean = true): MutableList<Recipe> {

        // Inizializzo la lista
        val recipeList: MutableList<Recipe> = mutableListOf()

        if (getImageReferences) {
            // Converte gli snapshot dei documenti in oggetti Recipe e li aggiunge alla lista "recipeList"
            for (document in this) {

                withContext(Dispatchers.Default) {

                    // Converto il documento in un oggetto Recipe
                    val recipe = document.toObject(Recipe::class.java)

                    if (recipe != null) {
                        // Prende un riferimento al file immagine della ricetta (non scarica il file)
                        recipe.imageReference = getFileReference(recipe.id.toString())
                        recipeList.add(recipe)
                    }
                }
            }
        } else {
            // Converte gli snapshot dei documenti in oggetti Recipe e li aggiunge alla lista "recipeList"
            for (document in this) {

                withContext(Dispatchers.Default) {

                    // Converto il documento in un oggetto Recipe
                    val recipe = document.toObject(Recipe::class.java)

                    if (recipe != null) {
                        // Inserisce la ricetta in lista
                        recipeList.add(recipe)
                    }
                }
            }
        }

        return recipeList
    }

    // Aggiunge un like ad una ricetta di cui è noto l'id
    suspend fun addLike(recipeId: String){
        withContext(Dispatchers.IO){
            val like: List<String?> = recipesReference.document(recipeId).get().await().get("likes") as List<String?>
            for(user in like){
                if(currentUserId == user) return@withContext
            }
            recipesReference.document(recipeId).update("likes", FieldValue.arrayUnion(currentUserId)).await()
            recipesReference.document(recipeId).update("likesCounter", FieldValue.increment(1))
        }
    }

    // Rimuove un like da una ricetta di cui è noto l'id
    suspend fun removeLike(recipeId: String){
        withContext(Dispatchers.IO){
            val like: List<String?> = recipesReference.document(recipeId).get().await().get("likes") as List<String?>
            for(user in like){
                if(currentUserId != user) return@withContext
            }
            recipesReference.document(recipeId).update("likes", FieldValue.arrayRemove(currentUserId)).await()
            recipesReference.document(recipeId).update("likesCounter", FieldValue.increment(-1))
        }
    }

    // Effettua la ricerca di una ricetta a patire da una parola chiave
    suspend fun getSearchList(keyWord: String?): MutableList<Recipe>{
        lateinit var recipeList: MutableList<Recipe>
        lateinit var documents: MutableList<DocumentSnapshot>

        withContext(Dispatchers.IO){
            documents = recipesReference.get().await().documents
        }

        withContext(Dispatchers.Default) {

            // Inizializzo la lista
            recipeList = mutableListOf()

            if(keyWord.isNullOrEmpty()) return@withContext recipeList

            for (document in documents) {
                if(document["titolo"].toString().contains(keyWord, true)){
                    if(document["boolPubblicata"] == true) {
                        val recipe = document.toObject(Recipe::class.java)
                        if (recipe != null) {

                            // Prende un riferimento al file immagine della ricetta (non scarica il file)
                            recipe.imageReference = getFileReference(recipe.id.toString())

                            recipeList.add(recipe)
                        }
                    }
                }
            }
        }
        return recipeList
    }

    // Ritorna una lista delle ricette ordinate in base al numero di mi piace
    suspend fun getMostLiked(): MutableList<Recipe>{
        lateinit var recipeList: MutableList<Recipe>
        lateinit var documents: MutableList<DocumentSnapshot>


        withContext(Dispatchers.IO){
            documents = recipesReference.orderBy("likeCounter", Query.Direction.DESCENDING).get().await().documents
        }

        withContext(Dispatchers.Default){
            recipeList = mutableListOf()
            for (document in documents) {
                val recipe = document.toObject(Recipe::class.java)
                if (recipe != null) {

                    // Prende un riferimento al file immagine della ricetta (non scarica il file)
                    recipe.imageReference = getFileReference(recipe.id.toString())

                    recipeList.add(recipe)
                }
            }
        }

        return recipeList
    }

    // Ritorna una lista delle ricette ordinate in base alla date di creazione (dalla più recente alla più vecchia)
    suspend fun getMostRecent(): MutableList<Recipe>{
        lateinit var recipeList: MutableList<Recipe>
        lateinit var documents: MutableList<DocumentSnapshot>


        withContext(Dispatchers.IO){
            documents = recipesReference.orderBy("timestampCreazione", Query.Direction.DESCENDING).get().await().documents
        }

        withContext(Dispatchers.Default){
            recipeList = mutableListOf()
            for (document in documents) {
                val recipe = document.toObject(Recipe::class.java)
                if (recipe != null) {

                    // Prende un riferimento al file immagine della ricetta (non scarica il file)
                    recipe.imageReference = getFileReference(recipe.id.toString())

                    recipeList.add(recipe)
                }
            }
        }

        return recipeList
    }

    /* Factory method */
    companion object {
        private var INSTANCE: RecipeNetwork? = null

        fun getDataSource(): RecipeNetwork {
            return synchronized(RecipeNetwork::class) {
                val newInstance = INSTANCE ?: RecipeNetwork()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}
