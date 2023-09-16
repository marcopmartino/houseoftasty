package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.project.houseoftasty.model.Product
import it.project.houseoftasty.repository.ProductRepository

class ProductViewModel : LoadingManagerViewModel(){
    var id: String = ""
    var nome: String = ""
    var quantita: String = ""
    var unitaMisura: String = ""
    var scadenza: String = ""

    private val dataSource: ProductRepository = ProductRepository.getDataSource()
    val productLiveData: MutableLiveData<MutableList<Product>> = MutableLiveData(mutableListOf())


    fun setData(id: String, nome:String, quantita: String, unitaMisura:String, scadenza:String){
        this.id = id
        this.nome = nome
        this.quantita = quantita
        this.unitaMisura = unitaMisura
        this.scadenza = scadenza
    }

    fun loadData(nome: String, quantita: String, scadenza: String, unitaMisura: String){
        this.nome = nome
        this.quantita = quantita
        this.scadenza = scadenza
        this.unitaMisura = unitaMisura
    }

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        // Ottiene dalla repository la lista delle ricette e aggiorna il LiveData
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        productLiveData.postValue(dataSource.getProductList())
    }



}