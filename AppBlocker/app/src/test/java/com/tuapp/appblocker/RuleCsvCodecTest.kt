package com.tuapp.appblocker

import com.tuapp.appblocker.data.BlockedNumber
import com.tuapp.appblocker.data.WhitelistNumber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class RuleCsvCodecTest {
    @Test
    fun exportedRulesCanBeImportedAgain() {
        val csv = RuleCsvCodec.encode(
            blockedNumbers = listOf(
                BlockedNumber(
                    prefix = "400",
                    isActive = false,
                    priority = 12,
                    matchType = "EXACT"
                )
            ),
            whitelist = listOf(WhitelistNumber(prefix = "+34 600"))
        )

        val entries = RuleCsvCodec.parse(csv)

        assertEquals(2, entries.size)
        assertEquals(RuleCsvEntry.Type.BLOCK, entries[0].type)
        assertEquals("400", entries[0].prefix)
        assertFalse(entries[0].active)
        assertEquals(12, entries[0].priority)
        assertEquals("EXACT", entries[0].matchType)
        assertEquals(RuleCsvEntry.Type.WHITELIST, entries[1].type)
        assertEquals("+34 600", entries[1].prefix)
        assertTrue(entries[1].active)
        assertEquals("PREFIX", entries[1].matchType)
    }

    @Test
    fun parsesQuotedCommasAndEscapedQuotes() {
        val entries = RuleCsvCodec.parse(
            "type,prefix,active,priority\r\nBLOCK,\"400,\"\"test\"\"\",true,4\r\n"
        )

        assertEquals("400,\"test\"", entries.single().prefix)
        assertEquals("PREFIX", entries.single().matchType)
    }

    @Test
    fun rejectsMalformedRows() {
        assertThrows(IllegalArgumentException::class.java) {
            RuleCsvCodec.parse("type,prefix,active,priority\nBLOCK,400,true,nope")
        }
    }

    @Test
    fun acceptsHeaderOnlyCsvToImportEmptyLists() {
        assertTrue(RuleCsvCodec.parse("type,prefix,active,priority\r\n").isEmpty())
    }

    @Test
    fun parsesNewMatchTypeColumn() {
        val entry = RuleCsvCodec.parse(
            "type,prefix,active,priority,match_type\nBLOCK,600123,true,7,EXACT"
        ).single()

        assertEquals("EXACT", entry.matchType)
    }
}
