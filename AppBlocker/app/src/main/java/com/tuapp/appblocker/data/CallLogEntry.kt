package com.tuapp.appblocker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val phoneNumber: String,
    val action: String, // "BLOQUEADA" o "PERMITIDA"
    val reason: String
)
