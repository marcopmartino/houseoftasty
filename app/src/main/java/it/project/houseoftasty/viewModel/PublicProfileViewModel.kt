package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.project.houseoftasty.model.Profile
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.ProfileNetwork
import it.project.houseoftasty.network.RecipeNetwork

class PublicProfileViewModel(private val userId: String?, val isCurrentUser: Boolean) : LoadingManagerViewModel() {
    private val profileDataSource: ProfileNetwork = ProfileNetwork.getDataSource()
    private val recipeDataSource: RecipeNetwork = RecipeNetwork.getDataSource()
    var profileLiveData: MutableLiveData<Profile> = MutableLiveData(Profile())
    var recipeLiveData: MutableLiveData<MutableList<Recipe>> = MutableLiveData(mutableListOf())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza le variabili "profileLiveData" e "recipeLiveData"
    override suspend fun initAsync() {
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        profileLiveData.postValue(profileDataSource.getUserData())
        recipeLiveData.postValue(
            if (isCurrentUser)
                recipeDataSource.getPublicRecipesByUser()
            else
                recipeDataSource.getPublicRecipesByUser(userId!!))
    }
}

// Factory class
class PublicProfileViewModelFactory(private val userId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublicProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PublicProfileViewModel(userId, userId == "[nullValue]") as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}