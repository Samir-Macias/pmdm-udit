package com.tuapp.appblocker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "received_sms")
data class ReceivedSms(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val body: String,
    val receivedAt: Long,
    val classification: String,
    val reason: String
)
