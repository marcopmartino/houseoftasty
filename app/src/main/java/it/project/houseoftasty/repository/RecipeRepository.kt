package it.project.houseoftasty.repository

import it.project.houseoftasty.model.Recipe
import it.project.houseoftasty.network.RecipeNetwork

/* Handles operations on flowersLiveData and holds details about it. */
class RecipeRepository {

    private val recipeNetwork = RecipeNetwork()

    suspend fun getRecipeList(): MutableList<Recipe> {
        return recipeNetwork.getRecipesByUser()
    }

    suspend fun getRecipeById(recipeId: String): Recipe {
        return recipeNetwork.getRecipeById(recipeId)
    }

    suspend fun addRecipe(recipe: Recipe) {
        return recipeNetwork.addRecipe(recipe)
    }

    suspend fun updateRecipe(recipe: Recipe) {
        return recipeNetwork.updateRecipe(recipe)
    }

    suspend fun deleteRecipe(recipeId: String) {
        return recipeNetwork.deleteRecipeById(recipeId)
    }

/*
    /* Aggiunge la ricetta al LiveData */
    fun addRecipe(recipe: Recipe) {
        val currentList = recipesLiveData.value
        if (currentList == null) {
            recipesLiveData.postValue(listOf(recipe) as MutableList<Recipe>)
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, recipe)
            recipesLiveData.postValue(updatedList)
        }
    }
*/

/*
    /* Rimuove la ricetta dal LiveData. */
    fun removeRecipe(recipe: Recipe) {
        val currentList = recipesLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(recipe)
            recipesLiveData.postValue(updatedList)
        }
    }
*/

/*
    /* Returns flower given an ID. */
    fun getFlowerForId(id: Long): Recipe? {
        recipesLiveData.value?.let { recipes ->
            return recipes.firstOrNull{ it.id == id}
        }
        return null
    }
*/

    /*
    /* Returns a random flower asset for flowers that are added. */
    fun getRandomFlowerImageAsset(): Int? {
        val randomNumber = (initialRecipesList.indices).random()
        return initialRecipesList[randomNumber].image
    }
*/

    /* Factory method */
    companion object {
        private var INSTANCE: RecipeRepository? = null

        fun getDataSource(): RecipeRepository {
            return synchronized(RecipeRepository::class) {
                val newInstance = INSTANCE ?: RecipeRepository()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}