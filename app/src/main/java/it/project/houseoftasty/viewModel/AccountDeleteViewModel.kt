package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.network.SettingsNetwork
import it.project.houseoftasty.utility.OperationType
import kotlinx.coroutines.launch

class AccountDeleteViewModel : LoadingManagerViewModel(initialStatus = true) {

    val dataSource: SettingsNetwork = SettingsNetwork.getDataSource()
    val isPasswordWrong: MutableLiveData<Boolean> = MutableLiveData(false)

    // Elimina i dati dell'account (ricette, raccolte, prodotti)
    suspend fun deleteAccount(inputPassword: String, onDeleteCompleted: () -> Unit) {
        startLoading()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val credential: AuthCredential = EmailAuthProvider.getCredential(
            currentUser?.email.toString(), inputPassword)

        Log.d("Email and Password", currentUser?.email.toString() + " " + inputPassword)
        currentUser?.reauthenticate(credential)?.addOnCompleteListener {
            if (it.isSuccessful)
                viewModelScope.launch {
                    dataSource.deleteAccount()
                    onDeleteCompleted.invoke()
                }
            else {
                isPasswordWrong.postValue(true)
                stopLoading()
            }
        }
    }
}