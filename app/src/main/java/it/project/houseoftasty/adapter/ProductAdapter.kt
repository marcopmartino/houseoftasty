package it.project.houseoftasty.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.model.Product
import it.project.houseoftasty.databinding.ProductItemBinding
import java.text.SimpleDateFormat
import java.util.*


class ProductAdapter(
    private var context: Context,
    private val resources: Resources,
    private val onClick: (Product) -> Unit) :
    ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback) {

    /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
    class ProductViewHolder(
        private val itemBinding: ProductItemBinding,
        private val context: Context,
        private val resources: Resources,
        private val onClick: (Product) -> Unit) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private var currentProduct: Product? = null

        init {
            itemView.setOnClickListener {
                currentProduct?.let {
                    onClick(it)
                }
            }
        }

        /* Fa il binding della ricetta. */
        @SuppressLint("SimpleDateFormat")
        fun bind(product: Product){

            val sdf = SimpleDateFormat("dd/MM/yyyy")
            currentProduct = product

            // Binding del nome
            itemBinding.nomeData.text = product.nome

            // Binding del quantita
            var temp = product.quantita+" "+product.misura
            itemBinding.quantitaData.text = temp

            if(product.scadenza != "-") {

                val chkDate = sdf.parse(product.scadenza!!)
                val diff = chkDate!!.time - Date().time
                val hours = (((diff / 1000) / 60) / 60)

                if (hours in -23..48) {
                    itemBinding.scadenzaData.setTextColor(Color.rgb(255,255,0))
                }else if(hours<=-24){
                    itemBinding.scadenzaData.setTextColor(Color.rgb(255,0,0))
                }
            }
            itemBinding.scadenzaData.text = product.scadenza
        }
    }

    /* Fa l'inflating e ritorna il ProductViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ProductViewHolder(binding, context, resources, onClick)
    }

    /* Prende il prodotto attuale e la usa per il binding con la view. */
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)

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

object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {

    // Controlla se si tratta dello stesso prodotto
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    /* Viene eseguito se "areItemsTheSame" ritorna TRUE, tuttavia sappiamo già che due prodotti con
    lo stesso ID hanno anche lo stesso contenuto (si tratta dello stesso prodotto) */
    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return true
    }


}
