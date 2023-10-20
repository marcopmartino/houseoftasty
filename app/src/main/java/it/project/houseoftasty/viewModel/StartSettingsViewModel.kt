package it.project.houseoftasty.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.utility.isUserAuthenticated
import it.project.houseoftasty.utility.update

class StartSettingsViewModel: ViewModel() {
    val visiblePreview: MutableLiveData<String> = MutableLiveData(String())

    fun setVisiblePreview(preview: String) {
        visiblePreview.update(preview)
    }
}