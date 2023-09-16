package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.*
import it.project.houseoftasty.model.Profile
import it.project.houseoftasty.repository.ProfileRepository
import it.project.houseoftasty.utility.OperationType
import kotlinx.coroutines.launch

class ProfileFormViewModel() : FormManagerViewModel() {
    private val dataSource: ProfileRepository = ProfileRepository.getDataSource()
    val profileLiveData: MutableLiveData<Profile> = MutableLiveData(Profile())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {

        // Ottiene la ricetta dalla repository e aggiorna il LiveData
        val profile = dataSource.getProfileData()

        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        profileLiveData.postValue(profile)

    }

    // Funzione eseguita al Submit della form
    override fun onFormSubmit(formData: MutableMap<String, Any>, hasDataChanged: Boolean) {
        Log.d("update", "ELOS")
        viewModelScope.launch {
            if (hasDataChanged) {
                updateProfile(formData)
                setOperationCompleted(OperationType.UPDATE)
            } else {
                setOperationCompleted(OperationType.NONE)
            }
        }
    }

    // Aggiorna un profilo esistente con i dati della form
    private suspend fun updateProfile(formData: MutableMap<String, Any>) {
        Log.d("Update", formData.toString())

        dataSource.updateProfile(
            Profile(
                null,
                formData["email"] as String,
                formData["psw"] as String,
                null,
                formData["newPsw"] as String,
                formData["chkPsw"] as String,
                formData["nome"] as String,
                formData["cognome"] as String,
                formData["username"] as String
            )
        )
    }
}

// Factory class
class ProfileFormViewModelFactory(private val profileUid: String?) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}