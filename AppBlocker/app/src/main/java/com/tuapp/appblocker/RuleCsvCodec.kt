package com.tuapp.appblocker

import com.tuapp.appblocker.data.BlockedNumber
import com.tuapp.appblocker.data.WhitelistNumber

data class RuleCsvEntry(
    val type: Type,
    val prefix: String,
    val active: Boolean,
    val priority: Int,
    val matchType: String = "PREFIX"
) {
    enum class Type { BLOCK, WHITELIST }
}

object RuleCsvCodec {
    private val header = listOf("type", "prefix", "active", "priority", "match_type")
    private val legacyHeader = header.dropLast(1)

    fun encode(blockedNumbers: List<BlockedNumber>, whitelist: List<WhitelistNumber>): String {
        val rows = buildList {
            add(header)
            blockedNumbers.forEach {
                add(
                    listOf(
                        "BLOCK",
                        it.prefix,
                        it.isActive.toString(),
                        it.priority.toString(),
                        it.matchType
                    )
                )
            }
            whitelist.forEach {
                add(listOf("WHITELIST", it.prefix, "true", "0", "PREFIX"))
            }
        }
        return rows.joinToString("\r\n") { row -> row.joinToString(",") { escape(it) } } + "\r\n"
    }

    fun parse(csv: String): List<RuleCsvEntry> {
        val rows = parseRows(csv).filterNot { row -> row.all(String::isBlank) }
        require(rows.isNotEmpty()) { "El CSV está vacío." }
        val csvHeader = rows.first().map { it.trim().lowercase() }
        val hasMatchType = csvHeader == header
        require(hasMatchType || csvHeader == legacyHeader) {
            "La cabecera debe ser: type,prefix,active,priority,match_type."
        }

        return rows.drop(1).mapIndexed { index, row ->
            val expectedColumns = if (hasMatchType) header.size else legacyHeader.size
            require(row.size == expectedColumns) {
                "La fila ${index + 2} debe tener $expectedColumns columnas."
            }
            val type = when (row[0].trim().uppercase()) {
                "BLOCK" -> RuleCsvEntry.Type.BLOCK
                "WHITELIST" -> RuleCsvEntry.Type.WHITELIST
                else -> throw IllegalArgumentException(
                    "Tipo no válido en la fila ${index + 2}: ${row[0]}."
                )
            }
            val prefix = row[1].trim()
            require(prefix.any(Char::isDigit)) {
                "El prefijo de la fila ${index + 2} no contiene dígitos."
            }
            val active = when (row[2].trim().lowercase()) {
                "true", "1", "si", "sí" -> true
                "false", "0", "no" -> false
                else -> throw IllegalArgumentException(
                    "El estado de la fila ${index + 2} debe ser true o false."
                )
            }
            val priority = row[3].trim().toIntOrNull()
                ?: throw IllegalArgumentException(
                    "La prioridad de la fila ${index + 2} debe ser un entero."
                )
            val matchType = if (hasMatchType) row[4].trim().uppercase() else "PREFIX"
            require(matchType == "PREFIX" || matchType == "EXACT") {
                "El tipo de coincidencia de la fila ${index + 2} debe ser PREFIX o EXACT."
            }
            RuleCsvEntry(type, prefix, active, priority, matchType)
        }
    }

    private fun escape(value: String): String {
        return if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun parseRows(csv: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        var row = mutableListOf<String>()
        val field = StringBuilder()
        var quoted = false
        var index = 0

        while (index < csv.length) {
            val char = csv[index]
            when {
                char == '"' && quoted && index + 1 < csv.length && csv[index + 1] == '"' -> {
                    field.append('"')
                    index++
                }
                char == '"' && field.isEmpty() && !quoted -> quoted = true
                char == '"' && quoted -> quoted = false
                char == ',' && !quoted -> {
                    row.add(field.toString())
                    field.clear()
                }
                (char == '\n' || char == '\r') && !quoted -> {
                    if (char == '\r' && index + 1 < csv.length && csv[index + 1] == '\n') {
                        index++
                    }
                    row.add(field.toString())
                    rows.add(row)
                    row = mutableListOf()
                    field.clear()
                }
                else -> field.append(char)
            }
            index++
        }

        require(!quoted) { "El CSV contiene un campo entrecomillado sin cerrar." }
        if (field.isNotEmpty() || row.isNotEmpty()) {
            row.add(field.toString())
            rows.add(row)
        }
        return rows
    }
}
