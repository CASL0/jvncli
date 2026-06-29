package com.github.casl0.jvncli.tui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 画面遷移を司る状態保持クラス。ルーティングライブラリは使わず、現在画面を [current] で表す。
 *
 * 詳細画面から戻るときは直前のタブへ復帰する。
 */
internal class Navigator(initial: Screen = Screen.Tabs(Tab.ALERTS)) {
    var current by mutableStateOf(initial)
        private set

    private var lastTab: Tab = (initial as? Screen.Tabs)?.tab ?: Tab.ALERTS

    fun switchTab(tab: Tab) {
        lastTab = tab
        current = Screen.Tabs(tab)
    }

    fun toggleTab() = switchTab(if (lastTab == Tab.ALERTS) Tab.VULNS else Tab.ALERTS)

    fun openDetail(vulnId: String) {
        current = Screen.VulnDetail(vulnId)
    }

    /** 詳細画面から直前のタブへ戻る。 */
    fun back() {
        if (current is Screen.VulnDetail) current = Screen.Tabs(lastTab)
    }
}
