package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import it.project.houseoftasty.model.Product
import it.project.houseoftasty.network.ProductNetwork

class ProductViewModel : LoadingManagerViewModel(){
    private val dataSource: ProductNetwork = ProductNetwork.getDataSource()
    val productLiveData: MutableLiveData<MutableList<Product>> = MutableLiveData(mutableListOf())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "productsLiveData"
    override suspend fun initAsync() {
        // Ottiene dalla repository la lista dei prodotti e aggiorna il LiveData
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        productLiveData.postValue(dataSource.getProductByUser())
    }



}