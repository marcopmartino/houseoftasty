package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeCollectionNetwork
import it.project.houseoftasty.network.RecipeNetwork

class CollectionAddRecipeViewModel(private val recipeIds: MutableList<String>) : LoadingManagerViewModel() {

    private val dataSource: RecipeNetwork = RecipeNetwork.getDataSource()
    val recipesLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData(mutableListOf())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        /* Ottiene dalla repository una lista di ricette e aggiorna il LiveData.
        * Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori. */
        recipesLiveData.postValue(dataSource.getMissingRecipesByUser(recipeIds))
    }

    // Aggiunge l'id di una ricetta a "recipeIds"
    fun addRecipeId(recipeId: String) {
        recipeIds.add(recipeId)
    }

    // Ritorna "recipeIds"
    fun getRecipeIds(): Array<String> {
        return recipeIds.toTypedArray()
    }

}

// Factory class
class CollectionAddRecipeViewModelFactory(private val recipeIds: Array<String>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionAddRecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectionAddRecipeViewModel(recipeIds.toMutableList()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}