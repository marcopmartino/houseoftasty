package it.project.houseoftasty.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.CommentItemBinding
import it.project.houseoftasty.model.Comment
import it.project.houseoftasty.utility.DateTimeFormatter
import it.project.houseoftasty.utility.ImageLoader
import java.util.*


class CommentAdapter(
    private var context: Context,
    private val resources: Resources,
    private val onClick: (String) -> Unit
) :
    ListAdapter<Comment, CommentAdapter.CommentViewHolder>(CommentDiffHolder) {

    /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
    class CommentViewHolder(
        private val itemBinding: CommentItemBinding,
        private val context: Context,
        private val resources: Resources,
        private val onClick: (String) -> Unit) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private var currentComment: Comment? = null

        init {
            itemView.setOnClickListener {
                currentComment?.let {
                    if (!it.userId.isNullOrEmpty()) onClick(it.userId!!)
                }
            }
        }

        /* Fa il binding del commento. */
        fun bind(comment: Comment){

            currentComment = comment

            // Binding del nome utente
            itemBinding.userUsername.text = comment.userUsername

            // Binding del timestamp di creazione del commento
            itemBinding.commentTimestamp.text =
                DateTimeFormatter.timeDiffToString(comment.timestamp)

            // Binding del testo del commento
            itemBinding.commentText.text = comment.commentData

            // Binding dell'immagine del commento
            ImageLoader.loadFromReference(
                context,
                comment.imageReference,
                itemBinding.profileImageContainer,
                R.drawable.img_peopleiconcol,
                R.drawable.img_peopleiconcol)
            itemBinding.profileImageContainer.clipToOutline = true
        }
    }

    /* Fa l'inflating e ritorna il ProductViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return CommentViewHolder(binding, context, resources, onClick)
    }

    /* Prende il prodotto attuale e la usa per il binding con la view. */
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }
}

object CommentDiffHolder : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return true
    }


}