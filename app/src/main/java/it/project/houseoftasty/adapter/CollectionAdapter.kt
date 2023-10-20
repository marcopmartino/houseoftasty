package it.project.houseoftasty.adapter

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.ItemCollectionBinding
import it.project.houseoftasty.model.RecipeCollection
import it.project.houseoftasty.utility.DateTimeFormatter
import it.project.houseoftasty.utility.ImageLoader

class CollectionAdapter(
    private val context: Context,
    private val resources: Resources,
    private val onClick: (String) -> Unit) :
    ListAdapter<RecipeCollection, CollectionAdapter.CollectionViewHolder>(CollectionDiffCallback) {

    /* ViewHolder for Recipe, takes in the inflated view and the onClick behavior. */
    class CollectionViewHolder(
        private val itemBinding: ItemCollectionBinding,
        private val context: Context,
        private val resources: Resources,
        private val onClick: (String) -> Unit) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private var currentRecipeCollection: RecipeCollection? = null

        init {
            itemView.setOnClickListener {
                currentRecipeCollection?.let {
                    if (!it.id.isNullOrEmpty()) onClick(it.id!!)
                }
            }
        }

        /* Fa il binding della ricetta. */
        fun bind(recipeCollection: RecipeCollection) {
            currentRecipeCollection = recipeCollection

            // Binding del nome
            "${recipeCollection.nome.toString().uppercase()} (${recipeCollection.listaRicette.size})"
                .also { itemBinding.collectionName.text = it }

            // Binding del timestamp
            itemBinding.collectionTime.text =
                if (recipeCollection.id == "saveCollection")
                    "Raccolta generata automaticamente"
                else
                    DateTimeFormatter.firebaseTimestampToTemplate(
                        resources, R.string.timestampTemplate_creation,
                        recipeCollection.timestampCreazione as com.google.firebase.Timestamp)

            // Binding dell'immagine
            var immagineCaricata = false
            for (ricetta in recipeCollection.listaRicette) {
                if (!immagineCaricata && ricetta.boolImmagine) {
                    ImageLoader.loadFromReference(context, ricetta.imageReference, itemBinding.collectionImage)
                    immagineCaricata = true
                }
            }

        }
    }

    /* Fa l'inflating e ritorna il CollectionViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = ItemCollectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return CollectionViewHolder(binding, context, resources, onClick)
    }

    /* Prende la ricetta attuale e la usa per il binding con la view. */
    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = getItem(position)
        holder.bind(collection)

    }
}

object CollectionDiffCallback : DiffUtil.ItemCallback<RecipeCollection>() {

    // Controlla se si tratta della stessa ricetta
    override fun areItemsTheSame(oldItem: RecipeCollection, newItem: RecipeCollection): Boolean {
        return oldItem.id == newItem.id
    }

    /* Viene eseguito se "areItemsTheSame" ritorna TRUE, tuttavia sappiamo già che due ricette con
    lo stesso ID hanno anche lo stesso contenuto (si tratta della stessa ricetta) */
    override fun areContentsTheSame(oldItem: RecipeCollection, newItem: RecipeCollection): Boolean {
        return true
    }
}