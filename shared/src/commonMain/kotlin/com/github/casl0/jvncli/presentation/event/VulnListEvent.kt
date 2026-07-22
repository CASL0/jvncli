package com.github.casl0.jvncli.presentation.event

/** 脆弱性情報ページの UI イベント。 */
sealed interface VulnListEvent {
    /** 再読み込み。 */
    data object Reload : VulnListEvent

    /** カーソルを上へ。 */
    data object MoveUp : VulnListEvent

    /** カーソルを下へ。 */
    data object MoveDown : VulnListEvent
}
