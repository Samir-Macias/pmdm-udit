package com.tuapp.appblocker

import android.app.AlertDialog
import android.app.role.RoleManager
import android.database.SQLException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.withTransaction
import com.tuapp.appblocker.data.AppDatabase
import com.tuapp.appblocker.data.BlockedNumber
import com.tuapp.appblocker.data.CallLogEntry
import com.tuapp.appblocker.data.WhitelistNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private enum class RuleList { BLOCKED, WHITELIST }

    private lateinit var database: AppDatabase
    private lateinit var blockedAdapter: BlockedNumberAdapter
    private lateinit var whitelistAdapter: WhitelistNumberAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    private lateinit var prefixInput: EditText
    private lateinit var priorityInput: EditText
    private lateinit var priorityGroup: android.view.View
    private lateinit var matchTypeGroup: RadioGroup
    private lateinit var saveButton: Button
    private var selectedList = RuleList.BLOCKED
    private var editingBlockedRule: BlockedNumber? = null
    private var editingWhitelistRule: WhitelistNumber? = null

    private val roleRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val message = if (result.resultCode == RESULT_OK) {
            "Filtro de llamadas activado."
        } else {
            "No se concedió el rol de filtro de llamadas."
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val importCsvLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let(::importCsv)
    }

    private val exportCsvLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let(::exportCsv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)
        prefixInput = findViewById(R.id.etPrefix)
        priorityInput = findViewById(R.id.etPriority)
        priorityGroup = findViewById(R.id.priorityGroup)
        matchTypeGroup = findViewById(R.id.matchTypeGroup)
        saveButton = findViewById(R.id.btnSaveRule)
        recyclerView = findViewById(R.id.recyclerView)
        emptyState = findViewById(R.id.tvEmptyState)

        setupRecyclerView()
        setupButtons()
        switchList(RuleList.BLOCKED)
        refreshRules()
        refreshOverview()
    }

    override fun onResume() {
        super.onResume()
        if (::database.isInitialized) refreshOverview()
    }

    private fun setupRecyclerView() {
        blockedAdapter = BlockedNumberAdapter(
            emptyList(),
            onDeleteClick = { confirmDeleteBlocked(it) },
            onToggleClick = { number, isChecked -> updateNumberStatus(number, isChecked) },
            onEditClick = ::editBlockedRule
        )
        whitelistAdapter = WhitelistNumberAdapter(
            emptyList(),
            onEditClick = ::editWhitelistRule,
            onDeleteClick = { confirmDeleteWhitelist(it) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnBlockedTab).setOnClickListener {
            switchList(RuleList.BLOCKED)
        }
        findViewById<Button>(R.id.btnWhitelistTab).setOnClickListener {
            switchList(RuleList.WHITELIST)
        }
        saveButton.setOnClickListener { saveCurrentRule() }
        findViewById<Button>(R.id.btnCancelEdit).setOnClickListener {
            clearEditor()
        }
        findViewById<Button>(R.id.btnRequestRole).setOnClickListener {
            requestCallScreeningRole()
        }
        findViewById<Button>(R.id.btnViewLogs).setOnClickListener {
            showCallLogs()
        }
        findViewById<Button>(R.id.btnSmsFeature).setOnClickListener {
            startActivity(Intent(this, SmsInboxActivity::class.java))
        }
        findViewById<Button>(R.id.btnImportCsv).setOnClickListener {
            importCsvLauncher.launch(arrayOf("text/*", "application/csv", "application/vnd.ms-excel"))
        }
        findViewById<Button>(R.id.btnExportCsv).setOnClickListener {
            exportCsvLauncher.launch("appblocker-reglas.csv")
        }
        findViewById<Button>(R.id.btnSimulate).setOnClickListener {
            val number = findViewById<EditText>(R.id.etSimulateNumber).text.toString().trim()
            if (number.any(Char::isDigit)) {
                simulateCall(number)
            } else {
                Toast.makeText(this, "Introduce un número válido para simular.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switchList(list: RuleList) {
        selectedList = list
        clearEditor()
        val blocked = list == RuleList.BLOCKED
        recyclerView.adapter = if (blocked) blockedAdapter else whitelistAdapter
        priorityGroup.isVisible = blocked
        matchTypeGroup.isVisible = blocked
        findViewById<Button>(R.id.btnBlockedTab).isSelected = blocked
        findViewById<Button>(R.id.btnWhitelistTab).isSelected = !blocked
        prefixInput.hint = if (blocked) "Prefijo o número a bloquear" else "Prefijo o número permitido"
        findViewById<TextView>(R.id.tvEditorTitle).text =
            if (blocked) "Nueva regla de bloqueo" else "Nueva excepción"
        saveButton.text = if (blocked) "Añadir regla" else "Añadir excepción"
        emptyState.text = if (blocked) {
            "Todavía no hay reglas de bloqueo."
        } else {
            "Todavía no hay números en la lista blanca."
        }
        refreshRules()
    }

    private fun refreshRules() {
        lifecycleScope.launch {
            val (blocked, whitelist) = withContext(Dispatchers.IO) {
                database.blockedNumberDao().getAllList() to database.whitelistDao().getAllList()
            }
            blockedAdapter.updateData(
                blocked.sortedWith(
                    compareByDescending<BlockedNumber> { it.priority }
                        .thenByDescending { it.prefix.length }
                )
            )
            whitelistAdapter.updateData(whitelist.sortedBy { it.prefix })
            val hasRules = if (selectedList == RuleList.BLOCKED) blocked.isNotEmpty() else whitelist.isNotEmpty()
            recyclerView.isVisible = hasRules
            emptyState.isVisible = !hasRules
            findViewById<TextView>(R.id.tvRulesCount).text =
                if (selectedList == RuleList.BLOCKED) "${blocked.size} reglas" else "${whitelist.size} excepciones"
        }
    }

    private fun saveCurrentRule() {
        val prefix = prefixInput.text.toString().trim()
        if (prefix.none(Char::isDigit)) {
            prefixInput.error = "Introduce al menos un dígito."
            return
        }

        if (selectedList == RuleList.BLOCKED) {
            val priority = priorityInput.text.toString().toIntOrNull()
            if (priority == null) {
                priorityInput.error = "La prioridad debe ser un número entero."
                return
            }
            val existing = editingBlockedRule
            val matchType = selectedMatchType()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if (existing == null) {
                        database.blockedNumberDao().insert(
                            BlockedNumber(
                                prefix = prefix,
                                priority = priority,
                                matchType = matchType
                            )
                        )
                    } else {
                        database.blockedNumberDao().update(
                            existing.copy(
                                prefix = prefix,
                                priority = priority,
                                matchType = matchType
                            )
                        )
                    }
                }
                clearEditor()
                refreshRules()
                Toast.makeText(
                    this@MainActivity,
                    if (existing == null) "Regla añadida." else "Regla actualizada.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val existing = editingWhitelistRule
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if (existing == null) {
                        database.whitelistDao().insert(WhitelistNumber(prefix = prefix))
                    } else {
                        database.whitelistDao().update(existing.copy(prefix = prefix))
                    }
                }
                clearEditor()
                refreshRules()
                Toast.makeText(
                    this@MainActivity,
                    if (existing == null) "Excepción añadida." else "Excepción actualizada.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun editBlockedRule(number: BlockedNumber) {
        switchList(RuleList.BLOCKED)
        editingBlockedRule = number
        prefixInput.setText(number.prefix)
        priorityInput.setText(number.priority.toString())
        matchTypeGroup.check(
            if (number.matchType == "EXACT") R.id.rbExactMatch else R.id.rbPrefixMatch
        )
        saveButton.text = "Guardar cambios"
        findViewById<TextView>(R.id.tvEditorTitle).text = "Editar regla"
        findViewById<Button>(R.id.btnCancelEdit).isVisible = true
    }

    private fun editWhitelistRule(number: WhitelistNumber) {
        switchList(RuleList.WHITELIST)
        editingWhitelistRule = number
        prefixInput.setText(number.prefix)
        saveButton.text = "Guardar cambios"
        findViewById<TextView>(R.id.tvEditorTitle).text = "Editar excepción"
        findViewById<Button>(R.id.btnCancelEdit).isVisible = true
    }

    private fun clearEditor() {
        editingBlockedRule = null
        editingWhitelistRule = null
        if (!::prefixInput.isInitialized) return
        prefixInput.text.clear()
        prefixInput.error = null
        priorityInput.text.clear()
        priorityInput.error = null
        matchTypeGroup.check(R.id.rbPrefixMatch)
        saveButton.text = if (selectedList == RuleList.BLOCKED) "Añadir regla" else "Añadir excepción"
        findViewById<Button>(R.id.btnCancelEdit).isVisible = false
        findViewById<TextView>(R.id.tvEditorTitle).text =
            if (selectedList == RuleList.BLOCKED) "Nueva regla de bloqueo" else "Nueva excepción"
    }

    private fun selectedMatchType(): String {
        return if (matchTypeGroup.checkedRadioButtonId == R.id.rbExactMatch) "EXACT" else "PREFIX"
    }

    private fun confirmDeleteBlocked(number: BlockedNumber) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar regla")
            .setMessage("¿Quieres eliminar el prefijo ${number.prefix}?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.blockedNumberDao().delete(number)
                    }
                    refreshRules()
                }
            }
            .show()
    }

    private fun confirmDeleteWhitelist(number: WhitelistNumber) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar excepción")
            .setMessage("¿Quieres quitar ${number.prefix} de la lista blanca?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.whitelistDao().delete(number)
                    }
                    refreshRules()
                }
            }
            .show()
    }

    private fun updateNumberStatus(number: BlockedNumber, isChecked: Boolean) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                database.blockedNumberDao().update(number.copy(isActive = isChecked))
            }
            refreshRules()
        }
    }

    private fun simulateCall(phoneNumber: String) {
        lifecycleScope.launch {
            val decision = withContext(Dispatchers.IO) {
                RuleEngine.evaluate(
                    phoneNumber,
                    database.whitelistDao().getAllList(),
                    database.blockedNumberDao().getAllList()
                )
            }
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Resultado de la simulación")
                .setMessage(
                    "Número: $phoneNumber\n\n" +
                        "Resultado: ${if (decision.blocked) "BLOQUEADA" else "PERMITIDA"}\n" +
                        "Motivo: ${decision.reason}\n\n" +
                        "La simulación no modifica las estadísticas de llamadas reales."
                )
                .setPositiveButton("Entendido", null)
                .show()
        }
    }

    private fun refreshOverview() {
        lifecycleScope.launch {
            val logs = withContext(Dispatchers.IO) {
                database.callLogDao().getAllList()
            }
            findViewById<TextView>(R.id.tvBlockedCount).text =
                logs.count { it.action == "BLOQUEADA" }.toString()
            findViewById<TextView>(R.id.tvAllowedCount).text =
                logs.count { it.action == "PERMITIDA" }.toString()
        }
    }

    private fun showCallLogs() {
        lifecycleScope.launch {
            val logs = withContext(Dispatchers.IO) {
                database.callLogDao().getAllList()
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val ruleStats = logs
                .filter { it.action == "BLOQUEADA" }
                .groupingBy(CallLogEntry::reason)
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .joinToString("\n") { "• ${it.key}: ${it.value}" }
                .ifEmpty { "Sin reglas aplicadas todavía." }
            val recentLogs = logs.take(30).joinToString("\n\n") { log ->
                val date = dateFormat.format(Date(log.timestamp))
                "[$date] ${log.phoneNumber}\n${log.action} · ${log.reason}"
            }.ifEmpty { "No hay registros de llamadas aún." }

            AlertDialog.Builder(this@MainActivity)
                .setTitle("Estadísticas y actividad")
                .setMessage(
                    "Total: ${logs.size} · Bloqueadas: ${logs.count { it.action == "BLOQUEADA" }} · " +
                        "Permitidas: ${logs.count { it.action == "PERMITIDA" }}\n\n" +
                        "Reglas aplicadas\n$ruleStats\n\nÚltimas llamadas\n$recentLogs"
                )
                .setPositiveButton("Cerrar", null)
                .setNeutralButton("Limpiar historial") { _, _ ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            database.callLogDao().clearLogs()
                        }
                        refreshOverview()
                        Toast.makeText(this@MainActivity, "Historial eliminado.", Toast.LENGTH_SHORT).show()
                    }
                }
                .show()
        }
    }

    private fun importCsv(uri: android.net.Uri) {
        lifecycleScope.launch {
            try {
                val entries = withContext(Dispatchers.IO) {
                    val csv = contentResolver.openInputStream(uri)
                        ?.bufferedReader(Charsets.UTF_8)
                        ?.use { it.readText() }
                        ?: throw IOException("No se pudo leer el archivo seleccionado.")
                    RuleCsvCodec.parse(csv)
                }
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Importar reglas")
                    .setMessage(
                        "Formato: type,prefix,active,priority,match_type. " +
                            "match_type puede ser PREFIX o EXACT. También se aceptan CSV antiguos de cuatro columnas.\n\n" +
                            "Se importarán ${entries.size} reglas y excepciones. " +
                            "Las listas actuales serán reemplazadas. ¿Continuar?"
                    )
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Reemplazar") { _, _ ->
                        lifecycleScope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    database.withTransaction {
                                        database.blockedNumberDao().deleteAll()
                                        database.whitelistDao().deleteAll()
                                        database.blockedNumberDao().insertAll(
                                            entries.filter { it.type == RuleCsvEntry.Type.BLOCK }
                                                .map {
                                                    BlockedNumber(
                                                        prefix = it.prefix,
                                                        isActive = it.active,
                                                        priority = it.priority
                                                    )
                                                }
                                        )
                                        database.whitelistDao().insertAll(
                                            entries.filter {
                                                it.type == RuleCsvEntry.Type.WHITELIST && it.active
                                            }.map { WhitelistNumber(prefix = it.prefix) }
                                        )
                                    }
                                }
                                clearEditor()
                                refreshRules()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Listas importadas correctamente.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (error: SQLException) {
                                showCsvError(error.message ?: "No se pudieron guardar las reglas importadas.")
                            } catch (error: IllegalArgumentException) {
                                showCsvError(error.message ?: "No se pudieron guardar las reglas importadas.")
                            }
                        }
                    }
                    .show()
            } catch (error: IOException) {
                showCsvError(error.message ?: "No se pudo leer el archivo.")
            } catch (error: SecurityException) {
                showCsvError(error.message ?: "No hay permiso para leer el archivo.")
            } catch (error: IllegalArgumentException) {
                showCsvError(error.message ?: "El formato del CSV no es válido.")
            } catch (error: SQLException) {
                showCsvError(error.message ?: "No se pudieron guardar las reglas importadas.")
            }
        }
    }

    private fun exportCsv(uri: android.net.Uri) {
        lifecycleScope.launch {
            try {
                val csv = withContext(Dispatchers.IO) {
                    val blocked = database.blockedNumberDao().getAllList()
                    val whitelist = database.whitelistDao().getAllList()
                    RuleCsvCodec.encode(blocked, whitelist)
                }
                withContext(Dispatchers.IO) {
                    val output = contentResolver.openOutputStream(uri)
                        ?: throw IOException("No se pudo crear el archivo seleccionado.")
                    output.bufferedWriter(Charsets.UTF_8).use { it.write(csv) }
                }
                Toast.makeText(this@MainActivity, "Reglas exportadas correctamente.", Toast.LENGTH_SHORT).show()
            } catch (error: IOException) {
                showCsvError(error.message ?: "No se pudo guardar el archivo.")
            } catch (error: SecurityException) {
                showCsvError(error.message ?: "No hay permiso para guardar el archivo.")
            } catch (error: SQLException) {
                showCsvError(error.message ?: "No se pudieron leer las reglas para exportarlas.")
            }
        }
    }

    private fun showCsvError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("No se pudo procesar el CSV")
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun requestCallScreeningRole() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(
                this,
                "El sistema requiere Android 10 o posterior para conceder este rol.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            Toast.makeText(this, "El filtro de llamadas ya está activado.", Toast.LENGTH_SHORT).show()
        } else {
            roleRequestLauncher.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING))
        }
    }
}
