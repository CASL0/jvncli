package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import com.github.casl0.jvncli.tui.navigation.Tab
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle

/** 上部のタブ表示。選択中のタブを `[...]` と太字で強調する。 */
@Composable
internal fun TabBar(selected: Tab) {
    Row {
        Tab.entries.forEach { tab ->
            val label =
                when (tab) {
                    Tab.ALERTS -> "警戒情報"
                    Tab.VULNS -> "脆弱性情報"
                }
            if (tab == selected) {
                Text(" [$label] ", textStyle = TextStyle.Bold)
            } else {
                Text("  $label  ")
            }
        }
    }
}
