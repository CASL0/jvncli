package com.github.casl0.jvncli.tui

import kotlin.test.Test
import kotlin.test.assertEquals

class TextWidthTest {
    @Test
    fun `displayWidth_半角は1桁ずつ数える`() {
        assertEquals(5, "hello".displayWidth())
    }

    @Test
    fun `displayWidth_全角は2桁ずつ数える`() {
        assertEquals(6, "あいう".displayWidth())
    }

    @Test
    fun `displayWidth_半角と全角の混在を合算する`() {
        assertEquals(6, "ab漢字".displayWidth())
    }

    @Test
    fun `ellipsize_収まる場合はそのまま返す`() {
        assertEquals("hello", "hello".ellipsize(10))
    }

    @Test
    fun `ellipsize_半角文字列を省略記号付きで切り詰める`() {
        assertEquals("he…", "hello".ellipsize(3))
    }

    @Test
    fun `ellipsize_全角文字列を省略記号付きで切り詰める`() {
        assertEquals("あい…", "あいうえお".ellipsize(5))
    }

    @Test
    fun `ellipsize_全角の境界で表示幅を超えない`() {
        // 上限 4・省略記号 1 桁 → 残り 3 桁。全角 2 桁を 1 つだけ入れて "あ…"(幅 3)に収める。
        assertEquals("あ…", "あいうえお".ellipsize(4))
    }

    @Test
    fun `ellipsize_上限が0以下なら空文字を返す`() {
        assertEquals("", "hello".ellipsize(0))
    }

    @Test
    fun `ellipsize_省略記号すら収まらないなら空文字を返す`() {
        assertEquals("", "hello".ellipsize(1, ellipsis = "。"))
    }
}
