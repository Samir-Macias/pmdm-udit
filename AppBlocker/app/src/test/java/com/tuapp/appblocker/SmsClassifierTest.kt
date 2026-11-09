package com.tuapp.appblocker

import com.tuapp.appblocker.data.BlockedNumber
import com.tuapp.appblocker.data.WhitelistNumber
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsClassifierTest {
    @Test
    fun knownBlockedSenderIsMarkedSuspicious() {
        val decision = SmsClassifier.classify(
            sender = "+34 600 123 456",
            body = "Hola, tenemos una actualización.",
            whitelist = emptyList(),
            blockedNumbers = listOf(
                BlockedNumber(prefix = "600123456", matchType = "EXACT")
            )
        )

        assertTrue(decision.suspicious)
        assertTrue(decision.reason.contains("Remitente"))
    }

    @Test
    fun whitelistPreventsSenderRuleFromFlaggingMessage() {
        val decision = SmsClassifier.classify(
            sender = "600123456",
            body = "Tu premio urgente: visita https://example.invalid",
            whitelist = listOf(WhitelistNumber(prefix = "600")),
            blockedNumbers = listOf(BlockedNumber(prefix = "600"))
        )

        assertFalse(decision.suspicious)
        assertTrue(decision.reason.contains("lista blanca"))
    }

    @Test
    fun combinesTextSignalsToClassifySuspiciousMessage() {
        val decision = SmsClassifier.classify(
            sender = "Banco",
            body = "Verifica tu cuenta ahora en https://example.invalid",
            whitelist = emptyList(),
            blockedNumbers = emptyList()
        )

        assertTrue(decision.suspicious)
        assertTrue(decision.reason.contains("enlace"))
    }

    @Test
    fun aSingleWeakSignalDoesNotFlagMessage() {
        val decision = SmsClassifier.classify(
            sender = "Banco",
            body = "Consulta la información en https://example.invalid",
            whitelist = emptyList(),
            blockedNumbers = emptyList()
        )

        assertFalse(decision.suspicious)
    }
}
