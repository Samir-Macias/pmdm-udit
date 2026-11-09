package com.tuapp.appblocker

import com.tuapp.appblocker.data.BlockedNumber
import com.tuapp.appblocker.data.WhitelistNumber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class RuleEngineTest {
    @Test
    fun whitelistOverridesMatchingBlockRule() {
        val decision = RuleEngine.evaluate(
            "+34 600 123 456",
            whitelist = listOf(WhitelistNumber(prefix = "600")),
            blockedNumbers = listOf(BlockedNumber(prefix = "600", priority = 100))
        )

        assertFalse(decision.blocked)
        assertEquals("En lista blanca", decision.reason)
    }

    @Test
    fun highestPriorityMatchingRuleWins() {
        val lowerPriority = BlockedNumber(id = 1, prefix = "600", priority = 2)
        val higherPriority = BlockedNumber(id = 2, prefix = "6001", priority = 8)

        val decision = RuleEngine.evaluate(
            "600123456",
            whitelist = emptyList(),
            blockedNumbers = listOf(lowerPriority, higherPriority)
        )

        assertTrue(decision.blocked)
        assertSame(higherPriority, decision.matchedRule)
        assertTrue(decision.reason.contains("prioridad 8"))
    }

    @Test
    fun longerPrefixBreaksPriorityTies() {
        val broadRule = BlockedNumber(id = 1, prefix = "600", priority = 5)
        val specificRule = BlockedNumber(id = 2, prefix = "60012", priority = 5)

        val decision = RuleEngine.evaluate(
            "600123456",
            whitelist = emptyList(),
            blockedNumbers = listOf(broadRule, specificRule)
        )

        assertSame(specificRule, decision.matchedRule)
    }

    @Test
    fun inactiveRulesAreIgnoredAndSpanishInternationalPrefixIsNormalized() {
        val decision = RuleEngine.evaluate(
            "0034 400 123 456",
            whitelist = emptyList(),
            blockedNumbers = listOf(
                BlockedNumber(prefix = "400", isActive = false),
                BlockedNumber(prefix = "+34 400", priority = 1)
            )
        )

        assertTrue(decision.blocked)
        assertEquals("+34 400", decision.matchedRule?.prefix)
    }

    @Test
    fun exactRuleDoesNotBlockNumbersThatOnlyShareItsPrefix() {
        val decision = RuleEngine.evaluate(
            "6001234567",
            whitelist = emptyList(),
            blockedNumbers = listOf(
                BlockedNumber(prefix = "600123456", matchType = "EXACT")
            )
        )

        assertFalse(decision.blocked)
    }

    @Test
    fun exactRuleMatchesNormalizedInternationalNumber() {
        val exactRule = BlockedNumber(prefix = "+34 600 123 456", matchType = "EXACT")
        val decision = RuleEngine.evaluate(
            "0034 600-123-456",
            whitelist = emptyList(),
            blockedNumbers = listOf(exactRule)
        )

        assertTrue(decision.blocked)
        assertSame(exactRule, decision.matchedRule)
    }
}
