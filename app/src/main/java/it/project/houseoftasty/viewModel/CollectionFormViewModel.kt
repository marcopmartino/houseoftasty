package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.model.RecipeCollection
import it.project.houseoftasty.network.RecipeCollectionNetwork
import it.project.houseoftasty.utility.OperationType
import it.project.houseoftasty.utility.toInt
import it.project.houseoftasty.utility.update
import kotlinx.coroutines.launch

class CollectionFormViewModel(
    private val collectionId: String?,
    val isCollectionNew: MutableLiveData<Boolean>,
    private val safeArgCollectionName: String,
    private val safeArgRecipeIds: MutableList<String>,
    private val firstTimeLoadingFragment: Boolean
) : FormManagerViewModel() {
    private val dataSource: RecipeCollectionNetwork = RecipeCollectionNetwork.getDataSource()
    val collectionNameLiveData: MutableLiveData<String> = MutableLiveData(String())
    val recipeListLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData(mutableListOf())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza le variabili "collectionNameLiveData" e "recipeListLiveData"
    override suspend fun initAsync() {

        if (firstTimeLoadingFragment) {
            if (isCollectionNew.value == false) {

                // Ottiene la ricetta dalla repository e aggiorna il LiveData
                // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
                val collection = dataSource.getCollectionById(collectionId!!)
                collectionNameLiveData.postValue(collection.nome ?: String())
                recipeListLiveData.postValue(collection.listaRicette)
            }
        } else {
            collectionNameLiveData.postValue(safeArgCollectionName)
            recipeListLiveData.postValue(dataSource
                .getRecipesByIds(safeArgRecipeIds, false))
        }
    }

    // Rimuove una ricetta dalla raccolta e aggiorna il LiveData (solo localmente)
    fun removeRecipeById(recipeId: String) {
        val recipeList: MutableList<Recipe> = recipeListLiveData.value ?: mutableListOf()
        val iterator: MutableIterator<Recipe> = recipeList.iterator()
        while (iterator.hasNext()) {
            val recipe = iterator.next()
            Log.d("Iterator", recipeId + " " + recipe.id)
            if (recipe.id == recipeId)
                iterator.remove()
        }
        Log.d("RecipeList", recipeList.size.toString())
        recipeListLiveData.postValue(recipeList)
    }

    // Ottiene e ritorna una lista con gli id delle ricette contenute nel "recipeListLiveData"
    fun getRecipeIds(): MutableList<String> {
        val recipeList: MutableList<Recipe> = recipeListLiveData.value ?: mutableListOf()
        val recipeIds: MutableList<String> = mutableListOf()
        for (recipe in recipeList) {
            recipeIds.add(recipe.id!!)
        }
        return recipeIds
    }

    // Funzione eseguita al Submit della form
    override fun onFormSubmit(formData: MutableMap<String, Any>, hasDataChanged: Boolean) {
        viewModelScope.launch {
            if (isCollectionNew.value == true) {
                insertCollection(formData)
                setOperationCompleted(OperationType.INSERTION)
            } else if (hasDataChanged) {
                updateCollection(formData)
                setOperationCompleted(OperationType.UPDATE)
            } else {
                setOperationCompleted(OperationType.NONE)
            }
        }
    }

    // Funzione eseguita alla pressione del pulsante di Delete della form
    override fun onFormDelete() {
        viewModelScope.launch {
            Log.d("Delete", collectionId.toString())

            dataSource.deleteCollectionById(collectionId!!)
            setOperationCompleted(OperationType.DELETION)
        }
    }

    // Inserisce una nuova raccolta con i dati della form
    private suspend fun insertCollection(formData: MutableMap<String, Any>) {
        Log.d("Insert", formData.toString())

        val newCollectionId = dataSource.addCollection(
            RecipeCollection(
                null,
                formData["nome"] as String,
                Timestamp.now()
            )
        )

        dataSource.updateCollectionRecipeList(newCollectionId, getRecipeIds())
    }

    // Aggiorna una raccolta esistente con i dati della form
    private suspend fun updateCollection(formData: MutableMap<String, Any>) {
        Log.d("Update", formData.toString())

        dataSource.updateCollectionName(collectionId!!, formData["nome"] as String)
        dataSource.updateCollectionRecipeList(collectionId, getRecipeIds())

    }
}

// Factory class
class CollectionFormViewModelFactory(
    private val collectionId: String?,
    private val collectionName: String?,
    private val recipeIdArray: Array<String>?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectionFormViewModel(
                collectionId,
                MutableLiveData(collectionId.isNullOrEmpty()),
                collectionName ?: String(),
                recipeIdArray?.toMutableList() ?: mutableListOf(),
                recipeIdArray.isNullOrEmpty()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}