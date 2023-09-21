package it.project.houseoftasty.validation

import android.widget.EditText
import android.widget.TextView
import it.project.houseoftasty.utility.*

/* Classe per validare un campo di input di tipo EditText. Ha come proprietà:
*   rules: MutableList con le regole di validazione associate all'EditText
*
* La classe viene costruita facendo uso del pattern Builder */
class EditTextValidator: Validator<EditText>() {
    private var rules: MutableList<ValidationRule> = mutableListOf()

    /* Esegue la validazione del testo contenuto in inputView. Ritorna TRUE se non sono stati
    * riscontrati errori, altrimenti FALSE. */
    override fun validate(): Boolean {
        var isValid = true
        if (inputField != null) {
            val inputText: String = inputField!!.getInputData() as String

            // Con un ciclo controlla che ogni regola di validazione sia stata rispettata
            var counter = 0
            val rulesCounter = rules.size
            while (isValid && counter < rulesCounter) {
                isValid = rules[counter].validate(inputText)
                counter++
            }

            // Mostra il messaggio di errore della prima regola di validazione infranta
            if (isValid)
                resetErrorMessage()
            else
                setErrorMessage(rules[counter - 1].getErrorMessage())
        }
        return isValid
    }

    /* Builder class per la classe EditTextValidator
    * Questa classe inizializza un oggetto EditTextValidator e lo costruisce per passi attraverso metodi che
    * ritornano un'istanza della classe Builder stessa. Il metodo build() va eseguito al termine
    * della costruzione e ritorna l'oggetto EditTextValidator personalizzato.
    *
    * Le proprietà della classe Builder sono usate solo durante la costruzione del validatore:
    * validator: l'oggetto EditTextValidator da costruire
    * onTextChangedValidation: indica se la validazione onTextChanged è abilitata o meno;
    * onFocusLostValidation: indica se la validazione onFocusLost è abilitata o meno;
    * allowExternalOptionChanges: indica se onTextChangedValidation oppure onFocusLostValidation
    *   sono stati impostati esplicitamente attraverso i metodi del Builder. In questo modo non
    *   potranno essere sovrascritti da un FormManager (si veda il metodo build() della classe
    *   Builder di FormManager). */
    class Builder: Validator.Builder<EditText>() {
        override val validator: EditTextValidator = EditTextValidator()
        private var onTextChangedValidation: Boolean = false
        private var onFocusLostValidation: Boolean = false
        var allowExternalOptionChanges: Boolean = true

        /* Viene eseguito se onTextChangedValidation oppure onFocusLostValidation sono stati
        * impostati facendo uso dei metodi appositi */
        private fun blockExternalOptionChanges() {
            if (allowExternalOptionChanges)
                allowExternalOptionChanges = false
        }

        // Imposta un InputField su cui eseguire la validazione
        override fun setInputView(inputView: EditText): Builder {
            validator.inputField = InputField(inputView)
            return this
        }

        // Imposta una TextView dove mostrare eventuali errori
        override fun setErrorView(errorView: TextView): Builder {
            validator.errorView = errorView
            return this
        }

        /* Aggiunge una o più regole alla lista di regole del validatore. Il parametro prepend
        * serve a decidere se aggiungere le regole in cima o in fondo alla lista. */
        fun addRules(vararg rules: ValidationRule, prepend: Boolean = false): Builder {
            if (prepend)
                for (rule in rules)
                    validator.rules.prepend(rule)
            else
                for (rule in rules)
                    validator.rules.add(rule)
            return this
        }

        // Abilita\disabilita la validazione automatica ogni volta che il testo del campo di input cambia
        fun enableOnTextChangedValidation(enabled: Boolean = true): Builder {
            onTextChangedValidation = enabled
            blockExternalOptionChanges()
            return this
        }

        // Abilita\disabilita la validazione automatica ogni volta che il campo di input perde il focus
        fun enableOnFocusLostValidation(enabled: Boolean = true): Builder {
            onFocusLostValidation = enabled
            blockExternalOptionChanges()
            return this
        }

        // Ultima la costruzione del validatore e lo ritorna
        override fun build(): EditTextValidator {
            if (validator.inputField != null) {
                val inputView = validator.inputField!!.getInputView()
                if (onTextChangedValidation)
                    inputView.onTextChanged { validator.validate() }
                if (onFocusLostValidation)
                    inputView.onFocusLost { validator.validate() }
            }
            validator.resetErrorMessage()
            return validator
        }
    }

}