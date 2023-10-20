package it.project.houseoftasty.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.model.Comment
import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeCollectionNetwork
import it.project.houseoftasty.utility.increment
import it.project.houseoftasty.utility.prepend
import kotlinx.coroutines.launch

class RecipePostViewModel(private val recipeId: String) : LoadingManagerViewModel() {
    private val dataSource: RecipeCollectionNetwork = RecipeCollectionNetwork.getDataSource()
    val recipeLiveData: MutableLiveData<Recipe> = MutableLiveData(Recipe())
    val commentsLiveData : MutableLiveData<MutableList<Comment>> = MutableLiveData(mutableListOf())
    val likeButtonPressed: MutableLiveData<Boolean> = MutableLiveData(false)
    val downloadButtonPressed: MutableLiveData<Boolean> = MutableLiveData(false)

    // Inizializzazione
    init {
        initialize()
    }

    // Inizializza la variabile "recipesLiveData"
    override suspend fun initAsync() {
        // Ottiene la ricetta dalla repository e aggiorna il LiveData
        // Il metodo "postValue" imposta il nuovo valore e notifica eventuali osservatori
        val recipe = dataSource.getRecipeById(recipeId,
            getImageReference = true,
            getCreatorName = true
        )
        if (recipe.idCreatore != dataSource.currentUserId) {
            dataSource.incrementViews(recipeId)
            recipe.views = recipe.views.increment()
        }
        recipeLiveData.postValue(recipe)
        likeButtonPressed.postValue(recipe.likes.contains(dataSource.currentUserId))
        downloadButtonPressed.postValue(recipe.downloads.contains(dataSource.currentUserId))
        commentsLiveData.postValue(dataSource.getCommentsByRecipeId(recipeId))
    }

    fun isRecipeLiked(): Boolean {
        return likeButtonPressed.value!!
    }

    fun isRecipeDownloaded(): Boolean {
        return downloadButtonPressed.value!!
    }

    fun isRecipeCreatorNotCurrentUser(): Boolean {
        Log.d("CurrentUserId - RecipeCreatorId", getCurrentUserId() + " - " + recipeLiveData.value?.idCreatore.toString())
        return getCurrentUserId() != (recipeLiveData.value?.idCreatore ?: String())
    }

    fun getCurrentUserId(): String {
        return dataSource.currentUserId
    }

    fun getCommentCreatorId(position: Int): String? {
        return commentsLiveData.value?.get(position)?.userId
    }

    // Fa il toggle di "likeButtonPressed" e notifica eventuali osservatori
    fun toggleLikeButtonPressed() {
        val isLiked = isRecipeLiked()
        likeButtonPressed.postValue(!isLiked)

        viewModelScope.launch {
            if (isLiked) dataSource.removeLike(recipeId) else dataSource.addLike(recipeId)
        }
    }

    fun toggleDownloadButtonPressed() {
        val isDownloaded = isRecipeDownloaded()
        downloadButtonPressed.postValue(!isDownloaded)

        viewModelScope.launch {
            if (isDownloaded) dataSource.removeDownload(recipeId) else dataSource.addDownload(recipeId)
        }
    }

    fun addComment(text: String) {
        viewModelScope.launch {
            val commentList = commentsLiveData.value ?: mutableListOf()
            commentList.prepend(dataSource.addComment(recipeId, text))
            commentsLiveData.postValue(commentList)
        }
    }

    fun removeComment(commentPosition: Int) {

        // Aggiorna il LiveData localmente
        val commentList: MutableList<Comment> = commentsLiveData.value ?: mutableListOf()
        val commentId = commentList[commentPosition].id.toString()
        commentList.removeAt(commentPosition)
        commentsLiveData.postValue(commentList)

        // Aggiorna la lista dei commenti in Firestore
        viewModelScope.launch {
            dataSource.removeComment(recipeId, commentId)
        }
    }
}

// Factory class
class RecipePostViewModelFactory(private val recipeId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipePostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipePostViewModel(recipeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}