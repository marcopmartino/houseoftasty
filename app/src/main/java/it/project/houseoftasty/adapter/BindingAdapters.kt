package it.project.houseoftasty.adapter

import android.os.Build
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.storage.StorageReference
import it.project.houseoftasty.R
import it.project.houseoftasty.utility.DateTimeFormatter
import it.project.houseoftasty.utility.ImageLoader
import java.util.*

class BindingAdapters {

    companion object {
        @BindingAdapter("setImage")
        @JvmStatic
        fun ImageView.setImage(imageReference: StorageReference?) {

            // Binding dell'immagine
            ImageLoader.loadFromReference(context, imageReference, this)

        }

        @BindingAdapter("setTextByCreationTimestamp")
        @JvmStatic
        fun TextView.setTextByCreationTimestamp(timestamp: Timestamp?) {

            // Binding del timestamp
            if (timestamp != null) {
                this.text = DateTimeFormatter.firebaseTimestampToTemplate(resources, timestamp)
            }

        }

        @BindingAdapter("setPreparationTime")
        @JvmStatic
        fun TextView.setPreparationTime(minutes: Int?) {

            // Binding del timestamp
            if (minutes != null) {
                val stringBuilder = StringBuilder()
                var hours = 0

                if (minutes >= 60) {
                    hours = minutes/60
                    stringBuilder.append(hours).append("h ")
                }
                stringBuilder.append(minutes-hours*60).append("min")

                this.text = stringBuilder
            }

        }

        @BindingAdapter("setFabVisibility")
        @JvmStatic
        fun FloatingActionButton.setFabVisibility(show: Boolean) {
            val fabVisibility = this.visibility

            // Mostra o nasconde il F.A.B. usando i metodi predefiniti
            if (show && fabVisibility != View.VISIBLE) {
                this.show()
            } else if (!show && fabVisibility == View.VISIBLE) {
                this.hide()
            }

        }

        @BindingAdapter("setSpinnerItem")
        @JvmStatic
        fun Spinner.setSpinnerItem(item: String?) {
            this.context?.let {
                ArrayAdapter.createFromResource(
                    it,
                    R.array.array_misure,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Apply the adapter to the spinner
                    this.adapter = adapter
                    if(item != null) {
                        val position = adapter.getPosition(item)
                        this.setSelection(position)
                    }
                }
            }


        }


    }
}