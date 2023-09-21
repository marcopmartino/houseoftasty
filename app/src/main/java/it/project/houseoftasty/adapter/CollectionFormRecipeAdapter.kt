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
import it.project.houseoftasty.databinding.ItemCollectionFormRecipeBinding
import it.project.houseoftasty.databinding.ItemPrivateRecipeBinding
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.module.GlideApp
import it.project.houseoftasty.utility.DateTimeFormatter
import it.project.houseoftasty.utility.ImageLoader
import java.text.SimpleDateFormat
import java.util.*


class CollectionFormRecipeAdapter(
    private val onClick: (String, Int) -> Unit) :
    ListAdapter<Recipe, CollectionFormRecipeAdapter.CollectionFormRecipeViewHolder>(RecipeDiffCallback) {

    /* ViewHolder for CollectionFormRecipe, takes in the inflated view and the onClick behavior. */
    class CollectionFormRecipeViewHolder(
        private val itemBinding: ItemCollectionFormRecipeBinding,
        private val onClick: (String, Int) -> Unit) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private var currentRecipe: Recipe? = null

        init {
            itemView.setOnClickListener {
                currentRecipe?.let {
                    if (!it.id.isNullOrEmpty()) onClick(it.id!!, adapterPosition)
                }
            }
        }

        /* Fa il binding della ricetta. */
        fun bind(recipe: Recipe) {
            currentRecipe = recipe

            // Binding del titolo
            itemBinding.recipeButton.text = recipe.titolo.toString()

        }
    }

    /* Fa l'inflating e ritorna il CollectionFormRecipeViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionFormRecipeViewHolder {
        val binding = ItemCollectionFormRecipeBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return CollectionFormRecipeViewHolder(binding, onClick)
    }

    /* Prende la ricetta attuale e la usa per il binding con la view. */
    override fun onBindViewHolder(holder: CollectionFormRecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)

    }
}