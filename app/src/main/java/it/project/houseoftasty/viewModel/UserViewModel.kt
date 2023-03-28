package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel() {
    private var username: String = ""
    var nome: String = ""
    var cognome: String = ""
    var email: String = ""

    init{
        Log.d("MODEL", "UserModel Creato!!")
    }

    fun setUsername(temp: String){
        username = temp
    }

    fun getUsername(): String {
        return username
    }

}