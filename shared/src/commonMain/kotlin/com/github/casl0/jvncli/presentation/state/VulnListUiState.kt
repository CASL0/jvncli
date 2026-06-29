package com.github.casl0.jvncli.presentation.state

import com.github.casl0.jvncli.core.model.VulnOverview

/**
 * 脆弱性情報ページの UI 状態。
 *
 * @property cursor 選択中の行(↑↓ で移動)
 */
data class VulnListUiState(
    val phase: LoadPhase,
    val items: List<VulnOverview>,
    val cursor: Int,
    val error: String?,
) {
    /** カーソル位置の脆弱性 ID。Enter 押下時に詳細へ遷移するために参照する。 */
    val currentId: String?
        get() = items.getOrNull(cursor)?.id
}
