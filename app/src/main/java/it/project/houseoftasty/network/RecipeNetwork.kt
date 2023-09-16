package it.project.houseoftasty.network

import com.google.firebase.firestore.DocumentSnapshot
import it.project.houseoftasty.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RecipeNetwork : FirebaseNetwork("recipes") {

    // Ritorna tutti i dati sulle ricette di un dato utente
    suspend fun getRecipesByUser(): MutableList<Recipe> {
        lateinit var recipeList: MutableList<Recipe>
        lateinit var documents: MutableList<DocumentSnapshot>

        // Recupera i dati sulle ricette (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            documents = firestoreReference.whereEqualTo("idCreatore", currentUserId).get().await().documents
        }

        // Converte gli snapshot dei documenti in oggetti Recipe e li aggiunge alla lista "recipeList"
        withContext(Dispatchers.Default) {

            // Inizializzo la lista
            recipeList = mutableListOf()

            for (document in documents) {
                val recipe = document.toObject(Recipe::class.java)
                if (recipe != null) {

                    // Prende un riferimento al file immagine della ricetta (non scarica il file)
                    recipe.imageReference = getFileReference("immagini_ricette/" + recipe.nomeImmagine)

                    recipeList.add(recipe)
                }
            }
        }

        return recipeList
    }

    // Ritorna tutti i dati sulla ricetta di cui è noto l'id
    suspend fun getRecipeById(recipeId: String): Recipe {
        lateinit var recipe : Recipe
        lateinit var document : DocumentSnapshot

        // Recupera i dati sulla ricetta (richiede la connessione a Firestore)
        withContext(Dispatchers.IO) {
            document = firestoreReference.document(recipeId).get().await()
        }

        // Converte lo snapshot del documento in un oggetto Recipe
        document.toObject(Recipe::class.java).also {
            if (it != null) {
                recipe = it
            }
        }

        // Prende un riferimento al file immagine della ricetta (non scarica il file)
        recipe.imageReference = getFileReference("immagini_ricette/" + recipe.nomeImmagine)

        return recipe
    }

    suspend fun addRecipe(recipe: Recipe) {
        recipe.idCreatore = currentUserId

        withContext(Dispatchers.IO) {
            firestoreReference.add(recipe).await()
        }
    }

    suspend fun updateRecipe(recipe: Recipe) {
        withContext(Dispatchers.IO) {
            firestoreReference.document(recipe.id.toString()).set(recipe).await()
        }
    }

    suspend fun deleteRecipeById(recipeId: String) {
        withContext(Dispatchers.IO) {
            firestoreReference.document(recipeId).delete().await()
        }
    }
}
