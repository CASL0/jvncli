package com.github.casl0.jvncli.tui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.jakewharton.mosaic.LocalTerminalState
import com.jakewharton.mosaic.layout.ContentDrawScope
import com.jakewharton.mosaic.layout.DrawModifier
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Color

/**
 * コンテンツ全体を罫線で囲む枠を描く。内側に padding(1) を確保し、その内側に子を描画する。
 *
 * Mosaic にはボーダー用のモディファイアが無いため、罫線文字を [DrawModifier] で自前描画する。
 */
@Stable internal fun Modifier.border(): Modifier = this.then(BorderModifier).padding(all = 1)

/**
 * [border] の内側でテキストを描画できる最大表示幅。端末幅から左右の枠 1 桁ずつを引いた値を返す。
 *
 * Mosaic はクリップも折り返しもしないため、各テキストはこの幅で [ellipsize] してから描画する。
 */
@Composable
internal fun contentWidth(): Int = (LocalTerminalState.current.size.columns - 2).coerceAtLeast(0)

/**
 * [border] の内側でコンテンツを描画できる行数。端末の行数から上下の枠 1 行ずつと、末尾のカーソル行 1 行を引いた値を返す([App] がルート高さを `rows - 1`
 * にしているため)。
 */
@Composable
internal fun contentHeight(): Int = (LocalTerminalState.current.size.rows - 3).coerceAtLeast(1)

private object BorderModifier : DrawModifier {
    override fun ContentDrawScope.draw() {
        val inner = (width - 2).coerceAtLeast(0)
        drawText(string = "┌", row = 0, column = 0, foreground = Color.Unspecified)
        drawText(string = "┐", row = 0, column = width - 1, foreground = Color.Unspecified)
        drawText(string = "└", row = height - 1, column = 0, foreground = Color.Unspecified)
        drawText(string = "┘", row = height - 1, column = width - 1, foreground = Color.Unspecified)
        drawText(string = "─".repeat(inner), row = 0, column = 1, foreground = Color.Unspecified)
        drawText(
            string = "─".repeat(inner),
            row = height - 1,
            column = 1,
            foreground = Color.Unspecified,
        )
        for (row in 1..height - 2) {
            drawText(string = "│", row = row, column = 0, foreground = Color.Unspecified)
            drawText(string = "│", row = row, column = width - 1, foreground = Color.Unspecified)
        }
        drawContent()
    }

    override fun toString() = "border"
}
