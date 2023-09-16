package it.project.houseoftasty.repository

import android.util.Log
import it.project.houseoftasty.model.Profile
import it.project.houseoftasty.network.ProfileNetwork

/* Handles operations on flowersLiveData and holds details about it. */
class ProfileRepository {

    private val profileNetwork = ProfileNetwork()

    suspend fun getProfileData(): Profile{
        return profileNetwork.getUserData()
    }

    suspend fun addProfile(profile: Profile) {
        return profileNetwork.addUser(profile)
    }

    suspend fun updateProfile(profile: Profile) {
        return profileNetwork.updateUser(profile)
    }

    /* Factory method */
    companion object {
        private var INSTANCE: ProfileRepository? = null

        fun getDataSource(): ProfileRepository {
            return synchronized(ProfileRepository::class) {
                val newInstance = INSTANCE ?: ProfileRepository()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}