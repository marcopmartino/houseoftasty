package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.model.Profile
import it.project.houseoftasty.network.ProfileNetwork
import it.project.houseoftasty.utility.OperationType
import kotlinx.coroutines.launch

class ProfileFormViewModel : FormManagerViewModel() {
    private val dataSource: ProfileNetwork = ProfileNetwork.getDataSource()
    val profileLiveData: MutableLiveData<Profile> = MutableLiveData(Profile())
    val isPasswordWrong: MutableLiveData<Boolean> = MutableLiveData(false)

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "profileLiveData"
    override suspend fun initAsync() {

        // Ottiene il profilo dalla repository e aggiorna il LiveData
        val profile = dataSource.getUserData()

        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        profileLiveData.postValue(profile)

    }

    // Ritorna TRUE se esiste un'immagine per il profilo, altrimenti ritorna FALSE
    fun profileImageExists(): Boolean {
        return profileLiveData.value?.boolImmagine ?: false
    }

    // Controlla se i campi in input sono cambiati (usata nel caso di UPDATE del profilo)
    override fun hasDataChanged(formData: MutableMap<String, Any>): Boolean {
        val oldData = profileLiveData.value!!
        val newPassword = formData["newPsw"] as String
        return oldData.username != formData["username"] as String ||
                oldData.nome != formData["nome"] as String ||
                oldData.cognome != formData["cognome"] as String ||
                oldData.email != formData["email"] as String ||
                (newPassword != String() && newPassword != formData["psw"] as String) ||
                "NONE" != formData["immagineOperationType"] as String
    }

    // Funzione eseguita al Submit della form
    override fun onFormSubmit(formData: MutableMap<String, Any>, hasDataChanged: Boolean) {

        val currentUser = FirebaseAuth.getInstance().currentUser
        val inputPassword: String = formData["psw"] as String

        val credential: AuthCredential = EmailAuthProvider.getCredential(
            currentUser?.email.toString(), inputPassword)

        Log.d("Email and Password", currentUser?.email.toString() + " " + inputPassword)
        currentUser?.reauthenticate(credential)?.addOnCompleteListener {
            if (it.isSuccessful)
                viewModelScope.launch {
                    if (hasDataChanged) {
                       updateProfile(formData)
                      setOperationCompleted(OperationType.UPDATE)
                    } else {
                        setOperationCompleted(OperationType.NONE)
                    }
                }
            else {
                isPasswordWrong.postValue(true)
                stopLoading()
            }
        }
    }

    // Aggiorna un profilo esistente con i dati della form
    private suspend fun updateProfile(formData: MutableMap<String, Any>) {
        Log.d("Update", formData.toString())

        val imageOperation = formData["immagineOperationType"] as String
        val oldData = profileLiveData.value!!

        dataSource.updateUser(
            Profile(
                oldData.id.toString(),
                formData["email"] as String,
                formData["psw"] as String,
                formData["newPsw"] as String,
                formData["nome"] as String,
                formData["cognome"] as String,
                formData["username"] as String,
                when (imageOperation) {
                    "SELECTION" -> true
                    "REMOVAL" -> false
                    else -> { oldData.boolImmagine }
                },
            )
        )

        when (imageOperation) {
            "SELECTION" -> dataSource.uploadFileFromByteArray(oldData.id.toString(), formData["immagine"] as ByteArray)
            "REMOVAL" -> dataSource.deleteFile(oldData.id.toString())
        }

    }
}

// Factory class
class ProfileFormViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}