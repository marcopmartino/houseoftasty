package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeNetwork

class RecentViewModel : LoadingManagerViewModel() {

    val dataSource: RecipeNetwork = RecipeNetwork.getDataSource()
    val recentLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData(mutableListOf())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        // Ottiene dalla repository la lista delle ricette e aggiorna il LiveData
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        recentLiveData.postValue(dataSource.getMostRecent())
    }
}