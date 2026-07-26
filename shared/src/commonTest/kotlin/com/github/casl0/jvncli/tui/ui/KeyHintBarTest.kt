package com.github.casl0.jvncli.tui.ui

import com.github.casl0.jvncli.tui.KEY_HINT_BAR_HEIGHT
import com.github.casl0.jvncli.tui.displayWidth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KeyHintBarTest {
    @Test
    fun `keyHintLines_区切り罫線とヒントの2行を返す`() {
        // "[r] 再読込" は表示幅 10。同じ幅を指定すれば罫線 10 桁とヒントがそのまま並ぶ。
        assertEquals(listOf("──────────", "[r] 再読込"), keyHintLines("[r] 再読込", 10))
    }

    @Test
    fun `keyHintLines_行数はフッター高さの定数と一致する`() {
        assertEquals(KEY_HINT_BAR_HEIGHT, keyHintLines("[r] 再読込", 20).size)
    }

    @Test
    fun `keyHintLines_区切り罫線が指定幅を埋める`() {
        assertEquals(20, keyHintLines("[r] 再読込", 20)[0].displayWidth())
    }

    @Test
    fun `keyHintLines_収まるヒントはそのまま返す`() {
        assertEquals("[Esc] 戻る", keyHintLines("[Esc] 戻る", 40)[1])
    }

    @Test
    fun `keyHintLines_狭い幅ではヒントを切り詰める`() {
        // "[Esc] 戻る" は表示幅 10。上限 5・省略記号 1 桁 → 残り 4 桁ぶんだけ残す。
        assertEquals("[Esc…", keyHintLines("[Esc] 戻る", 5)[1])
    }

    @Test
    fun `keyHintLines_各行が指定幅を超えない`() {
        val hint = "[↑↓] 移動  [Enter] 詳細  [r] 再読込  [Tab] タブ切替"
        listOf(1, 3, 5, 20, 40, 80).forEach { width ->
            assertTrue(
                keyHintLines(hint, width).all { it.displayWidth() <= width },
                "width=$width で表示幅を超えた",
            )
        }
    }

    @Test
    fun `keyHintLines_幅が0以下なら空行を返す`() {
        assertEquals(listOf("", ""), keyHintLines("[r] 再読込", 0))
        assertEquals(listOf("", ""), keyHintLines("[r] 再読込", -3))
    }

    @Test
    fun `警戒情報タブは移動と再読込とタブ切替を案内する`() {
        assertEquals("[↑↓] 移動  [r] 再読込  [Tab] タブ切替", ALERT_KEY_HINT)
    }
}
