package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeNetwork

class HomeViewModel : LoadingManagerViewModel() {

    val dataSource: RecipeNetwork = RecipeNetwork.getDataSource()
    val homeLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData(mutableListOf())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        // Ottiene dalla repository la lista delle ricette e aggiorna il LiveData
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        homeLiveData.postValue(dataSource.getMostRecentHome())
    }
}