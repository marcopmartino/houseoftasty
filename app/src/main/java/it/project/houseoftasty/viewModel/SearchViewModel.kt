package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeNetwork
import kotlinx.coroutines.launch

class SearchViewModel : LoadingManagerViewModel() {

    val dataSource: RecipeNetwork = RecipeNetwork.getDataSource()
    val searchLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData(mutableListOf())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        // Ottiene dalla repository la lista delle ricette e aggiorna il LiveData
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        searchLiveData.postValue(dataSource.getSearchList(""))
    }

    fun searchRecipe(keyWord: String?) {
        viewModelScope.launch {
            searchLiveData.postValue(dataSource.getSearchList(keyWord))
        }
    }
}