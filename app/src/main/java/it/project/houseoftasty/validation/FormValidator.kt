package it.project.houseoftasty.validation

import android.util.Log
import android.view.View
import android.widget.Button

/* Classe per gestire la validazione di una Form. Ha come proprietà:
* validators: MutableList con i validatori associati ai campi EditText della form;
* unvalidatedFields: MutableList con i campi non validati;
* onValidationCompleted: funzione da eseguire successivamente alla validazione della form;
* onValidationSuccess: funzione da eseguire se la validazione della form ha successo;
* onValidationFailure: funzione da eseguire se la validazione della form non ha successo;
* onDeleteButtonClicked: funzione da eseguire al click sul pulsante di eliminazione.
*
* La classe viene costruita facendo uso del pattern Builder */
open class FormValidator {
    private var validators: MutableList<Validator> = mutableListOf()
    private var unvalidatedFields: MutableList<InputField<View>> = mutableListOf()
    private var onValidationCompleted: (formData: MutableMap<String, Any>, hasDataChanged: Boolean)
        -> Unit = { _: MutableMap<String, Any>, _: Boolean -> }
    private var onValidationSuccess: (formData: MutableMap<String, Any>, hasDataChanged: Boolean)
        -> Unit = { _: MutableMap<String, Any>, _: Boolean -> }
    private var onValidationFailure: () -> Unit = {}
    private var onDeleteButtonClicked: () -> Unit = {}

    // Eseguita al click sul pulsante di eliminazione
    private fun onDelete() {
        onDeleteButtonClicked()
    }

    // Eseguita al click sul pulsante di Submit
    private fun onSubmit() {
        val success = validateAll()
        val formData = getAllInputData()
        val hasDataChanged = hasAnyDataChanged()
        onValidationCompleted(formData, hasDataChanged)
        if (success) onValidationSuccess(formData, hasDataChanged) else onValidationFailure()
    }

    /* Esegue la validazione per ciascun Validator presente nella lista "validators".
    *  Ritorna TRUE se tutte le validazioni sono andate a buon fine, altrimenti FALSE. */
    private fun validateAll(): Boolean {
        var isValid = true
        for (validator in validators) {
            if (!validator.validate())
                isValid = false
        }
        return isValid
    }

    // Crea una mappa con tutti i dati inseriti nella form
    private fun getAllInputData(): MutableMap<String, Any> {
        val dataMap = mutableMapOf<String,Any>()
        for (validator in validators)
            dataMap[validator.getInputViewTag()] = validator.getInputData()
        for (field in unvalidatedFields)
            dataMap[field.getInputViewTag()] = field.getInputData()
        Log.d("TAG",validators.toString())
        return dataMap
    }

    // Indica se almeno un valore di un campo della form è stato modificato dall'utente
    private fun hasAnyDataChanged(): Boolean {
        var hasChanged = false
        for (validator in validators)
            if (validator.hasDataChanged()) hasChanged = true
        if (!hasChanged)
            for (field in unvalidatedFields)
                if (field.hasDataChanged()) hasChanged = true
        return hasChanged
    }

    /* Builder class per la classe FormValidator
    * Questa classe inizializza un oggetto FormValidator e lo costruisce per passi attraverso metodi
    * che ritornano un'istanza della classe Builder stessa. Il metodo build() va eseguito al termine
    * della costruzione e ritorna l'oggetto FormValidator personalizzato.
    *
    * Per maggiore flessibilità e comodità, questa classe Builder può lavorare sia con validatori
    * già costruiti (oggetti Validator), sia con validatori "semi-costruiti" (oggetti
    * Validator.Builder), anche contemporaneamente: in questo modo è possibile specificare una volta
    * sola le regole di validazione e altre opzioni che sono comuni a tutti i validatori
    * semi-costruiti, risparmiando righe di codice.
    * I validatori già costruiti non possono subire modifiche e pertanto non saranno influenzati
    * da modifiche alle proprietà dei validatori semi-costruiti.
    *
    * Questa classe Builder si occupa anche di ultimare la costruzione dei validatori semi-costruiti.
    *
    * Le proprietà della classe Builder sono usate solo durante la costruzione del validatore:
    * formValidator: l'oggetto FormValidator da costruire;
    * validatorBuilders: lista dei validatori "semi-costruiti" (oggetti Validator.Builder);
    * commonRules: lista delle regole di validazione comuni a tutti i validatori semi-costruiti;
    * prependCommonRules: indica se aggiungere le regole comuni a tutti i validatori semi-costruiti
    *   in cima o in fondo alla lista delle regole già definite per ognuno;
    * commonOnTextChangedValidation: indica se la validazione comune onTextChanged (per i validatori
    *   semi-costruiti che lo consentono) è abilitata;
    * commonOnFocusLostValidation: indica se la validazione comune onFocusLost (per i validatori
    *   semi-costruiti che lo consentono) è abilitata.
    *  */
    class Builder {
        private val formValidator: FormValidator = FormValidator()
        private val validatorBuilders: MutableList<Validator.Builder> = mutableListOf()
        private val commonRules: MutableList<ValidationRule> = mutableListOf()
        private var prependCommonRules: Boolean = true
        private var commonOnTextChangedValidation: Boolean = false
        private var commonOnFocusLostValidation: Boolean = false

        // Imposta un pulsante di Submit per il FormValidator
        fun setSubmitButton(submitButton: Button): Builder {
            submitButton.setOnClickListener { formValidator.onSubmit() }
            return this
        }

        // Imposta un pulsante di eliminazione per il FormValidator
        fun setDeleteButton(deleteButton: Button): Builder {
            deleteButton.setOnClickListener { formValidator.onDelete() }
            return this
        }

        // Aggiunge uno o più validatori alla lista dei validatori di FormValidator
        fun addValidators(vararg validators: Validator): Builder {
            for (validator in validators)
                formValidator.validators.add(validator)
            return this
        }

        // Aggiunge uno o più validatori semi-costruiti alla lista "validatorBuilders" di questa classe
        fun addValidators(vararg builders: Validator.Builder): Builder {
            for (builder in builders)
                validatorBuilders.add(builder)
            return this
        }

        // Aggiunge uno o più campi non validati
        fun addUnvalidatedFields(vararg fields: View): Builder {
            for (field in fields)
                formValidator.unvalidatedFields.add(InputField(field))
            return this
        }

        /* Aggiunge una o più regole di validazione alla lista delle regole comuni a tutti i
        * validatori semi-costruiti. */
        fun addCommonRules(vararg rules: ValidationRule): Builder {
            for (rule in rules)
                commonRules.add(rule)
            return this
        }

        /* Fa in modo che la validazione delle regole comuni venga eseguita prima della validazione
        * delle regole specifiche di ogni validatore. */
        fun checkCommonRulesFirst(check: Boolean = true) {
            prependCommonRules = check
        }

        /* Abilita\disabilita la validazione comune automatica ogni volta che il testo del campo di
        * input cambia */
        fun enableOnTextChangedValidation(enabled: Boolean = true): Builder {
            commonOnTextChangedValidation = enabled
            return this
        }

        /* Abilita\disabilita la validazione comune automatica ogni volta che il campo di input
        * perde il focus */
        fun enableOnFocusLostValidation(enabled: Boolean = true): Builder {
            commonOnFocusLostValidation = enabled
            return this
        }

        // Imposta la funzione da eseguire successivamente alla validazione della form
        fun onValidationCompleted(function: (formData: MutableMap<String, Any>,
                                             hasDataChanged: Boolean) -> Unit): Builder {
            formValidator.onValidationCompleted = function
            return this
        }

        // Imposta la funzione da eseguire se la validazione della form ha successo
        fun onValidationSuccess(function: (formData: MutableMap<String, Any>,
                                           hasDataChanged: Boolean) -> Unit): Builder {
            formValidator.onValidationSuccess = function
            return this
        }

        // Imposta la funzione da eseguire se la validazione della form non ha successo
        fun onValidationFailure(function: () -> Unit): Builder {
            formValidator.onValidationFailure = function
            return this
        }

        // Imposta la funzione da eseguire al click sul pulsante di eliminazione
        fun onDeleteButtonClicked(function: () -> Unit): Builder {
            formValidator.onDeleteButtonClicked = function
            return this
        }

        // Ultima la costruzione del FormValidator e lo ritorna
        fun build(): FormValidator {
            for (builder in validatorBuilders) {
                for (rule in commonRules)
                    builder.addRules(rule, prepend = prependCommonRules)
                if (builder.allowExternalOptionChanges) {
                    builder.enableOnTextChangedValidation(commonOnTextChangedValidation)
                    builder.enableOnFocusLostValidation(commonOnFocusLostValidation)
                }
                formValidator.validators.add(builder.build())
            }
            return formValidator
        }
    }

}