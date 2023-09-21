package it.project.houseoftasty.validation

import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.switchmaterial.SwitchMaterial
import it.project.houseoftasty.utility.imageToByteArray
import com.google.android.material.textview.MaterialTextView
import it.project.houseoftasty.utility.tagToString
import it.project.houseoftasty.utility.textToString

/* Classe che rappresenta un campo di input di una form. Ha come proprietà:
* inputView: campo di input con cui l'utente può interagire;
* getInputData: funzione che ritorna il contenuto del campo di input */
class InputField<T: View>(private val inputView: T) {
    private var getInputData: () -> Any = { ("").toString() }

    init {
        initGetInputData()
        Log.d("getInputData", getInputData.invoke().toString())
        Log.d("inputViewClass", inputView::class.toString())
    }

    // Ritorna l'id dell'inputView
    fun getInputViewTag(): String {
        return inputView.tagToString()
    }

    // Ritorna il valore inserito nel campo di input
    fun getInputData(): Any {
        return getInputData.invoke()
    }

    // Ritorna inputView
    fun getInputView(): T {
        return inputView
    }

    // Inizializza la funzione getInputData() in base al tipo di InputField
    private fun initGetInputData() = when (inputView::class) {
        AppCompatEditText::class -> initEditText()
        SwitchMaterial::class -> initSwitch()
        AppCompatImageView::class -> initImageView()
        AppCompatSpinner::class -> initSpinner()
        MaterialTextView::class -> initTextView()
        else -> { }
    }

    // Inizializza la funzione getInputData() per EditText
    private fun initEditText() {
        val editText = inputView as EditText
        getInputData = { editText.textToString() }
    }

    // Inizializza la funzione getInputData() per Spinner
    private fun initSpinner() {
        val spinner = inputView as AppCompatSpinner
            getInputData = { spinner.selectedItem ?: String() }
    }

    // Inizializza la funzione getInputData() per TextView
    private fun initTextView() {
        val textView = inputView as TextView
        getInputData = { textView.text.toString() }
    }

    // Inizializza la funzione getInputData() per Switch
    private fun initSwitch() {
        val switch = inputView as SwitchMaterial
        getInputData = { switch.isChecked }
    }

    // Inizializza la funzione getInputData() per ImageView
    private fun initImageView() {
        val imageView = inputView as ImageView
        getInputData = { imageView.imageToByteArray() }
    }

}