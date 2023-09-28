package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.project.houseoftasty.model.Profile
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.ProfileNetwork
import it.project.houseoftasty.network.RecipeNetwork

class MyPublicProfileViewModel() : LoadingManagerViewModel() {
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
        recipeLiveData.postValue(recipeDataSource.getPublicRecipesByUser())
    }
}