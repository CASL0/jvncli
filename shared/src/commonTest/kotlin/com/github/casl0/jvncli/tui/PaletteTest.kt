package com.github.casl0.jvncli.tui

import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.TextStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PaletteTest {
    @Test
    fun `severityColor_段階ごとに異なる色を返す`() {
        val colored =
            listOf(
                SeverityLevel.CRITICAL,
                SeverityLevel.HIGH,
                SeverityLevel.MEDIUM,
                SeverityLevel.LOW,
            )
        val colors = colored.map { severityColor(it) }
        assertEquals(colored.size, colors.toSet().size, "同じ色を使い回している段階がある")
        assertTrue(colors.all { it != Color.Unspecified }, "色が付いていない段階がある")
    }

    @Test
    fun `severityColor_CriticalとHighを別の色で区別する`() {
        // 案C の要点。赤 1 色に寄せず、High は橙(truecolor)で分ける。
        assertEquals(Color.Red, severityColor(SeverityLevel.CRITICAL))
        assertNotEquals(Color.Red, severityColor(SeverityLevel.HIGH))
    }

    @Test
    fun `severityColor_深刻度なしは色を指定しない`() {
        assertEquals(Color.Unspecified, severityColor(SeverityLevel.NONE))
    }

    @Test
    fun `severityTextStyle_深刻度なしだけを控えめにする`() {
        assertEquals(TextStyle.Dim, severityTextStyle(SeverityLevel.NONE))
        assertEquals(TextStyle.Unspecified, severityTextStyle(SeverityLevel.CRITICAL))
        assertEquals(TextStyle.Unspecified, severityTextStyle(SeverityLevel.LOW))
    }
}
