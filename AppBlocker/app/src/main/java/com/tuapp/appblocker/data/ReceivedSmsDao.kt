package com.tuapp.appblocker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReceivedSmsDao {
    @Query("SELECT * FROM received_sms ORDER BY receivedAt DESC")
    suspend fun getAll(): List<ReceivedSms>

    @Insert
    suspend fun insert(message: ReceivedSms)

    @Query("DELETE FROM received_sms WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM received_sms")
    suspend fun clear()

    @Query(
        "DELETE FROM received_sms WHERE receivedAt < :cutoff OR id NOT IN " +
            "(SELECT id FROM received_sms ORDER BY receivedAt DESC LIMIT :maxRows)"
    )
    suspend fun prune(cutoff: Long, maxRows: Int)
}
