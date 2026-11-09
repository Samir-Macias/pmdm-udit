package com.tuapp.appblocker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "blocked_numbers")
data class BlockedNumber(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prefix: String,
    var isActive: Boolean = true,
    @ColumnInfo(defaultValue = "0") val priority: Int = 0,
    @ColumnInfo(defaultValue = "'PREFIX'") val matchType: String = "PREFIX"
)