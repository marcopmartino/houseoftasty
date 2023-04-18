package it.project.houseoftasty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.viewModel.ProductViewModel

class ProductAdapter(
    private val mList: List<ProductViewModel>,
    private val listener: Communicator) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val productList = mList[position]

        holder.nomeView.text = productList.nome
        holder.quantitaView.text = productList.quantita
        holder.quantitaMisuraView.text = productList.unitaMisura
        holder.scadenzaView.text = productList.scadenza

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val nomeView: TextView = itemView.findViewById(R.id.nomeData)
        val quantitaView:TextView = itemView.findViewById(R.id.quantitaData)
        val quantitaMisuraView:TextView = itemView.findViewById(R.id.quantitaMisura)
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
}