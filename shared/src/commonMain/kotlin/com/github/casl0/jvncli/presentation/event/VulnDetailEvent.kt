package com.github.casl0.jvncli.presentation.event

/** 脆弱性詳細ページの UI イベント。 */
sealed interface VulnDetailEvent {
    /** 再読み込み。 */
    data object Reload : VulnDetailEvent
}
