package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel

class UserViewModel() : ViewModel() {
    var username: String = ""
    var nome: String = ""
    var cognome: String = ""
    var email: String = ""

    init{
        Log.d("MODEL", "UserModel Creato!!")
    }

    fun loadData(username: String, nome: String, cognome: String, email: String){
        this.username = username
        this.nome = nome
        this.cognome = cognome
        this.email = email
    }

    /*fun setUsername(temp: String){
        username = temp
    }

    fun getUsername(): String {
        return username
    }*/

}