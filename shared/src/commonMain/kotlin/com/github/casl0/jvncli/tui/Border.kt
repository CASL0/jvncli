package com.github.casl0.jvncli.tui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.jakewharton.mosaic.LocalTerminalState
import com.jakewharton.mosaic.layout.ContentDrawScope
import com.jakewharton.mosaic.layout.DrawModifier
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Color

/** 枠線 1 辺の太さ(桁数・行数)。左右・上下それぞれに 1 辺ずつ確保する。 */
internal const val BORDER_SIZE = 1

/**
 * 末尾に常に確保するカーソル行の高さ。[App] がルート高さを `rows - CURSOR_ROW_HEIGHT` にしているぶん、[contentHeight] でも同じだけ差し引く。
 */
internal const val CURSOR_ROW_HEIGHT = 1

/** タブバー(TabBar)が占める高さ。 */
internal const val TAB_BAR_HEIGHT = 1

/** スクロール時に下部へ表示する位置インジケータの高さ。 */
internal const val SCROLL_INDICATOR_HEIGHT = 1

/** 画面下部のキーヒント(KeyHintBar)が占める高さ(区切り罫線 1 行 + ヒント文言 1 行)。 */
internal const val KEY_HINT_BAR_HEIGHT = 2

/**
 * コンテンツ全体を罫線で囲む枠を描く。内側に枠線 1 辺ぶんの padding([BORDER_SIZE])を確保し、その内側に子を描画する。
 *
 * Mosaic にはボーダー用のモディファイアが無いため、罫線文字を [DrawModifier] で自前描画する。
 */
@Stable
internal fun Modifier.border(): Modifier = this.then(BorderModifier).padding(all = BORDER_SIZE)

/**
 * [border] の内側でテキストを描画できる最大表示幅。端末幅から左右の枠([BORDER_SIZE])ぶんを引いた値を返す。
 *
 * Mosaic はクリップも折り返しもしないため、各テキストはこの幅で [ellipsize] してから描画する。
 */
@Composable
internal fun contentWidth(): Int =
    (LocalTerminalState.current.size.columns - BORDER_SIZE * 2).coerceAtLeast(0)

/**
 * [border]
 * の内側でコンテンツを描画できる行数。端末の行数から上下の枠([BORDER_SIZE])ぶんと、末尾のカーソル行([CURSOR_ROW_HEIGHT])を引いた値を返す([App]
 * がルート高さを `rows - CURSOR_ROW_HEIGHT` にしているため)。
 */
@Composable
internal fun contentHeight(): Int =
    (LocalTerminalState.current.size.rows - BORDER_SIZE * 2 - CURSOR_ROW_HEIGHT).coerceAtLeast(1)

/**
 * キーヒント([KEY_HINT_BAR_HEIGHT])を除いた、本文や一覧に使える行数。[aboveRows] にはタブバーなど、この画面より上で 消費する行数を渡す。
 *
 * 端末を広げても内容が短いままだとキーヒントが枠の途中に浮くため、呼び出し側はこの高さを固定枠として確保し、余りを 埋めたうえで最下部にキーヒントを置く。
 */
@Composable
internal fun bodyHeight(aboveRows: Int = 0): Int =
    (contentHeight() - aboveRows - KEY_HINT_BAR_HEIGHT).coerceAtLeast(1)

private object BorderModifier : DrawModifier {
    override fun ContentDrawScope.draw() {
        // 初期フレーム等でサイズが小さいときに枠自身が範囲外へ描画して落ちないようガードする
        // (枠は左右・上下それぞれ [BORDER_SIZE] ぶん、最低 BORDER_SIZE * 2 四方が無いと描けない)。
        if (width >= BORDER_SIZE * 2 && height >= BORDER_SIZE * 2) {
            val inner = width - BORDER_SIZE * 2
            drawText(string = "┌", row = 0, column = 0, foreground = Color.Unspecified)
            drawText(
                string = "┐",
                row = 0,
                column = width - BORDER_SIZE,
                foreground = Color.Unspecified,
            )
            drawText(
                string = "└",
                row = height - BORDER_SIZE,
                column = 0,
                foreground = Color.Unspecified,
            )
            drawText(
                string = "┘",
                row = height - BORDER_SIZE,
                column = width - BORDER_SIZE,
                foreground = Color.Unspecified,
            )
            drawText(
                string = "─".repeat(inner),
                row = 0,
                column = BORDER_SIZE,
                foreground = Color.Unspecified,
            )
            drawText(
                string = "─".repeat(inner),
                row = height - BORDER_SIZE,
                column = BORDER_SIZE,
                foreground = Color.Unspecified,
            )
            for (row in BORDER_SIZE until height - BORDER_SIZE) {
                drawText(string = "│", row = row, column = 0, foreground = Color.Unspecified)
                drawText(
                    string = "│",
                    row = row,
                    column = width - BORDER_SIZE,
                    foreground = Color.Unspecified,
                )
            }
        }
        drawContent()
    }

    override fun toString() = "border"
}
