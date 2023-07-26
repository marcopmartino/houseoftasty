package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.Timestamp
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.repository.RecipeRepository
import it.project.houseoftasty.utility.OperationType
import it.project.houseoftasty.utility.toInt
import kotlinx.coroutines.launch

class RecipeFormViewModel(private val recipeId: String?, val isRecipeNew: MutableLiveData<Boolean>) : FormManagerViewModel() {
    private val dataSource: RecipeRepository = RecipeRepository.getDataSource()
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

    // Funzione eseguita al Submit della form
    override fun onFormSubmit(formData: MutableMap<String, Any>, hasDataChanged: Boolean) {
        viewModelScope.launch {
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

            dataSource.deleteRecipe(recipeId!!)
            setOperationCompleted(OperationType.DELETION)
        }
    }

    // Inserisce una nuova ricetta con i dati della form
    private suspend fun insertRecipe(formData: MutableMap<String, Any>) {
        Log.d("Insert", formData.toString())

        val isPublished = formData["boolPubblicata"] as Boolean
        dataSource.addRecipe(
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
                Timestamp.now(),
                if (isPublished) Timestamp.now() else null,
                null,
                null
            )
        )
    }

    // Aggiorna una ricetta esistente con i dati della form
    private suspend fun updateRecipe(formData: MutableMap<String, Any>) {
        Log.d("Update", formData.toString())

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
                oldData.timestampCreazione,
                if (!isPublished) null else if (wasPublished) oldData.timestampPubblicazione else Timestamp.now(),
                oldData.nomeImmagine,
                oldData.imageReference
            )
        )
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