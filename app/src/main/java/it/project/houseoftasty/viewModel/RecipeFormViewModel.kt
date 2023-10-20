package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeCollectionNetwork
import it.project.houseoftasty.network.RecipeNetwork
import it.project.houseoftasty.utility.OperationType
import it.project.houseoftasty.utility.toInt
import kotlinx.coroutines.launch

class RecipeFormViewModel(private val recipeId: String?, val isRecipeNew: MutableLiveData<Boolean>) : FormManagerViewModel() {
    private val dataSource: RecipeCollectionNetwork = RecipeCollectionNetwork.getDataSource()
    val recipeLiveData: MutableLiveData<Recipe> = MutableLiveData(Recipe())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {

        if (!isRecipeNew.value!!) {
            // Ottiene la ricetta dalla repository e aggiorna il LiveData
            // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
            recipeLiveData.postValue(recipeId?.let { dataSource.getRecipeById(it) })
        }

    }

    // Ritorna TRUE se esiste un'immagine per la ricetta, altrimenti ritorna FALSE
    fun recipeImageExists(): Boolean {
        return recipeLiveData.value?.boolImmagine ?: false
    }

    // Controlla se i campi in input sono cambiati (usata nel caso di UPDATE di una ricetta)
    override fun hasDataChanged(formData: MutableMap<String, Any>): Boolean {
        val oldData = recipeLiveData.value!!
        return oldData.titolo != formData["titolo"] as String ||
                oldData.ingredienti != formData["ingredienti"] as String ||
                oldData.numPersone != formData["numPersone"]?.toInt() ||
                oldData.preparazione != formData["preparazione"] as String ||
                oldData.tempoPreparazione != formData["tempoPreparazione"]?.toInt() ||
                oldData.boolPubblicata != formData["boolPubblicata"] as Boolean ||
                oldData.boolPostPrivato != formData["boolPostPrivato"] as Boolean ||
                "NONE" != formData["immagineOperationType"] as String
    }

    // Funzione eseguita al Submit della form
    override fun onFormSubmit(formData: MutableMap<String, Any>, hasDataChanged: Boolean) {
        viewModelScope.launch {
            Log.d("Immagine", formData["immagineOperationType"] as String)
            if (isRecipeNew.value == true) {
                insertRecipe(formData)
                setOperationCompleted(OperationType.INSERTION)
            } else if (hasDataChanged) {
                updateRecipe(formData)
                setOperationCompleted(OperationType.UPDATE)
            } else {
                setOperationCompleted(OperationType.NONE)
            }
        }
    }

    // Funzione eseguita alla pressione del pulsante di Delete della form
    override fun onFormDelete() {
        viewModelScope.launch {
            Log.d("Delete", recipeId.toString())

            dataSource.deleteRecipeById(recipeId!!)
            setOperationCompleted(OperationType.DELETION)
        }
    }

    // Inserisce una nuova ricetta con i dati della form
    private suspend fun insertRecipe(formData: MutableMap<String, Any>) {
        Log.d("Insert", formData.toString())

        val isPublished = formData["boolPubblicata"] as Boolean
        val isImageSelected = formData["immagineOperationType"] as String == "SELECTION"
        val newRecipeId = dataSource.addRecipe(
            Recipe(
                null,
                null,
                formData["titolo"] as String,
                formData["ingredienti"] as String,
                formData["numPersone"]?.toInt(),
                formData["preparazione"] as String,
                formData["tempoPreparazione"]?.toInt(),
                isPublished,
                formData["boolPostPrivato"] as Boolean,
                isImageSelected,
                Timestamp.now(),
                if (isPublished) Timestamp.now() else null,
            )
        )

        if (isImageSelected)
            dataSource.uploadFileFromByteArray(newRecipeId, formData["immagine"] as ByteArray)
    }

    // Aggiorna una ricetta esistente con i dati della form
    private suspend fun updateRecipe(formData: MutableMap<String, Any>) {
        Log.d("Update", formData.toString())

        val imageOperation = formData["immagineOperationType"] as String

        val oldData = recipeLiveData.value!!
        val wasPublished = oldData.boolPubblicata
        val isPublished = formData["boolPubblicata"] as Boolean
        dataSource.updateRecipe(
            Recipe(
                oldData.id,
                oldData.idCreatore,
                formData["titolo"] as String,
                formData["ingredienti"] as String,
                formData["numPersone"]?.toInt(),
                formData["preparazione"] as String,
                formData["tempoPreparazione"]?.toInt(),
                isPublished,
                formData["boolPostPrivato"] as Boolean,
                when (imageOperation) {
                "SELECTION" -> true
                "REMOVAL" -> false
                    else -> { oldData.boolImmagine }
                },
                oldData.timestampCreazione,
                if (!isPublished) null else if (wasPublished) oldData.timestampPubblicazione else Timestamp.now(),
                if (isPublished) oldData.views else 0,
                if (isPublished) oldData.downloads else mutableListOf(),
                if (isPublished) oldData.downloadCounter else 0,
                if (isPublished) oldData.likes else mutableListOf(),
                if (isPublished) oldData.likeCounter else 0,
                if (isPublished) oldData.commentCounter else 0,
            )
        )

        /* Se la ricetta è non più pubblicata, viene rimossa dalle raccolte di altri utenti che
        * l'avevano salvata e vengono rimossi i suoi commenti */
        if (!isPublished && wasPublished) {
            dataSource.removeRecipeFromSaveCollections(oldData.id.toString())
            dataSource.removeComments(oldData.id.toString())
        }

        // Aggiorna o rimuove l'immagine della ricetta
        when (imageOperation) {
            "SELECTION" -> dataSource.uploadFileFromByteArray(oldData.id.toString(), formData["immagine"] as ByteArray)
            "REMOVAL" -> dataSource.deleteFile(oldData.id.toString())
        }
    }
}

// Factory class
class RecipeFormViewModelFactory(private val recipeId: String?) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeFormViewModel(recipeId, MutableLiveData(recipeId.isNullOrEmpty())) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}