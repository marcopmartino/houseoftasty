package it.project.houseoftasty.adapter

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.ItemPrivateRecipeBinding
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.module.GlideApp
import it.project.houseoftasty.utility.DateTimeFormatter
import it.project.houseoftasty.utility.ImageLoader
import java.text.SimpleDateFormat
import java.util.*

class PrivateRecipeAdapter(
    private val context: Context,
    private val resources: Resources,
    private val onClick: (String) -> Unit) :
    ListAdapter<Recipe, PrivateRecipeAdapter.PrivateRecipeViewHolder>(RecipeDiffCallback) {

    /* ViewHolder for Recipe, takes in the inflated view and the onClick behavior. */
    class PrivateRecipeViewHolder(
        private val itemBinding: ItemPrivateRecipeBinding,
        private val context: Context,
        private val resources: Resources,
        private val onClick: (String) -> Unit) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private var currentRecipe: Recipe? = null

        init {
            itemView.setOnClickListener {
                currentRecipe?.let {
                    if (!it.id.isNullOrEmpty()) onClick(it.id!!)
                }
            }
        }

        /* Fa il binding della ricetta. */
        fun bind(recipe: Recipe) {
            currentRecipe = recipe

            // Binding del titolo
            itemBinding.privateRecipeTitle.text = recipe.titolo.toString().uppercase()

            // Binding del timestamp
            val timestamp = recipe.timestampCreazione as com.google.firebase.Timestamp
            itemBinding.privateRecipeDate.text = DateTimeFormatter.firebaseTimestampToTemplate(resources, timestamp)

            // Binding dell'immagine
            ImageLoader.loadFromReference(context, recipe.imageReference, itemBinding.privateRecipeImage)
        }
    }

    /* Fa l'inflating e ritorna il PrivateRecipeViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivateRecipeViewHolder {
        val binding = ItemPrivateRecipeBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return PrivateRecipeViewHolder(binding, context, resources, onClick)
    }

    /* Prende la ricetta attuale e la usa per il binding con la view. */
    override fun onBindViewHolder(holder: PrivateRecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)

    }
}

object RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {

    // Controlla se si tratta della stessa ricetta
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.id == newItem.id
    }

    /* Viene eseguito se "areItemsTheSame" ritorna TRUE, tuttavia sappiamo già che due ricette con
    lo stesso ID hanno anche lo stesso contenuto (si tratta della stessa ricetta) */
    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return true
    }
}