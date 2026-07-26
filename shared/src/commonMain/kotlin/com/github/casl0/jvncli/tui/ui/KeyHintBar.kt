package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import com.github.casl0.jvncli.tui.KEY_HINT_BAR_HEIGHT
import com.github.casl0.jvncli.tui.contentWidth
import com.github.casl0.jvncli.tui.ellipsize
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle

/** コンテンツとキーヒントを区切る罫線の文字。枠線([border])の横罫と同じものを使う。 */
private const val DIVIDER_CHAR = "─"

/**
 * キーヒント欄を構成する行を組み立てる。区切り罫線([DIVIDER_CHAR] を [width] ぶん並べた 1 行)と、[width] へ切り詰めた [hint] の 1 行を返す。
 *
 * Mosaic は折り返しもクリップもしないため、描画前にここで表示幅を [width] 以内へ整える。返す行数は常に [KEY_HINT_BAR_HEIGHT] に一致する。
 */
internal fun keyHintLines(hint: String, width: Int): List<String> {
    val inner = width.coerceAtLeast(0)
    return listOf(DIVIDER_CHAR.repeat(inner), hint.ellipsize(inner))
}

/**
 * 枠の内側・最下部へ表示する操作キーのヒント。区切り罫線を挟んで [hint] を 1 行で示す。
 *
 * 罫線・文言とも [TextStyle.Dim] にしてコンテンツと視覚的に区別する。高さは [KEY_HINT_BAR_HEIGHT]
 * 行で固定なので、呼び出し側は同じ行数を一覧や本文の予約行として差し引くこと。
 */
@Composable
internal fun KeyHintBar(hint: String) {
    val lines = keyHintLines(hint, contentWidth())
    Column { lines.forEach { Text(it, textStyle = TextStyle.Dim) } }
}
