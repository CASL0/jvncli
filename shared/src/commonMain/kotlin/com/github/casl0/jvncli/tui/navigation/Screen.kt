package com.github.casl0.jvncli.tui.navigation

/** タブ画面の種別。 */
enum class Tab {
    ALERTS,
    VULNS,
}

/** TUI の現在画面。タブ画面を底に、その上へ詳細画面が乗る浅い構造で表す。 */
sealed interface Screen {
    /** 警戒情報 / 脆弱性情報のタブ画面。 */
    data class Tabs(val tab: Tab) : Screen

    /** 脆弱性詳細画面。 */
    data class VulnDetail(val vulnId: String) : Screen
}
