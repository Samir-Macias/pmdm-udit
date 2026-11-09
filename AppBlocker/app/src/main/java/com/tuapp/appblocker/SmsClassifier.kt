package com.tuapp.appblocker

import com.tuapp.appblocker.data.BlockedNumber
import com.tuapp.appblocker.data.WhitelistNumber

data class SmsDecision(
    val suspicious: Boolean,
    val reason: String
)

object SmsClassifier {
    private val suspiciousTerms = listOf(
        "verifica tu cuenta",
        "verificar cuenta",
        "premio",
        "has ganado",
        "ganador",
        "urgente",
        "pin",
        "contraseña",
        "contrasena",
        "código de seguridad",
        "codigo de seguridad",
        "paquete retenido",
        "envio pendiente",
        "envío pendiente",
        "actualiza tus datos"
    )

    fun classify(
        sender: String,
        body: String,
        whitelist: List<WhitelistNumber>,
        blockedNumbers: List<BlockedNumber>
    ): SmsDecision {
        val senderDecision = RuleEngine.evaluate(sender, whitelist, blockedNumbers)
        if (senderDecision.reason == "En lista blanca") {
            return SmsDecision(false, "Remitente en lista blanca")
        }
        if (senderDecision.blocked) {
            return SmsDecision(true, "Remitente: ${senderDecision.reason}")
        }

        val normalizedBody = body.lowercase()
        val matchedTerms = suspiciousTerms.filter(normalizedBody::contains)
        val hasLink = Regex("""(?:https?://|www\.)\S+""", RegexOption.IGNORE_CASE)
            .containsMatchIn(body)
        val score = matchedTerms.size + if (hasLink) 1 else 0

        return if (score >= 2) {
            val signals = buildList {
                addAll(matchedTerms.distinct())
                if (hasLink) add("enlace")
            }
            SmsDecision(true, "Señales detectadas: ${signals.joinToString(", ")}")
        } else {
            SmsDecision(false, "Sin señales sospechosas suficientes")
        }
    }
}
