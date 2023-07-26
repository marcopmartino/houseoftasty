package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.repository.RecipeRepository

class RecipeDetailsViewModel(val recipeId: String) : LoadingManagerViewModel() {
    private val dataSource: RecipeRepository = RecipeRepository.getDataSource()
    val recipeLiveData : MutableLiveData<Recipe> = MutableLiveData(Recipe())
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

    }

    // Fa il toggle di "likeButtonPressed" e notifica eventuali osservatori
    fun toggleLikeButtonPressed() {
        likeButtonPressed.postValue(!likeButtonPressed.value!!)
    }

    /*
     /* Queries datasource to returns a flower that corresponds to an id. */
     fun getRecipeForId(id: Long) : Recipe? {
         return datasource.getFlowerForId(id)
     }

     /* Queries datasource to remove a flower. */
     fun removeRecipe(recipe: Recipe) {
         datasource.removeRecipe(recipe)
     }

  */
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