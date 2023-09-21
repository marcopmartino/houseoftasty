package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import it.project.houseoftasty.model.Profile
import it.project.houseoftasty.network.ProfileNetwork

class ProfileViewModel : LoadingManagerViewModel() {
    private val dataSource: ProfileNetwork = ProfileNetwork.getDataSource()
    var profileLiveData: MutableLiveData<Profile> = MutableLiveData(Profile())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "profileLiveData"
    override suspend fun initAsync() {

        val profileData = dataSource.getUserData()
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        profileLiveData.postValue(profileData)
    }



}