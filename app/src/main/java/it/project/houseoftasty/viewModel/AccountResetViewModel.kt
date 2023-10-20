package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.project.houseoftasty.network.SettingsNetwork
import kotlinx.coroutines.launch

class AccountResetViewModel : LoadingManagerViewModel(initialStatus = true) {

    val dataSource: SettingsNetwork = SettingsNetwork.getDataSource()

   // Elimina i dati dell'account (ricette, raccolte, prodotti)
    fun resetAccount(onResetCompleted: () -> Unit) {
       viewModelScope.launch {
           startLoading()
           dataSource.resetAccount()
           stopLoading()
           onResetCompleted.invoke()
       }
    }
}