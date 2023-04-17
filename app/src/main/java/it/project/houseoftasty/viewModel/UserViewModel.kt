package it.project.houseoftasty.viewModel

import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    var username: String = ""
    var nome: String = ""
    var cognome: String = ""
    var email: String = ""

    fun loadData(username: String, nome: String, cognome: String, email: String){
        this.username = username
        this.nome = nome
        this.cognome = cognome
        this.email = email
    }

}