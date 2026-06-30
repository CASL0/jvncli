package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.LocalTerminalState
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle

/**
 * ターミナルの行数に合わせて表示範囲をウィンドウ化する一覧。
 *
 * 端末の高さ ([LocalTerminalState]) から表示可能行数を求め、[cursor] が常に画面内に収まるよう
 * ウィンドウをずらして描画する。これにより固定サイズのビューポート内でスクロールするように見える。 画面外の要素は描画しない(可視ぶんだけ compose する)。
 *
 * @param reservedRows 一覧以外で消費する行数(タブバーやフッターなど)。
 * @param renderRow 各行の描画。第2引数はその行が選択中(カーソル位置)かどうか。
 */
@Composable
internal fun <T> ScrollableList(
    items: List<T>,
    cursor: Int,
    reservedRows: Int = 1,
    renderRow: @Composable (item: T, selected: Boolean) -> Unit,
) {
    val rows = LocalTerminalState.current.size.rows
    val budget = (rows - reservedRows).coerceAtLeast(1)
    val scrollable = items.size > budget
    // スクロール時は位置インジケータ1行ぶんを確保する。
    val visible = if (scrollable) (budget - 1).coerceAtLeast(1) else budget
    val start =
        when {
            items.size <= visible -> 0
            cursor < visible -> 0
            else -> (cursor - visible + 1).coerceAtMost(items.size - visible)
        }
    val end = (start + visible).coerceAtMost(items.size)

    Column {
        for (i in start until end) {
            renderRow(items[i], i == cursor)
        }
        if (scrollable) {
            Text("(${cursor + 1}/${items.size})", textStyle = TextStyle.Dim)
        }
    }
}
