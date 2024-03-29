package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeCollectionNetwork

class CollectionDetailsViewModel(private val collectionId: String) : LoadingManagerViewModel() {

    private val dataSource: RecipeCollectionNetwork = RecipeCollectionNetwork.getDataSource()
    val recipesLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData(mutableListOf())
    var actionBarTitle: MutableLiveData<String> = MutableLiveData("Dettagli Raccolta")
    var isSaveCollection: MutableLiveData<Boolean> = MutableLiveData(collectionId == "saveCollection")

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza le variabili "recipesLiveData" e "actionBarTitle"
    override suspend fun initAsync() {
        /* Ottiene dalla repository una lista di ricette e aggiorna i LiveData.
        * Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori. */
        val collection = dataSource.getCollectionById(collectionId)
        actionBarTitle.postValue("${collection.nome} (${collection.listaRicette.size})")
        recipesLiveData.postValue(collection.listaRicette)
    }

    fun getIsSaveCollection(): Boolean {
        return isSaveCollection.value == true
    }

}

// Factory class
class CollectionDetailsViewModelFactory(private val collectionId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectionDetailsViewModel(collectionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}