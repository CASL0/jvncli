package com.github.casl0.jvncli.tui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.casl0.jvncli.presentation.Presenters
import com.github.casl0.jvncli.tui.navigation.Navigator
import com.github.casl0.jvncli.tui.navigation.Screen
import com.github.casl0.jvncli.tui.navigation.Tab
import com.github.casl0.jvncli.tui.ui.AlertScreen
import com.github.casl0.jvncli.tui.ui.TabBar
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text

private val TabKey = KeyEvent("Tab")
private val Enter = KeyEvent("Enter")
private val Escape = KeyEvent("Escape")

/**
 * TUI のルートコンポーザブル。[Navigator] を1つ保持し、現在画面を出し分ける。
 *
 * 画面固有キー(↑↓ など)は各 Screen の `onKeyEvent` が処理し、未処理キーはここ(App ルート)へ伝播して タブ切り替え・詳細遷移・戻るといったナビ操作になる。
 */
@Composable
internal fun App(presenters: Presenters) {
    val navigator = remember { Navigator() }
    Column(
        modifier =
            Modifier.onKeyEvent { key ->
                when (val screen = navigator.current) {
                    is Screen.Tabs ->
                        when (key) {
                            TabKey -> {
                                navigator.toggleTab()
                                true
                            }
                            Enter -> {
                                if (screen.tab == Tab.VULNS) {
                                    navigator.openDetail("JVNDB-SAMPLE")
                                    true
                                } else {
                                    false
                                }
                            }
                            else -> false
                        }
                    is Screen.VulnDetail ->
                        when (key) {
                            Escape -> {
                                navigator.back()
                                true
                            }
                            else -> false
                        }
                }
            }
    ) {
        when (val screen = navigator.current) {
            is Screen.Tabs -> {
                TabBar(screen.tab)
                when (screen.tab) {
                    Tab.ALERTS -> AlertScreen(presenters.alertList)
                    Tab.VULNS -> Text("脆弱性情報ページ (WIP) — Enter で詳細へ / Tab で警戒情報へ")
                }
            }
            is Screen.VulnDetail -> Text("脆弱性詳細ページ (WIP): ${screen.vulnId} — Esc で戻る")
        }
    }
}
