package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.project.houseoftasty.model.Comment
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeNetwork

class RecipeDetailsViewModel(val recipeId: String) : LoadingManagerViewModel() {
    private val dataSource: RecipeNetwork = RecipeNetwork.getDataSource()
    val recipeLiveData : MutableLiveData<Recipe> = MutableLiveData(Recipe())
    val commentLiveData : MutableLiveData<MutableList<Comment>> = MutableLiveData(mutableListOf(Comment()))
    val likeButtonPressed: MutableLiveData<Boolean> = MutableLiveData(false)

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        // Ottiene la ricetta dalla repository e aggiorna il LiveData
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        recipeLiveData.postValue(dataSource.getRecipeById(recipeId))
        commentLiveData.postValue(dataSource.getCommentsByRecipeId(recipeId))

    }

}

// Factory class
class RecipeDetailsViewModelFactory(private val recipeId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeDetailsViewModel(recipeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}