package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeNetwork

class CookbookViewModel : LoadingManagerViewModel() {

    val dataSource: RecipeNetwork = RecipeNetwork.getDataSource()
    val recipesLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData(mutableListOf())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        /* Ottiene dalla repository una lista di ricette e aggiorna il LiveData.
        * Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori. */
        recipesLiveData.postValue(dataSource.getRecipesByUser())
    }
}