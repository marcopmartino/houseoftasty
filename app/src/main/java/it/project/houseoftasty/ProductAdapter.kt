package it.project.houseoftasty

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.viewModel.ProductViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
  * La classe ProductAdapter rappresenta l'adapter per una  la RecyclerView che mostra
  * nella schermata "I miei prodotti".
  * Ogni elemento della RecyclerView e' rappresentato da un ViewHolder.
  * La classe fornisce i metodi necessari per creare la struttura della View (onCreateViewHolder) e
  * associare i dati dell'oggetto alla relativa View (onBindViewHolder)
  * @property mList Lista di oggetti di tipo ProductViewModel
  * @property listener Oggetto di tipo Communicator che fornisce il metodo passData per comunicare al Fragment
  * quando un elemento della RecyclerView e' stato selezionato
 **/
class ProductAdapter(
    private val mList: List<ProductViewModel>,
    private val listener: Communicator) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)

        return ViewHolder(view)
    }


    /**
     * onBindViewHolder e' un metodo di classe ViewHolderAdapter che viene chiamato per associare i dati dell'oggetto productList
     * alla relativa View. Inoltre, controlla la data di scadenza e cambia il colore del testo della View in base alla sua
     *  prossimita' alla data corrente
    **/
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val productList = mList[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val temp = sdf.format(System.currentTimeMillis())
        val currentDate = Date(temp)

        holder.nomeView.text = productList.nome
        holder.quantitaView.text = productList.quantita+" "+productList.unitaMisura
        if(productList.scadenza != "-") {
            val chkDate = sdf.parse(productList.scadenza)
            val diff = chkDate!!.time - currentDate.time
            val days = (((diff / 1000) / 60) / 60) / 24
            if (days <= 2) {
                holder.scadenzaView.setTextColor(Color.rgb(255,255,0))
            }else if(days.compareTo(0) == 0){
                holder.scadenzaView.setTextColor(Color.rgb(255,0,0))
            }
        }
        holder.scadenzaView.text = productList.scadenza

    }


    /**
     * Restituisce il numero di elementi presenti nella lista
    **/
    override fun getItemCount(): Int {
        return mList.size
    }


    /**
     * Questa classe rappresenta una vista di un elemento all'interno di una RecyclerView
    **/
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val nomeView: TextView = itemView.findViewById(R.id.nomeData)
        val quantitaView:TextView = itemView.findViewById(R.id.quantitaData)
        val scadenzaView:TextView = itemView.findViewById(R.id.scadenzaData)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.passData(mList[adapterPosition].id)
            }
        }
    }

    companion object{
        /**
         * Crea un nuovo ID casuale
        **/
        fun createRandomId() : String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..20)
                .map { allowedChars.random() }
                .joinToString("")
        }
    }
}
