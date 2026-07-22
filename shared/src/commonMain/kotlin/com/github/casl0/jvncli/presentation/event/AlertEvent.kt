package com.github.casl0.jvncli.presentation.event

/** 警戒情報ページの UI イベント。 */
sealed interface AlertEvent {
    /** 再読み込み。 */
    data object Reload : AlertEvent

    /** カーソルを上へ。 */
    data object MoveUp : AlertEvent

    /** カーソルを下へ。 */
    data object MoveDown : AlertEvent
}
