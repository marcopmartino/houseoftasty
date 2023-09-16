package it.project.houseoftasty.viewModel

import android.util.Log
import it.project.houseoftasty.model.Profile
import it.project.houseoftasty.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ProfileViewModel : LoadingManagerViewModel(){
    var nome: String = ""
    var cognome: String = ""
    var email: String = ""
    var username: String = ""

    private val dataSource: ProfileRepository = ProfileRepository.getDataSource()
    var profileLiveData = runBlocking{ dataSource.getProfileData()}

    fun setData(nome:String, cognome: String, email:String, username:String){
        this.nome = nome
        this.cognome = cognome
        this.email = email
        this.username = username
    }

    fun loadData(nome:String, cognome: String, email:String, username:String){
        this.nome = nome
        this.cognome = cognome
        this.email = email
        this.username = username
    }

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori

    }



}