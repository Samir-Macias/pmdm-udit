package com.tuapp.appblocker

import com.tuapp.appblocker.data.BlockedNumber
import com.tuapp.appblocker.data.WhitelistNumber

data class FilterDecision(
    val blocked: Boolean,
    val reason: String,
    val matchedRule: BlockedNumber? = null
)

object RuleEngine {
    fun evaluate(
        phoneNumber: String,
        whitelist: List<WhitelistNumber>,
        blockedNumbers: List<BlockedNumber>
    ): FilterDecision {
        if (whitelist.any { matches(phoneNumber, it.prefix) }) {
            return FilterDecision(blocked = false, reason = "En lista blanca")
        }

        val matchedRule = blockedNumbers
            .asSequence()
            .filter { it.isActive && matches(phoneNumber, it) }
            .maxWithOrNull(
                compareBy<BlockedNumber> { it.priority }
                    .thenBy { it.matchType == "EXACT" }
                    .thenBy { normalizePrefix(it.prefix).length }
                    .thenByDescending { it.id }
            )

        return if (matchedRule == null) {
            FilterDecision(blocked = false, reason = "Sin reglas de bloqueo coincidentes")
        } else {
            FilterDecision(
                blocked = true,
                reason = "${if (matchedRule.matchType == "EXACT") "Número bloqueado" else "Prefijo bloqueado"}: " +
                    "${matchedRule.prefix} (prioridad ${matchedRule.priority})",
                matchedRule = matchedRule
            )
        }
    }

    private fun matches(phoneNumber: String, rule: BlockedNumber): Boolean {
        val number = normalizePhone(phoneNumber)
        val normalizedPrefix = normalizePrefix(rule.prefix)
        if (normalizedPrefix.isEmpty()) return false
        return if (rule.matchType == "EXACT") {
            number == normalizedPrefix
        } else {
            number.startsWith(normalizedPrefix)
        }
    }

    private fun matches(phoneNumber: String, prefix: String): Boolean {
        val number = normalizePhone(phoneNumber)
        val normalizedPrefix = normalizePrefix(prefix)
        return normalizedPrefix.isNotEmpty() && number.startsWith(normalizedPrefix)
    }

    private fun normalizePhone(value: String): String {
        val digits = value.filter(Char::isDigit)
        return when {
            digits.startsWith("0034") -> digits.drop(4)
            digits.startsWith("34") && digits.length > 9 -> digits.drop(2)
            else -> digits
        }
    }

    private fun normalizePrefix(value: String): String {
        val trimmed = value.trim()
        val digits = trimmed.filter(Char::isDigit)
        return when {
            trimmed.startsWith("+34") -> digits.drop(2)
            trimmed.startsWith("0034") -> digits.drop(4)
            digits.startsWith("34") && digits.length > 9 -> digits.drop(2)
            else -> digits
        }
    }
}
