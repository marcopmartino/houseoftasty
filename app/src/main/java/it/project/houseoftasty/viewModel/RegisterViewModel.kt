package it.project.houseoftasty.viewModel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
        }
    }

    // Inserisce un nuovo profilo con i dati della form
    suspend fun insertProfile(formData: MutableMap<String, Any>) {
        Log.d("Insert", formData["username"].toString())

        val email = formData["email"] as String
        val password = formData["psw"] as String

        try {
            dataSource.createUser(email, password)
            dataSource.addUser(
                Profile(
                    null,
                    email = email,
                    password = password,
                    nome = formData["nome"] as String,
                    cognome = formData["cognome"] as String,
                    username = formData["username"] as String
                ))
            setOperationCompleted(OperationType.INSERTION)
        } catch (e: FirebaseAuthUserCollisionException) {
            setOperationCompleted(OperationType.FirebaseAuthUserCollisionException)
        } catch (e: Exception) {
            setOperationCompleted(OperationType.Exception)
        }
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