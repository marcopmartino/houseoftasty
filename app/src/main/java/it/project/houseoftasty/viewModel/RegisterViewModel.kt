package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.*
import it.project.houseoftasty.model.Profile
import it.project.houseoftasty.network.ProfileNetwork
import it.project.houseoftasty.utility.OperationType
import kotlinx.coroutines.launch

class RegisterViewModel : FormManagerViewModel(true) {
    private val dataSource: ProfileNetwork = ProfileNetwork.getDataSource()
    val registerLiveData: MutableLiveData<Profile> = MutableLiveData(Profile())

    // Funzione eseguita al Submit della form
    override fun onFormSubmit(formData: MutableMap<String, Any>, hasDataChanged: Boolean) {
        viewModelScope.launch {
            insertProfile(formData)
            setOperationCompleted(OperationType.UPDATE)
        }
    }

    // Inserisce un nuovo profilo con i dati della form
    private suspend fun insertProfile(formData: MutableMap<String, Any>) {
        Log.d("Insert", formData["username"].toString())

        dataSource.addUser(
            Profile(
                null,
                formData["email"] as String,
                formData["psw"] as String,
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