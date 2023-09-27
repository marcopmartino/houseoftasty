package it.project.houseoftasty.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.project.houseoftasty.R
import it.project.houseoftasty.adapter.BindingAdapters.Companion.setFabVisibility
import it.project.houseoftasty.adapter.CommentAdapter
import it.project.houseoftasty.databinding.FragmentRecipeDetailsBinding
import it.project.houseoftasty.databinding.FragmentRecipePostBinding
import it.project.houseoftasty.model.RecipeCollection
import it.project.houseoftasty.network.RecipeCollectionNetwork
import it.project.houseoftasty.utility.DateTimeFormatter
import it.project.houseoftasty.utility.dateToString
import it.project.houseoftasty.utility.decrement
import it.project.houseoftasty.utility.increment
import it.project.houseoftasty.utility.textToInt
import it.project.houseoftasty.utility.timeToString
import it.project.houseoftasty.utility.toLocalDateTime
import it.project.houseoftasty.viewModel.RecipePostViewModel
import it.project.houseoftasty.viewModel.RecipePostViewModelFactory
import kotlinx.coroutines.runBlocking

class RecipePostFragment : Fragment() {

    private val recipePostViewModel: RecipePostViewModel by viewModels {
        RecipePostViewModelFactory(args.recipeId)
    }

    lateinit var binding: FragmentRecipePostBinding
    val recipeCollectionNetwork = RecipeCollectionNetwork()

    // Parametri passati al Fragment dalla navigazione
    private val args: RecipePostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflating e View Binding
        binding = FragmentRecipePostBinding.inflate(inflater, container, false)

        // Data Binding
        binding.viewModel = recipePostViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        val mainActivity = (activity as MainActivity)
        mainActivity.setActionBarTitle("Dettagli Ricetta")

        // Ottiene un riferimento al pulsante dei "Mi piace" e al relativo contatore
        val likeButton = binding.likeIcon
        val likeCounter = binding.likesCounter

        val downloadButton = binding.downloadIcon
        val downloadCounter = binding.downloadsCounter

        // Ottiene un riferimento al pulsante dei "Commenti" e al relativo contatore
        val commentIcon = binding.commentIcon
        val commentCounter = binding.commentsCounter

        // Ottiene un riferimento all'EditText e al pulsante per l'inserimento di commenti
        val commentText: EditText = binding.commentEditText
        val sendCommentButton: ImageView = binding.sendCommentButton

        // Aggiorna il contatore dei Mi Piace al click sull'icona
        fun updateLikeCounter() {
            likeCounter.text =
                if (recipePostViewModel.isRecipeLiked())
                    likeCounter.decrement()
                else
                    likeCounter.increment()
        }

        // Aggiorna il contatore dei Download al click sull'icona
        fun updateDownloadCounter(){
            downloadCounter.text =
                if(recipePostViewModel.isRecipeDownloaded()) {
                    downloadCounter.decrement()
                }else {
                    downloadCounter.increment()
                }
        }

        /* Osservatore su "recipeLiveData" per il binding del timestamp, per determinare la
            visibilità del F.A.B e il comportamento del pulsante dei "Mi piace" e dei "Download" */
        recipePostViewModel.recipeLiveData.observe(viewLifecycleOwner) {
            if (it.id != null) {

                // Binding del timestamp e dell'username del creatore
                val timestamp = (it.timestampPubblicazione as com.google.firebase.Timestamp).toLocalDateTime()

                // Controlla che la ricetta sia dell'utente
                if (it.idCreatore == recipePostViewModel.getCurrentUserId()) {

                    // Binding del sottotitolo della ricetta
                    binding.recipeInfo.text = DateTimeFormatter.localDateTimeToTemplate(
                        resources,
                        if (it.boolPostPrivato)
                            R.string.timestampTemplate_publicationPrivate
                        else
                            R.string.timestampTemplate_publication,
                        timestamp)

                    /* Imposta un clickListener sul F.A.B */
                    val fab = binding.floatingActionButton
                    fab.setFabVisibility(true)
                    fab.setOnClickListener {
                        fab.setFabVisibility(false)
                        fabOnClick()
                    }

                    // Nasconde l'EditText del commento
                    commentText.visibility = View.INVISIBLE
                    commentText.height = 0

                } else {
                    // Binding del sottotitolo della ricetta
                    binding.recipeInfo.text = resources.getString(
                        R.string.authorAndTimestampTemplate, it.nomeCreatore,
                        timestamp.dateToString(true), timestamp.timeToString())

                    // Imposta un clickListener per il pulsante dei "Mi piace"
                    likeButton.setOnClickListener {
                        likeButton.isClickable = false
                        updateLikeCounter()

                        // Prima parte dell'animazione: rotazione di 90° in 250ms
                        likeButton.animate().apply {
                            duration = 250
                            rotationYBy(90f)
                        }.withEndAction {

                            // Codice da eseguire tra le due parti dell'animazione
                            recipePostViewModel.toggleLikeButtonPressed()

                            // Seconda parte dell'animazione: rotazione di 270° in 750ms
                            likeButton.animate().apply {
                                duration = 750
                                rotationYBy(270f)
                            }.withEndAction {
                                likeButton.isClickable = true
                            }
                        }.start()
                    }

                    downloadButton.setOnClickListener {
                        downloadButton.isClickable = false
                        updateDownloadCounter()

                        // Prima parte dell'animazione: rotazione di 90° in 250ms
                        downloadButton.animate().apply {
                            duration = 250
                            rotationYBy(90f)
                        }.withEndAction {

                            // Codice da eseguire tra le due parti dell'animazione
                            recipePostViewModel.toggleDownloadButtonPressed()

                            // Seconda parte dell'animazione: rotazione di 270° in 750ms
                            downloadButton.animate().apply {
                                duration = 750
                                rotationYBy(270f)
                            }.withEndAction {
                                downloadButton.isClickable = true
                            }
                        }.start()
                    }
                }
            }
        }

        /* Osservatore su "likeButtonPressed" per determinare eseguire il toggle grafico dell'icona
        dei Mi Piace" */
        recipePostViewModel.likeButtonPressed.observe(viewLifecycleOwner) {
            if (recipePostViewModel.isRecipeCreatorNotCurrentUser()) {
                if (it)
                    likeButton.setImageResource(R.drawable.icon_heart_red)
                else
                    likeButton.setImageResource(R.drawable.icon_heart_empty)
            } else
                likeButton.setImageResource(R.drawable.icon_heart)
        }

        recipePostViewModel.downloadButtonPressed.observe(viewLifecycleOwner) {
            if (recipePostViewModel.isRecipeCreatorNotCurrentUser()) {
                if (it)
                    downloadButton.setImageResource(R.drawable.icon_tick)
                else
                    downloadButton.setImageResource(R.drawable.icon_download)
            } else
                downloadButton.setImageResource(R.drawable.icon_download)
        }

        // Sezione commenti
        val recyclerview: RecyclerView = binding.commentList
        val commentAdapter = CommentAdapter(requireContext(), resources) {
            adapterOnClick(it)
        }
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.adapter = commentAdapter

        // Osservatore sul LiveData dei commenti per aggiornare la lista
        recipePostViewModel.commentsLiveData.observe(viewLifecycleOwner) {
            it?.let {

                /* Invia all'Adapter la lista di commenti da mostrare */
                commentAdapter.submitList(it)
                commentAdapter.notifyDataSetChanged()
            }
        }

        // Per pubblicare un commento
        sendCommentButton.setOnClickListener {
            if (commentText.text.trim().isNotEmpty()) {
                sendCommentButton.isClickable = false

                sendCommentButton.setImageResource(R.drawable.icon_send_brown)

                val commentTextValue = commentText.text.trim().toString()
                Log.d("CommentDone", commentText.text.toString())

                // Svuota l'EditText, toglie il focus e chiude la tastiera a schermo
                commentText.text.clear()
                commentText.clearFocus()
                mainActivity.hideSoftKeyboard()
                sendCommentButton.animate().apply {
                    duration = 500
                    translationX(200f)
                }.withEndAction {
                    recipePostViewModel.addComment(commentTextValue)
                    commentCounter.text = commentCounter.increment()
                    sendCommentButton.animate().apply {
                        duration = 0
                        rotationYBy(180f)
                    }.withEndAction {
                        sendCommentButton.animate().apply {
                            duration = 500
                            translationXBy(-200f)
                        }.withEndAction {
                            sendCommentButton.animate().apply {
                                duration = 500
                                rotationYBy(180f)
                            }.withEndAction {
                                sendCommentButton.setImageResource(R.drawable.icon_send)
                                sendCommentButton.isClickable = true
                            }
                        }
                    }
                }.start()
            }
        }

        // Scorre verso la sezione commenti al click sull'icona dei commenti
        commentIcon.setOnClickListener {
            binding.recipePostScrollView.smoothScrollTo(0, binding.thirdDivider.bottom)
        }

    }

    /* Naviga verso PublicProfileFragment al click su un commento */
    private fun adapterOnClick(userId: String) {
        /*
        if (userId == recipePostViewModel.getCurrentUserId()) {
            navigateTo(RecipePostFragmentDirections
                .actionRecipePostFragmentToNavProfile("[nullValue]"))
        } else {
            navigateTo(RecipePostFragmentDirections
                .actionRecipePostFragmentToNavProfile(userId))
        }

         */

    }

    /* Naviga verso RecipeFormFragment al click sul F.A.B */
    private fun fabOnClick() {
       navigateTo(RecipePostFragmentDirections
            .actionRecipePostFragmentToRecipeDetailsFragment(args.recipeId))
    }

    // Funzione per navigare verso altri Fragment
    private fun navigateTo(direction: NavDirections) {
        requireView().findNavController().navigate(direction)
    }
}