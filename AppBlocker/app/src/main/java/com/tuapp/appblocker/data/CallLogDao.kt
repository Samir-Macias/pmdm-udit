package com.tuapp.appblocker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CallLogDao {
    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    suspend fun getAllList(): List<CallLogEntry>

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<CallLogEntry>>

    @Insert
    suspend fun insert(log: CallLogEntry)

    @Query("DELETE FROM call_logs")
    suspend fun clearLogs()
}
