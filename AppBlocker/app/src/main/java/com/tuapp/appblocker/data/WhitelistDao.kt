package com.tuapp.appblocker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WhitelistDao {
    @Query("SELECT * FROM whitelist_numbers")
    suspend fun getAllList(): List<WhitelistNumber>

    @Query("SELECT * FROM whitelist_numbers")
    fun getAll(): Flow<List<WhitelistNumber>>

    @Insert
    suspend fun insert(whitelistNumber: WhitelistNumber)

    @Insert
    suspend fun insertAll(whitelistNumbers: List<WhitelistNumber>)

    @Update
    suspend fun update(whitelistNumber: WhitelistNumber)

    @Delete
    suspend fun delete(whitelistNumber: WhitelistNumber)

    @Query("DELETE FROM whitelist_numbers")
    suspend fun deleteAll()
}
