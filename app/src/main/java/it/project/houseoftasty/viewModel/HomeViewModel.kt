package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.model.Product
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.ProductNetwork
import it.project.houseoftasty.network.RecipeNetwork

class HomeViewModel : LoadingManagerViewModel() {

    private val recipeDataSource: RecipeNetwork = RecipeNetwork.getDataSource()
    private val productDataSource: ProductNetwork = ProductNetwork.getDataSource()
    val homeLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData(mutableListOf())
    val productLiveData: MutableLiveData<MutableList<Product>> = MutableLiveData(mutableListOf())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        // Ottiene dalla repository la lista delle ricette e aggiorna il LiveData
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        homeLiveData.postValue(recipeDataSource.getMostRecentHome())
        productLiveData.postValue(productDataSource.getExpireProduct())
    }
}