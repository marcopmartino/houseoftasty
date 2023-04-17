package it.project.houseoftasty.viewModel

import androidx.lifecycle.ViewModel

class ProductViewModel(): ViewModel() {
    var nome: String = ""
    var quantita: String = ""
    var unitaMisura: String = ""
    var scadenza: String = ""


    fun setData(nome:String, quantita: String, unitaMisura:String, scadenza:String){
        this.nome = nome
        this.quantita = quantita
        this.unitaMisura = unitaMisura
        this.scadenza = scadenza
    }

    fun loadData(nome: String, quantita: String){
        this.nome = nome
        this.quantita = quantita
    }



}