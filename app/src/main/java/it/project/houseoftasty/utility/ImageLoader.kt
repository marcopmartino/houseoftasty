package it.project.houseoftasty.utility

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.firebase.storage.StorageReference
import it.project.houseoftasty.R
import it.project.houseoftasty.module.GlideApp
import java.io.ByteArrayOutputStream

class ImageLoader {

    companion object {

        private const val defaultPlaceholder: Int = R.drawable.medium_logo
        private const val defaultErrorImage: Int = R.drawable.kitchen_background_01

        // Crea e restituisce l'animazione di caricamento
        private fun loadingAnimation(context: Context): CircularProgressDrawable {

            // Definiamo l'animazione di caricamento
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            return circularProgressDrawable
        }

        fun loadDrawable(
            context: Context,
            resourceId: Int,
            imageView: ImageView,
            placeholder: Int = defaultPlaceholder,
            errorImage: Int = defaultErrorImage,
            animatedLoading: Boolean = false
        ) {
            if (!animatedLoading) {
                // Glide carica l'immagine in maniera asincrona
                GlideApp.with(context)
                    .load(resourceId)
                    .placeholder(placeholder)
                    .error(errorImage)
                    .centerCrop()
                    .into(imageView)
            } else {
                // Glide carica l'immagine usando l'animazione di caricamento come placeholder
                GlideApp.with(context)
                    .load(resourceId)
                    .placeholder(loadingAnimation(context))
                    .error(errorImage)
                    .centerCrop()
                    .into(imageView)
            }
        }

        fun loadBitmap(
            imageView: ImageView,
            bitmap: Bitmap?,
        ) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            }
        }

        fun loadFromReference(
            context: Context,
            imageReference: StorageReference?,
            imageView: ImageView,
            placeholder: Int = defaultPlaceholder,
            errorImage: Int = defaultErrorImage,
            animatedLoading: Boolean = false
        ) {
            if (!animatedLoading) {
                // Glide carica l'immagine in maniera asincrona
                GlideApp.with(context)
                    .load(imageReference)
                    .placeholder(placeholder)
                    .error(errorImage)
                    .centerCrop()
                    .into(imageView)
            } else {
                // Glide carica l'immagine usando l'animazione di caricamento come placeholder
                GlideApp.with(context)
                    .load(imageReference)
                    .placeholder(loadingAnimation(context))
                    .error(errorImage)
                    .centerCrop()
                    .into(imageView)
            }
        }

        fun loadFromUri(imageUri: Uri?, imageView: ImageView) {
            imageView.setImageURI(imageUri)
        }
    }
}