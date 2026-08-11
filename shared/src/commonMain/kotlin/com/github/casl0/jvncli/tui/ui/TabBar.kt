package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import com.github.casl0.jvncli.tui.TAB_ACTIVE_COLOR
import com.github.casl0.jvncli.tui.contentWidth
import com.github.casl0.jvncli.tui.displayWidth
import com.github.casl0.jvncli.tui.ellipsize
import com.github.casl0.jvncli.tui.navigation.Tab
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle

/** 上部のタブ表示。選択中のタブを `[...]` とシアン+太字で強調する。 */
@Composable
internal fun TabBar(selected: Tab) {
    // Row は各ラベルを横に並べるだけで幅を制限しないため、合計が枠の内側幅を超えないよう
    // 残り幅を配りながら順に切り詰める(狭い端末での枠外描画クラッシュを防ぐ)。
    var remaining = contentWidth()
    Row {
        Tab.entries.forEach { tab ->
            val label =
                when (tab) {
                    Tab.ALERTS -> "警戒情報"
                    Tab.VULNS -> "脆弱性情報"
                }
            val text = (if (tab == selected) " [$label] " else "  $label  ").ellipsize(remaining)
            remaining = (remaining - text.displayWidth()).coerceAtLeast(0)
            Text(
                text,
                color = if (tab == selected) TAB_ACTIVE_COLOR else Color.Unspecified,
                textStyle = if (tab == selected) TextStyle.Bold else TextStyle.Empty,
            )
        }
    }
}
