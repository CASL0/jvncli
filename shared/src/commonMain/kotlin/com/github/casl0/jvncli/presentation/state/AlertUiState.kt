package com.github.casl0.jvncli.presentation.state

import com.github.casl0.jvncli.core.model.Alert

/**
 * 警戒情報ページの UI 状態。
 *
 * @property cursor 選択中の行(↑↓ で移動)
 */
data class AlertUiState(
    val phase: LoadPhase,
    val alerts: List<Alert>,
    val cursor: Int,
    val error: String?,
)
