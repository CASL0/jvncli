package com.github.casl0.jvncli.tui

import androidx.compose.runtime.Stable
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
