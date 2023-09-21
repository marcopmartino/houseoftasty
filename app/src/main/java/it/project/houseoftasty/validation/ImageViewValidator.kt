package it.project.houseoftasty.validation

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.storage.StorageReference
import it.project.houseoftasty.R
import it.project.houseoftasty.utility.ImageLoader
import it.project.houseoftasty.utility.OperationType

/* Classe per validare un campo di input di tipo EditText. Ha come proprietà:
*   removeButton: pulsante per la rimozione dell'immagine associata all'ImageView;
*   maxImageSizeKB: dimensione massima dell'immagine associata all'ImageView in Kilobytes;
*   defaultImageId: id della risorsa usata come immagine di default nell'ImageView;
*   onSelectButtonClicked: funzione da eseguire al click sul pulsante di selezione.
*
* La classe viene costruita facendo uso del pattern Builder */
class ImageViewValidator: Validator<ImageView>() {
    private var removeButton: Button? = null
    private var lastOperation: OperationType = OperationType.NONE
    private var maxImageSizeKB: Int = 2048 // 2MB
    private var defaultImageId: Int = -1
    private var onSelectButtonClicked: () -> Unit = {}

    /* Esegue la validazione dell'immagine selezionata. Ritorna TRUE se non sono stati
    * riscontrati errori, altrimenti FALSE. */
    override fun validate(): Boolean {
        var isValid = true
        if (inputField != null) {
            Log.d("ClasseInputDataImmagine", inputField!!.getInputData()::class.toString())
            val inputData: ByteArray = inputField!!.getInputData() as ByteArray

            // Controlla che sia rispettato il vincolo sulle dimensioni massime dell'immagine
            isValid = inputData.size.toFloat()/1024 <= maxImageSizeKB.toFloat()
            Log.d("ImageSize", inputData.size.toString())

            // Mostra il messaggio di errore della prima regola di validazione infranta
            if (isValid)
                resetErrorMessage()
            else
                setErrorMessage("Dimensioni del file troppo grandi (max: $maxImageSizeKB KB)")
        }
        return isValid
    }

    // Ritorna l'ultima operazione eseguita
    fun getLastOperation(): String {
        return lastOperation.name
    }

    // Abilita il pulsante di rimozione dell'immagine
    fun enableRemoveButton() {
        if (removeButton != null) removeButton?.isEnabled = true
    }

    // Disabilita il pulsante di rimozione dell'immagine
    fun disableRemoveButton() {
        if (removeButton != null) removeButton?.isEnabled = false
    }

    // Mostra l'immagine di default nell'ImageView
    fun loadDefaultImage() {
        if (inputField != null && defaultImageId != -1) {
            inputField!!.getInputView().setImageResource(defaultImageId)
        }
    }

    // Mostra l'immagine salvata al percorso "reference" nell'ImageView
    fun loadImageFromReference(context: Context, reference: StorageReference) {
        if (inputField != null)
            ImageLoader.loadFromReference(
                context,
                reference,
                inputField!!.getInputView(),
                defaultImageId,
                defaultImageId)
    }

    // Mostra l'immagine Bitmap nell'ImageView
    fun loadBitmap(bitmap: Bitmap?) {
        if (inputField != null)
            ImageLoader.loadBitmap(inputField!!.getInputView(), bitmap)
    }

    // Mostra l'immagine di default nell'ImageView
    fun loadFromUri(imageUri: Uri?) {
        if (inputField != null) {
            inputField!!.getInputView().setImageURI(imageUri)
        }
    }

    // Eseguita al click sul pulsante di selezione dell'immagine
    private fun onSelect() {
        onSelectButtonClicked.invoke()
    }

    /* Indica che è stata correttamente portata a termine un'operazione di selezione di un'immagine
    * e abilita il pulsante di rimozione dell'immagine.
     */
    fun onSelectSuccess() {
        validate()
        lastOperation = OperationType.SELECTION
        enableRemoveButton()
    }

    // Eseguita al click sul pulsante di rimozione dell'immagine
    private fun onRemove() {
        disableRemoveButton()
        lastOperation = OperationType.REMOVAL
        loadDefaultImage()
    }

    /* Builder class per la classe ImageViewValidator
    * Questa classe inizializza un oggetto ImageViewValidator e lo costruisce per passi attraverso metodi che
    * ritornano un'istanza della classe Builder stessa. Il metodo build() va eseguito al termine
    * della costruzione e ritorna l'oggetto ImageViewValidator personalizzato.
    *
    * Le proprietà della classe Builder sono usate solo durante la costruzione del validatore:
    *   validator: l'oggetto ImageViewValidator da costruire */
    class Builder: Validator.Builder<ImageView>() {
        override val validator: ImageViewValidator = ImageViewValidator()

        // Imposta un InputField su cui eseguire la validazione
        override fun setInputView(inputView: ImageView): Builder {
            validator.inputField = InputField(inputView)
            return this
        }

        // Imposta una TextView dove mostrare eventuali errori
        override fun setErrorView(errorView: TextView): Builder {
            validator.errorView = errorView
            return this
        }

        // Imposta un pulsante di selezione dell'immagine
        fun setSelectButton(selectButton: Button): Builder {
            selectButton.setOnClickListener { validator.onSelect() }
            return this
        }

        // Imposta un pulsante di rimozione dell'immagine
        fun setRemoveButton(removeButton: Button): Builder {
            validator.removeButton = removeButton
            return this
        }

        // Imposta una dimensione massima per l'immagine
        fun setMaxFileSize(kilobytes: Int): Builder {
            if (kilobytes < 100)
                validator.maxImageSizeKB = 100
            else if (kilobytes < 5120) // 5MB
                validator.maxImageSizeKB = kilobytes
            else
                validator.maxImageSizeKB = 5120
            return this
        }

        // Imposta l'immagine di default per l'ImageView
        fun setDefaultImage(resourceId: Int): Builder {
            validator.defaultImageId = resourceId
            return this
        }

        // Imposta la funzione da eseguire al click sul pulsante di selezione
        fun onSelectButtonClicked(function: () -> Unit): Builder {
            validator.onSelectButtonClicked = function
            return this
        }

        // Ultima la costruzione del validatore e lo ritorna
        override fun build(): ImageViewValidator {
            if (validator.inputField != null) {
                val imageView: ImageView = validator.inputField!!.getInputView()
                imageView.setOnClickListener { validator.onSelect() }
            }
            if (validator.removeButton != null)
                validator.removeButton!!.setOnClickListener { validator.onRemove() }
            validator.resetErrorMessage()
            return validator
        }
    }

}