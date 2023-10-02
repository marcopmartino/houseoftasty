package it.project.houseoftasty.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.ItemNewestRecipeBinding
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.utility.DateTimeFormatter
import it.project.houseoftasty.utility.dateToString
import it.project.houseoftasty.utility.timeToString
import it.project.houseoftasty.utility.toLocalDateTime

class PublicRecipeHomeAdapter(
    private val context: Context,
    private val resources: Resources,
    private val onClick: (String) -> Unit) :
    ListAdapter<Recipe, PublicRecipeHomeAdapter.PublicRecipeHomeViewHolder>(RecipeDiffCallback) {

    /* ViewHolder for Recipe, takes in the inflated view and the onClick behavior. */
    class PublicRecipeHomeViewHolder(
        private val itemBinding: ItemNewestRecipeBinding,
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
            itemBinding.recipeItemTitle.text = recipe.titolo.toString().uppercase()
            
            // Binding del timestamp
            val timestamp = (recipe.timestampPubblicazione as com.google.firebase.Timestamp).toLocalDateTime()
            val nomeCreatore = recipe.nomeCreatore

            if (nomeCreatore.isNullOrEmpty()) {
                if (recipe.boolPostPrivato)
                    itemBinding.recipeItemInfo.text = DateTimeFormatter.localDateTimeToTemplate(
                        resources, R.string.timestampTemplate_publicationPrivate, timestamp)
                else
                    itemBinding.recipeItemInfo.text = DateTimeFormatter.localDateTimeToTemplate(
                        resources, R.string.timestampTemplate_publication, timestamp)
            } else {
                itemBinding.recipeItemInfo.text = resources.getString(
                    R.string.authorAndTimestampTemplate, nomeCreatore,
                    timestamp.dateToString(true), timestamp.timeToString())
            }

            // Binding delle statistiche
            itemBinding.likesCounter.text = recipe.likeCounter.toString()
            itemBinding.commentsCounter.text = recipe.commentCounter.toString()
            itemBinding.viewsCounter.text = recipe.views.toString()
            itemBinding.downloadsCounter.text = recipe.downloadCounter.toString()

        }
    }

    /* Fa l'inflating e ritorna il PrivateRecipeViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicRecipeHomeViewHolder {
        val binding = ItemNewestRecipeBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return PublicRecipeHomeViewHolder(binding, context, resources, onClick)
    }

    /* Prende la ricetta attuale e la usa per il binding con la view. */
    override fun onBindViewHolder(holder: PublicRecipeHomeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)

    }
}