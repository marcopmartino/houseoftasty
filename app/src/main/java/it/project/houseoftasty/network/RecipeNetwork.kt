package it.project.houseoftasty.network

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.model.Comment
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.utility.getCurrentUserIdOrAndroidId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

open class RecipeNetwork : StorageNetwork("immagini_ricette/") {

    val currentUserId: String = FirebaseAuth.getInstance().getCurrentUserIdOrAndroidId()
    private val recipesReference: CollectionReference =
        FirebaseFirestore.getInstance().collection("recipes")

    // Ritorna tutti i documenti sulle ricette di un dato utente
    suspend fun getRecipeDocumentsByUser(userId: String = currentUserId): MutableList<DocumentSnapshot> {
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sulle ricette (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = recipesReference.whereEqualTo("idCreatore", userId).get().await().documents
        }

        return documents
    }

    // Ritorna tutti i dati sulle ricette di un dato utente
    suspend fun getRecipesByUser(userId: String = currentUserId, getImageReferences: Boolean = true): MutableList<Recipe> {
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sulle ricette (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = recipesReference.whereEqualTo("idCreatore", userId).get().await().documents
        }

        return documents.toRecipeList(getImageReferences)
    }

    // Ritorna tutti i dati sulle ricette di un dato utente
    suspend fun getPublicRecipesByUser(userId: String = currentUserId, getImageReferences: Boolean = true): MutableList<Recipe> {
        lateinit var documents: MutableList<DocumentSnapshot>

        if (userId == currentUserId)
            // Recupera i dati sulle ricette pubblicate (richiede la connessione a Firestore)
            withContext(Dispatchers.IO) {
                documents = recipesReference.whereEqualTo("idCreatore", userId)
                    .whereEqualTo("boolPubblicata", true).get().await().documents
            }
        else
            // Recupera i dati sulle ricette pubblicate (richiede la connessione a Firestore)
            withContext(Dispatchers.IO) {
                documents = recipesReference.whereEqualTo("idCreatore", userId)
                    .whereEqualTo("boolPubblicata", true)
                    .whereEqualTo("boolPostPrivato", false).get().await().documents
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
    suspend fun getRecipeById(recipeId: String, getImageReference: Boolean = true, getCreatorName: Boolean = false): Recipe {
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

                // Prende l'username del creatore della ricetta
                if (getCreatorName)
                    recipe.nomeCreatore = ProfileNetwork.getDataSource().getUserUsername(recipe.idCreatore.toString())
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

    // Rende pubblica una ricetta
    suspend fun publishRecipe(recipeId: String) {

        withContext(Dispatchers.IO) {
            recipesReference.document(recipeId).update("timestampPubblicazione", Timestamp.now()).await()
            recipesReference.document(recipeId).update("boolPubblicata", true).await()
        }

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
            val documentReference = recipesReference.document(recipeId)
            if (documentReference.get().await().get("boolImmagine") == true)
                deleteFile(recipeId)
            documentReference.delete().await()
        }
    }

    // Converte una lista di DocumentSnapshot in una lista di oggetti Recipe e la ritorna
    private suspend fun MutableList<DocumentSnapshot>.toRecipeList(getImageReferences: Boolean = true, getCreatorName: Boolean = false): MutableList<Recipe> {

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

                        // Prende l'username del creatore della ricetta
                        if (getCreatorName)
                            recipe.nomeCreatore = ProfileNetwork.getDataSource().getUserUsername(
                                recipe.idCreatore.toString())

                        // Inserisce la ricetta in lista
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
                        // Prende l'username del creatore della ricetta
                        if (getCreatorName)
                            recipe.nomeCreatore = ProfileNetwork.getDataSource().getUserUsername(
                                recipe.idCreatore.toString())

                        // Inserisce la ricetta in lista
                        recipeList.add(recipe)


                    }
                }
            }
        }

        return recipeList
    }

    // Aggiunge un like ad una ricetta di cui è noto l'id
    @Suppress("UNCHECKED_CAST")
    suspend fun addLike(recipeId: String){
        withContext(Dispatchers.IO){
            val likes: List<String?> = recipesReference.document(recipeId).get().await().get("likes") as List<String?>
            for(user in likes){
                if (currentUserId == user) return@withContext
            }
            recipesReference.document(recipeId).update("likes", FieldValue.arrayUnion(currentUserId)).await()
            recipesReference.document(recipeId).update("likeCounter", FieldValue.increment(1)).await()
        }
    }

    // Rimuove un like da una ricetta di cui è noto l'id
    @Suppress("UNCHECKED_CAST")
    suspend fun removeLike(recipeId: String){
        withContext(Dispatchers.IO){
            val likes: List<String?> = recipesReference.document(recipeId).get().await().get("likes") as List<String?>
            for(user in likes){
                if (currentUserId == user) {
                    recipesReference.document(recipeId).update("likes", FieldValue.arrayRemove(currentUserId)).await()
                    recipesReference.document(recipeId).update("likeCounter", FieldValue.increment(-1)).await()
                    return@withContext
                }
            }
        }
    }

    // Aggiunge una ricetta alla collezione "Salvati" e incrementa il numero dei download della stessa
    @Suppress("UNCHECKED_CAST")
    suspend fun addDownload(recipeId: String) {
        withContext(Dispatchers.IO) {
            val downloads: List<String?> =
                recipesReference.document(recipeId).get().await().get("downloads") as List<String?>
            if (downloads.contains(currentUserId)) return@withContext
            val collectionNetwork = RecipeCollectionNetwork()
            collectionNetwork.addRecipeToSaveCollection(recipeId)
            recipesReference.document(recipeId)
                .update("downloads", FieldValue.arrayUnion(currentUserId)).await()
            recipesReference.document(recipeId).update("downloadCounter", FieldValue.increment(1))
                .await()
        }
    }

    // Rimuove una ricetta dalla collezione "Salvati" di un utente e decrementa il numero dei download della stessa
    @Suppress("UNCHECKED_CAST")
    suspend fun removeDownload(recipeId: String){
        withContext(Dispatchers.IO){
            val downloads: List<String?> = recipesReference.document(recipeId).get().await().get("downloads") as List<String?>
            for(user in downloads){
                if (currentUserId == user) {
                    val collectionNetwork = RecipeCollectionNetwork()
                    collectionNetwork.removeRecipeFromCollections(recipeId)
                    recipesReference.document(recipeId).update("downloads", FieldValue.arrayRemove(currentUserId)).await()
                    recipesReference.document(recipeId).update("downloadCounter", FieldValue.increment(-1)).await()
                    return@withContext
                }
            }
        }
    }

    // Aggiunge un commento ad una ricetta di cui è noto l'id
    suspend fun addComment(recipeId: String, text: String): Comment{
        val comment = Comment(null, currentUserId, text, Timestamp.now(), "Tu")

        withContext(Dispatchers.IO){
            comment.id = recipesReference.document(recipeId)
                .collection("comments")
                .add(comment).await().id
            comment.imageReference = ProfileNetwork.getDataSource().getFileReference(currentUserId)
            recipesReference.document(recipeId).update("commentCounter", FieldValue.increment(1)).await()
        }

        return comment
    }

    // Rimuove un commento di una ricetta di cui è noto l'id
    suspend fun removeComment(recipeId: String, commentId: String) {
        withContext(Dispatchers.IO){
            recipesReference.document(recipeId)
                .collection("comments").document(commentId).delete()
            recipesReference.document(recipeId).update("commentCounter", FieldValue.increment(-1)).await()
        }
    }

    // Rimuove tutti i commenti di una ricetta di cui è noto l'id
    suspend fun removeComments(recipeId: String) {
        var documents: MutableList<DocumentSnapshot>
        val commentsReference: CollectionReference =
            recipesReference.document(recipeId).collection("comments")

        withContext(Dispatchers.IO){
            documents = commentsReference.get().await().documents

            for (document in documents) {
                commentsReference.document(document.id).delete()
            }

            recipesReference.document(recipeId).update("commentCounter",0)
        }

    }

    // Incrementa le visualizzazioni di una ricetta
    suspend fun incrementViews(recipeId: String){
        withContext(Dispatchers.IO){
            val recipe = recipesReference.document(recipeId).get().await().toObject(Recipe::class.java)
            if(!recipe!!.idCreatore.equals(currentUserId))
                recipesReference.document(recipeId).update("views", FieldValue.increment(1)).await()
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
                if (document["idCreatore"].toString() != currentUserId &&
                    document["boolPubblicata"] as Boolean &&
                    !(document["boolPostPrivato"] as Boolean) &&
                    document["titolo"].toString().contains(keyWord, true)) {
                    val recipe = document.toObject(Recipe::class.java)
                    if (recipe != null) {

                        // Prende l'username del creatore della ricetta
                        recipe.nomeCreatore = ProfileNetwork.getDataSource().getUserUsername(
                            recipe.idCreatore.toString())

                        // Prende un riferimento al file immagine della ricetta (non scarica il file)
                        recipe.imageReference = getFileReference(recipe.id.toString())

                        recipeList.add(recipe)
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
            documents = recipesReference
                .orderBy("likeCounter", Query.Direction.DESCENDING)
                .get().await().documents
        }

        withContext(Dispatchers.Default) {

            // Inizializzo la lista
            recipeList = mutableListOf()

            for (document in documents) {
                if (document["idCreatore"].toString() != currentUserId &&
                    document["boolPubblicata"] as Boolean &&
                    !(document["boolPostPrivato"] as Boolean)) {
                    val recipe = document.toObject(Recipe::class.java)
                    if (recipe != null) {

                        // Prende l'username del creatore della ricetta
                        recipe.nomeCreatore = ProfileNetwork.getDataSource().getUserUsername(
                            recipe.idCreatore.toString())

                        // Prende un riferimento al file immagine della ricetta (non scarica il file)
                        recipe.imageReference = getFileReference(recipe.id.toString())

                        recipeList.add(recipe)
                    }
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
            documents = recipesReference
                .orderBy("timestampPubblicazione", Query.Direction.DESCENDING).limit(5)
                .get().await().documents
        }

        withContext(Dispatchers.Default) {

            // Inizializzo la lista
            recipeList = mutableListOf()

            for (document in documents) {
                if (document["idCreatore"].toString() != currentUserId  &&
                    document["boolPubblicata"] as Boolean &&
                    !(document["boolPostPrivato"] as Boolean)) {
                    val recipe = document.toObject(Recipe::class.java)
                    if (recipe != null) {

                        // Prende l'username del creatore della ricetta
                        recipe.nomeCreatore = ProfileNetwork.getDataSource().getUserUsername(
                            recipe.idCreatore.toString())

                        // Prende un riferimento al file immagine della ricetta (non scarica il file)
                        recipe.imageReference = getFileReference(recipe.id.toString())

                        recipeList.add(recipe)
                    }
                }
            }
        }

        return recipeList
    }

    // Ritorna una lista delle ricette ordinate in base alla date di creazione (dalla più recente alla più vecchia) fino ad un massimo di 5 ricette
    suspend fun getMostRecentHome(): MutableList<Recipe>{
        lateinit var recipeList: MutableList<Recipe>
        lateinit var documents: MutableList<DocumentSnapshot>


        withContext(Dispatchers.IO){
            documents = recipesReference
                .orderBy("timestampPubblicazione", Query.Direction.DESCENDING).limit(5)
                .get().await().documents
        }

        withContext(Dispatchers.Default) {

            // Inizializzo la lista
            recipeList = mutableListOf()

            if(!currentUserId.isNullOrEmpty()){
                for (document in documents) {
                    if (document["idCreatore"].toString() != currentUserId  &&
                        document["boolPubblicata"] as Boolean &&
                        !(document["boolPostPrivato"] as Boolean)) {
                        val recipe = document.toObject(Recipe::class.java)
                        if (recipe != null) {

                            // Prende l'username del creatore della ricetta
                            recipe.nomeCreatore = ProfileNetwork.getDataSource().getUserUsername(
                                recipe.idCreatore.toString())

                            // Prende un riferimento al file immagine della ricetta (non scarica il file)
                            recipe.imageReference = getFileReference(recipe.id.toString())

                            recipeList.add(recipe)
                        }
                    }
                }
            }else{
                for (document in documents) {
                    if (document["boolPubblicata"] as Boolean &&
                        !(document["boolPostPrivato"] as Boolean)) {
                        val recipe = document.toObject(Recipe::class.java)
                        if (recipe != null) {

                            // Prende l'username del creatore della ricetta
                            recipe.nomeCreatore = ProfileNetwork.getDataSource().getUserUsername(
                                recipe.idCreatore.toString())

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


    // Ritorna la lista dei commenti di una ricetta di cui è noto l'id
    suspend fun getCommentsByRecipeId(recipeId: String): MutableList<Comment>{
        val usersReference: CollectionReference =
            FirebaseFirestore.getInstance().collection("users")
        val commentList : MutableList<Comment> = mutableListOf()
        lateinit var documents : MutableList<DocumentSnapshot>

        withContext(Dispatchers.IO){
            documents = recipesReference.document(recipeId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING).get().await().documents
        }

        withContext(Dispatchers.Default){
            for(document in documents){
                document.data.also {
                    if (it != null) {
                        val comment = Comment(
                            document.id,
                            it["userId"].toString(),
                            it["text"].toString(),
                            it["timestamp"] as Timestamp)
                        val userId = comment.userId
                        if (!userId.isNullOrEmpty()) {
                            comment.imageReference = ProfileNetwork.getDataSource().getFileReference(userId)
                            comment.userUsername =
                                if (userId == currentUserId)
                                    "Tu"
                                else
                                    usersReference
                                        .document(userId).get().await().get("username").toString()
                            commentList.add(comment)
                        }
                    }
                }

            }
        }

        return commentList
    }

    fun isCreator(recipeId: String): Boolean{
        lateinit var documents: DocumentSnapshot

        // Recupera i dati sulla ricetta (richiede la connessione a Firestore)
        runBlocking {
            documents = recipesReference.document(recipeId).get().await()
        }

        return documents["idCreatore"]!! == currentUserId

    }

    /* Factory method */
    companion object {
        private var INSTANCE: RecipeNetwork? = null
        private var NEW_INSTANCE = RecipeNetwork()

        fun getDataSource(newInstance: Boolean = true): RecipeNetwork {
            return synchronized(RecipeNetwork::class) {
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
