package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.*
import it.project.houseoftasty.adapter.ProductAdapter
import it.project.houseoftasty.model.Product
import it.project.houseoftasty.repository.ProductRepository
import it.project.houseoftasty.utility.OperationType
import kotlinx.coroutines.launch

class ProductFormViewModel(private val productId: String?, val isProductNew: MutableLiveData<Boolean>) : FormManagerViewModel() {
    private val dataSource: ProductRepository = ProductRepository.getDataSource()
    val productLiveData: MutableLiveData<Product> = MutableLiveData(Product())
    var isScadenzaNull = true

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {

        if (!isProductNew.value!!) {
            // Ottiene la ricetta dalla repository e aggiorna il LiveData
            val product = productId?.let{dataSource.getProductById(it)}
            Log.d("checker", product!!.misura.toString())
            if(product!!.scadenza!!.isNotEmpty()){
                isScadenzaNull = false
                Log.d("checker", isScadenzaNull.toString())
            }
            // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
            productLiveData.postValue(product!!)
        }

    }

    // Funzione eseguita al Submit della form
    override fun onFormSubmit(formData: MutableMap<String, Any>, hasDataChanged: Boolean) {
        viewModelScope.launch {
            if (isProductNew.value == true) {
                insertProduct(formData)
                setOperationCompleted(OperationType.INSERTION)
            } else if (hasDataChanged) {
                updateProduct(formData)
                setOperationCompleted(OperationType.UPDATE)
            } else {
                setOperationCompleted(OperationType.NONE)
            }
        }
    }

    // Funzione eseguita alla pressione del pulsante di Delete della form
    override fun onFormDelete() {
        viewModelScope.launch {
            Log.d("Delete", productId.toString())

            dataSource.deleteProduct(productId!!)
            setOperationCompleted(OperationType.DELETION)
        }
    }

    // Inserisce una nuova ricetta con i dati della form
    private suspend fun insertProduct(formData: MutableMap<String, Any>) {
        Log.d("Insert", formData.toString())

        dataSource.addProduct(
            Product(
                ProductAdapter.createRandomId(),
                formData["nome"] as String,
                formData["quantita"] as String,
                formData["misura"] as String,
                formData["scadenza"] as String,
            )
        )
    }

    // Aggiorna una ricetta esistente con i dati della form
    private suspend fun updateProduct(formData: MutableMap<String, Any>) {
        Log.d("Update", formData.toString())

        val oldData = productLiveData.value!!
        
        dataSource.updateProduct(
            Product(
                oldData.id,
                formData["nome"] as String,
                formData["quantita"] as String,
                formData["misura"] as String,
                formData["scadenza"] as String,
            )
        )
    }
}

// Factory class
class ProductFormViewModelFactory(private val productId: String?) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductFormViewModel(productId, MutableLiveData(productId.isNullOrEmpty())) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}