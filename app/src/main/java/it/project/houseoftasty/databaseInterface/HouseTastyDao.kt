package it.project.houseoftasty.databaseInterface

import androidx.room.*
import it.project.houseoftasty.dataModel.Product
import it.project.houseoftasty.dataModel.ProductId

@Dao
interface HouseTastyDao {

    @Query ("SELECT * FROM product")
    fun getAll(): Array<Product>

    @Query ("SELECT * FROM product WHERE id= :id")
    fun getById(id: String): Array<Product>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg product: Product)

    @Update
    fun update(product: Product)

    @Delete
    fun delete(product: Product)

    @Delete(entity = Product::class)
    fun deleteById(vararg id: ProductId)

}