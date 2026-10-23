package com.github.casl0.jvncli.tui.ui

import com.github.casl0.jvncli.tui.displayWidth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** 3 断片の合計表示幅。枠内へ収まっているかの検証に使う。 */
private fun SeverityRowSegments.totalWidth() =
    marker.displayWidth() + badge.displayWidth() + body.displayWidth()

class SeverityRowTest {
    @Test
    fun `severityRowSegments_選択行にカーソルマーカーを出す`() {
        val segments = severityRowSegments("[9.8]", "JVNDB-2026-1 タイトル", selected = true, 40)
        assertEquals("› ", segments.marker)
    }

    @Test
    fun `severityRowSegments_非選択行はマーカーぶんの幅を空ける`() {
        val segments = severityRowSegments("[9.8]", "JVNDB-2026-1 タイトル", selected = false, 40)
        assertEquals("  ", segments.marker)
    }

    @Test
    fun `severityRowSegments_バッジは本文と1桁空けて並べる`() {
        val segments = severityRowSegments("[9.8]", "タイトル", selected = false, 40)
        assertEquals("[9.8] ", segments.badge)
        assertEquals("タイトル", segments.body)
    }

    @Test
    fun `severityRowSegments_バッジが無ければ空になる`() {
        val segments = severityRowSegments(null, "タイトル", selected = false, 40)
        assertEquals("", segments.badge)
        assertEquals("タイトル", segments.body)
    }

    @Test
    fun `severityRowSegments_選択行は残り幅を空白で埋める`() {
        // 反転(Invert)が行いっぱいのバーとして見えるよう、選択行だけ幅ちょうどまで伸ばす。
        val segments = severityRowSegments("[9.8]", "タイトル", selected = true, 30)
        assertEquals(30, segments.totalWidth())
    }

    @Test
    fun `severityRowSegments_非選択行は空白で埋めない`() {
        val segments = severityRowSegments("[9.8]", "タイトル", selected = false, 30)
        assertEquals("タイトル", segments.body)
    }

    @Test
    fun `severityRowSegments_狭いときは本文を切り詰める`() {
        // 幅 12 = マーカー 2 + バッジ "[9.8] " 6 → 本文に残るのは 4 桁。
        val segments = severityRowSegments("[9.8]", "abcdefg", selected = false, 12)
        assertEquals("abc…", segments.body)
    }

    @Test
    fun `severityRowSegments_どの幅でも枠の内側に収まる`() {
        listOf(0, 1, 2, 3, 5, 8, 12, 40, 80).forEach { width ->
            listOf(true, false).forEach { selected ->
                val segments = severityRowSegments("[10.0]", "JVNDB-2026-1 タイトル", selected, width)
                assertTrue(
                    segments.totalWidth() <= width,
                    "width=$width selected=$selected で表示幅を超えた",
                )
            }
        }
    }
}
