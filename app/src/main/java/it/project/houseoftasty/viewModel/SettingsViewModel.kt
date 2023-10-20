package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.utility.isUserAuthenticated

class SettingsViewModel: ViewModel() {
    val showRestrictedItems: MutableLiveData<Boolean> =
        MutableLiveData(FirebaseAuth.getInstance().isUserAuthenticated())
}