package com.github.casl0.jvncli.tui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.casl0.jvncli.presentation.Presenters
import com.github.casl0.jvncli.tui.navigation.Navigator
import com.github.casl0.jvncli.tui.navigation.Screen
import com.github.casl0.jvncli.tui.navigation.Tab
import com.github.casl0.jvncli.tui.ui.AlertScreen
import com.github.casl0.jvncli.tui.ui.TabBar
import com.github.casl0.jvncli.tui.ui.VulnDetailScreen
import com.github.casl0.jvncli.tui.ui.VulnListScreen
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Column

private val TabKey = KeyEvent("Tab")
private val Escape = KeyEvent("Escape")

/**
 * TUI のルートコンポーザブル。[Navigator] を1つ保持し、現在画面を出し分ける。
 *
 * 画面固有キー(↑↓ や Enter)は各 Screen の `onKeyEvent` が処理し、未処理キーはここ(App ルート)へ伝播して
 * タブ切り替え(Tab)・詳細から戻る(Esc)といったナビ操作になる。
 */
@Composable
internal fun App(presenters: Presenters) {
    val navigator = remember { Navigator() }
    Column(
        modifier =
            Modifier.onKeyEvent { key ->
                when (navigator.current) {
                    is Screen.Tabs ->
                        if (key == TabKey) {
                            navigator.toggleTab()
                            true
                        } else {
                            false
                        }
                    is Screen.VulnDetail ->
                        if (key == Escape) {
                            navigator.back()
                            true
                        } else {
                            false
                        }
                }
            }
    ) {
        when (val screen = navigator.current) {
            is Screen.Tabs -> {
                TabBar(screen.tab)
                when (screen.tab) {
                    Tab.ALERTS -> AlertScreen(presenters.alertList)
                    Tab.VULNS -> VulnListScreen(presenters.vulnList, navigator)
                }
            }
            is Screen.VulnDetail -> {
                val presenter =
                    remember(screen.vulnId) { presenters.vulnDetailFactory(screen.vulnId) }
                VulnDetailScreen(presenter)
            }
        }
    }
}
