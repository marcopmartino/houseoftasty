package it.project.houseoftasty.validation

import android.view.View
import android.widget.TextView

/* Classe per validare un campo di input. Ha come proprietà:
* inputField: campo di input con cui l'utente può interagire;
* errorView: TextView in cui viene mostrato l'eventuale messaggio di errore;
*
* La classe viene costruita facendo uso del pattern Builder */
abstract class Validator<T: View> {
    protected var inputField: InputField<T>? = null
    protected var errorView: TextView? = null

    /* Esegue la validazione del testo contenuto in inputView. Ritorna TRUE se non sono stati
    * riscontrati errori, altrimenti FALSE. */
    abstract fun validate(): Boolean

    // Ritorna l'id dell'EditText
    fun getInputViewTag(): String {
        return inputField?.getInputViewTag() ?: String()
    }

    // Ritorna il testo contenuto nell'EditText
    open fun getInputData(): Any {
        return inputField?.getInputData() ?: String()
    }

    // Mostra un messaggio di errore in errorView
    protected fun setErrorMessage(message: String) {
        if (errorView != null)
            errorView!!.text = message
    }

    fun resetErrorMessage() {
        setErrorMessage(String())
    }

    /* Builder class per la classe Validator
    * Questa classe inizializza un oggetto Validator e lo costruisce per passi attraverso metodi che
    * ritornano un'istanza della classe Builder stessa. Il metodo build() va eseguito al termine
    * della costruzione e ritorna l'oggetto Validator personalizzato.
    *
    * Le proprietà della classe Builder sono usate solo durante la costruzione del validatore:
    *   validator: l'oggetto Validator da costruire */
    abstract class Builder<T: View> {
        abstract val validator: Validator<T>

        // Imposta un InputField su cui eseguire la validazione
        abstract fun setInputView(inputView: T): Builder<T>

        // Imposta una TextView dove mostrare eventuali errori
        abstract fun setErrorView(errorView: TextView): Builder<T>

        // Ultima la costruzione del validatore e lo ritorna
        abstract fun build(): Validator<T>
    }

}