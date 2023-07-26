package it.project.houseoftasty.utility

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.firebase.storage.StorageReference
import it.project.houseoftasty.R
import it.project.houseoftasty.module.GlideApp

class ImageLoader {
    companion object {
        fun loadFromReference(context: Context, imageReference: StorageReference?, imageView: ImageView) {

            /*

            // Definiamo l'animazione di caricamento
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            // Glide carica le immagini in maniera asincrona
            GlideApp.with(context)
                .load(imageReference)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.medium_logo)
                .centerCrop()
                .into(imageView)

             */

            // Glide carica le immagini in maniera asincrona
            GlideApp.with(context)
                .load(imageReference)
                .placeholder(R.drawable.medium_logo)
                .centerCrop()
                .into(imageView)
        }
    }
}