package com.tuapp.appblocker

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuapp.appblocker.data.AppDatabase
import com.tuapp.appblocker.data.ReceivedSms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsInboxActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var adapter: ReceivedSmsAdapter
    private lateinit var permissionButton: Button
    private lateinit var emptyState: TextView
    private var showOnlySuspicious = false
    private var allMessages: List<ReceivedSms> = emptyList()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        updatePermissionState()
        if (granted) {
            Toast.makeText(this, "Recepción de SMS activada.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                "Sin este permiso, AppBlocker no puede analizar SMS nuevos.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_inbox)
        database = AppDatabase.getDatabase(this)
        permissionButton = findViewById(R.id.btnSmsPermission)
        emptyState = findViewById(R.id.tvSmsEmpty)

        adapter = ReceivedSmsAdapter(
            emptyList(),
            onOpen = ::showMessage,
            onDelete = ::deleteMessage
        )
        findViewById<RecyclerView>(R.id.recyclerSms).apply {
            layoutManager = LinearLayoutManager(this@SmsInboxActivity)
            adapter = this@SmsInboxActivity.adapter
        }

        findViewById<Button>(R.id.btnBackToMain).setOnClickListener { finish() }
        permissionButton.setOnClickListener {
            if (hasSmsPermission()) {
                Toast.makeText(this, "La recepción de SMS ya está activada.", Toast.LENGTH_SHORT).show()
            } else {
                permissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
            }
        }
        findViewById<Button>(R.id.btnSmsAll).setOnClickListener {
            showOnlySuspicious = false
            refreshMessages()
        }
        findViewById<Button>(R.id.btnSmsSuspicious).setOnClickListener {
            showOnlySuspicious = true
            refreshMessages()
        }
        findViewById<Button>(R.id.btnClearSms).setOnClickListener {
            confirmClearMessages()
        }
        updatePermissionState()
        refreshMessages()
    }

    override fun onResume() {
        super.onResume()
        if (::database.isInitialized) {
            updatePermissionState()
            refreshMessages()
        }
    }

    private fun hasSmsPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) ==
            PackageManager.PERMISSION_GRANTED

    private fun updatePermissionState() {
        val granted = hasSmsPermission()
        permissionButton.text = if (granted) "Recepción de SMS activada" else "Activar análisis de SMS"
        findViewById<TextView>(R.id.tvSmsPermissionInfo).text = if (granted) {
            "Se guardan localmente hasta 500 mensajes durante un máximo de 30 días."
        } else {
            "Con tu permiso, se analizarán SMS nuevos. No se leen ni modifican los mensajes de tu app habitual."
        }
    }

    private fun refreshMessages() {
        lifecycleScope.launch {
            allMessages = withContext(Dispatchers.IO) {
                database.receivedSmsDao().getAll()
            }
            val visibleMessages = if (showOnlySuspicious) {
                allMessages.filter { it.classification == "SOSPECHOSO" }
            } else {
                allMessages
            }
            adapter.updateData(visibleMessages)
            emptyState.isVisible = visibleMessages.isEmpty()
            findViewById<RecyclerView>(R.id.recyclerSms).isVisible = visibleMessages.isNotEmpty()
            emptyState.text = when {
                visibleMessages.isNotEmpty() -> ""
                !hasSmsPermission() -> "Activa el permiso para empezar a analizar SMS nuevos."
                showOnlySuspicious -> "No hay mensajes sospechosos guardados."
                else -> "Aún no se han recibido SMS desde que activaste el filtro."
            }
            findViewById<TextView>(R.id.tvSmsCount).text =
                if (showOnlySuspicious) "${visibleMessages.size} sospechosos" else "${allMessages.size} mensajes"
            findViewById<Button>(R.id.btnSmsAll).isSelected = !showOnlySuspicious
            findViewById<Button>(R.id.btnSmsSuspicious).isSelected = showOnlySuspicious
        }
    }

    private fun showMessage(message: ReceivedSms) {
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(message.receivedAt))
        AlertDialog.Builder(this)
            .setTitle(message.sender)
            .setMessage(
                "$date\n${message.classification}\n${message.reason}\n\n${message.body}"
            )
            .setPositiveButton("Cerrar", null)
            .setNeutralButton("Eliminar") { _, _ -> deleteMessage(message) }
            .show()
    }

    private fun deleteMessage(message: ReceivedSms) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                database.receivedSmsDao().delete(message.id)
            }
            refreshMessages()
        }
    }

    private fun confirmClearMessages() {
        AlertDialog.Builder(this)
            .setTitle("Vaciar bandeja SMS")
            .setMessage("Se eliminarán los mensajes guardados dentro de AppBlocker.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Vaciar") { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.receivedSmsDao().clear()
                    }
                    refreshMessages()
                    Toast.makeText(this@SmsInboxActivity, "Bandeja vaciada.", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }
}
