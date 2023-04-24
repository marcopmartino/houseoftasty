package it.project.houseoftasty.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.project.houseoftasty.dataModel.Product
import it.project.houseoftasty.databaseInterface.HouseTastyDao

@Database (entities = [Product::class], version = 1)
abstract class HouseTastyDb: RoomDatabase() {
    abstract fun houseTastyDAO(): HouseTastyDao

    companion object{
        @Volatile
        private var INSTANCE: HouseTastyDb? = null

        fun getInstance(context: Context): HouseTastyDb{
            return INSTANCE?: synchronized(this){
                INSTANCE?: Room.databaseBuilder(
                    context.applicationContext,
                    HouseTastyDb::class.java, "houseTasty_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}