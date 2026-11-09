package com.tuapp.appblocker.service

import android.telecom.Call
import android.telecom.CallScreeningService
import com.tuapp.appblocker.RuleEngine
import com.tuapp.appblocker.data.AppDatabase
import com.tuapp.appblocker.data.CallLogEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpamCallScreeningService : CallScreeningService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: "Desconocido"

        serviceScope.launch {
            val database = AppDatabase.getDatabase(applicationContext)
            val whitelist = database.whitelistDao().getAllList()
            val blockedNumbersList = database.blockedNumberDao().getAllList()
            val decision = RuleEngine.evaluate(phoneNumber, whitelist, blockedNumbersList)

            database.callLogDao().insert(
                CallLogEntry(
                    timestamp = System.currentTimeMillis(),
                    phoneNumber = phoneNumber,
                    action = if (decision.blocked) "BLOQUEADA" else "PERMITIDA",
                    reason = decision.reason
                )
            )

            if (decision.blocked) {
                val response = CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(true)
                    .build()

                respondToCall(callDetails, response)
            } else {
                val response = CallResponse.Builder()
                    .setDisallowCall(false)
                    .setRejectCall(false)
                    .build()

                respondToCall(callDetails, response)
            }
        }
    }
}
