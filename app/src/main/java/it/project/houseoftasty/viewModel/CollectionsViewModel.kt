package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import it.project.houseoftasty.model.RecipeCollection
import it.project.houseoftasty.network.RecipeCollectionNetwork

class CollectionsViewModel : LoadingManagerViewModel() {

    private val dataSource: RecipeCollectionNetwork = RecipeCollectionNetwork.getDataSource()
    val collectionsLiveData: MutableLiveData<MutableList<RecipeCollection>> = MutableLiveData(mutableListOf())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "collectionsLiveData"
    override suspend fun initAsync() {
        // Ottiene dalla repository la lista delle raccolte e aggiorna il LiveData
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        collectionsLiveData.postValue(dataSource.getRecipeCollectionsByCurrentUser())
    }

}