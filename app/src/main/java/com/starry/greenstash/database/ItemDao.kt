package com.starry.greenstash.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDao {

    @Query("SELECT * FROM greenstash ORDER BY id ASC")
    fun getItems(): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("UPDATE greenstash SET amount = :amount WHERE id = :id" )
    suspend fun  updateAmount(id: Int, amount: Float)
}