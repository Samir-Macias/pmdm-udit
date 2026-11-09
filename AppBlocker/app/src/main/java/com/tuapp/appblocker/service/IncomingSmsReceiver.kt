package com.tuapp.appblocker.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.SQLException
import android.provider.Telephony
import android.util.Log
import com.tuapp.appblocker.SmsClassifier
import com.tuapp.appblocker.data.AppDatabase
import com.tuapp.appblocker.data.ReceivedSms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IncomingSmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = try {
            Telephony.Sms.Intents.getMessagesFromIntent(intent)
        } catch (error: IllegalArgumentException) {
            Log.e(TAG, "No se pudo interpretar el SMS recibido.", error)
            return
        }
        if (messages.isNullOrEmpty()) return

        val sender = messages.firstOrNull()?.originatingAddress ?: "Desconocido"
        val body = messages.joinToString(separator = "") { it.messageBody.orEmpty() }
        val receivedAt = messages.firstOrNull()?.timestampMillis ?: System.currentTimeMillis()
        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(appContext)
                val decision = SmsClassifier.classify(
                    sender,
                    body,
                    database.whitelistDao().getAllList(),
                    database.blockedNumberDao().getAllList()
                )
                database.receivedSmsDao().insert(
                    ReceivedSms(
                        sender = sender,
                        body = body,
                        receivedAt = receivedAt,
                        classification = if (decision.suspicious) "SOSPECHOSO" else "NORMAL",
                        reason = decision.reason
                    )
                )
                database.receivedSmsDao().prune(
                    cutoff = System.currentTimeMillis() - RETENTION_MILLIS,
                    maxRows = MAX_STORED_MESSAGES
                )
            } catch (error: SecurityException) {
                Log.e(TAG, "No se pudo guardar el SMS por falta de permisos.", error)
            } catch (error: IllegalStateException) {
                Log.e(TAG, "No se pudo abrir la base de datos de SMS.", error)
            } catch (error: SQLException) {
                Log.e(TAG, "No se pudo guardar el SMS en la base de datos.", error)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        const val TAG = "IncomingSmsReceiver"
        const val MAX_STORED_MESSAGES = 500
        const val RETENTION_MILLIS = 30L * 24 * 60 * 60 * 1000
    }
}
