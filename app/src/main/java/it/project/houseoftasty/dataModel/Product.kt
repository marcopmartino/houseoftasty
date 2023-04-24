package it.project.houseoftasty.dataModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(@PrimaryKey var id: String,
                   var nome: String,
                   var quantita: String,
                   var unitaMisura: String,
                   var scadenza: String,) {
}