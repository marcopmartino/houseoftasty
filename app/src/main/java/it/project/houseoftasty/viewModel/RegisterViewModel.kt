package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.*
import it.project.houseoftasty.model.Profile
import it.project.houseoftasty.repository.ProfileRepository
import it.project.houseoftasty.utility.OperationType
import kotlinx.coroutines.launch

class RegisterViewModel() : FormManagerViewModel() {
    private val dataSource: ProfileRepository = ProfileRepository.getDataSource()
    val registerLiveData: MutableLiveData<Profile> = MutableLiveData(Profile())

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {

    }

    // Funzione eseguita al Submit della form
    override fun onFormSubmit(formData: MutableMap<String, Any>, hasDataChanged: Boolean) {
        Log.d("update", "ELOS")
        viewModelScope.launch {
            if (hasDataChanged) {
                insertProfile(formData)
                setOperationCompleted(OperationType.UPDATE)
            } else {
                setOperationCompleted(OperationType.NONE)
            }
        }
    }

    // Inserisce un nuovo profilo con i dati della form
    private suspend fun insertProfile(formData: MutableMap<String, Any>) {
        Log.d("Insert", formData["username"].toString())

        dataSource.addProfile(
            Profile(
                null,
                formData["email"] as String,
                formData["psw"] as String,
                null,
                null,
                null,
                formData["nome"] as String,
                formData["cognome"] as String,
                formData["username"] as String
            )
        )
    }
}

// Factory class
class RegisterViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}