package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeNetwork
import kotlinx.coroutines.launch

class RecipePublishViewModel : LoadingManagerViewModel() {

    private val dataSource: RecipeNetwork = RecipeNetwork.getDataSource()
    val recipesLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData(mutableListOf())
    val publicationCompleted: MutableLiveData<Boolean> = MutableLiveData(false)

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        /* Ottiene dalla repository una lista di ricette e aggiorna il LiveData.
        * Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori. */
        recipesLiveData.postValue(dataSource.getUnpublishedRecipesByUser())
    }

    fun publishRecipe(recipeId: String) {
        startLoadingAndDo {
            viewModelScope.launch {
                dataSource.publishRecipe(recipeId)
                publicationCompleted.postValue(true)
            }
        }
    }

}