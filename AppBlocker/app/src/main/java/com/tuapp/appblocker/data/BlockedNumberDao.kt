package com.tuapp.appblocker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedNumberDao {
    @Query("SELECT * FROM blocked_numbers")
    fun getAll(): Flow<List<BlockedNumber>>

    @Query("SELECT * FROM blocked_numbers")
    suspend fun getAllList(): List<BlockedNumber>

    @Query("DELETE FROM blocked_numbers")
    suspend fun deleteAll()

    @Update
    suspend fun update(blockedNumber: BlockedNumber)

    @Insert
    suspend fun insert(blockedNumber: BlockedNumber)

    @Insert
    suspend fun insertAll(blockedNumbers: List<BlockedNumber>)

    @Delete
    suspend fun delete(blockedNumber: BlockedNumber)
}
