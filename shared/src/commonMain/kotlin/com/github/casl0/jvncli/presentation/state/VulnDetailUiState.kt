package com.github.casl0.jvncli.presentation.state

import com.github.casl0.jvncli.core.model.VulnDetail

/**
 * 脆弱性詳細ページの UI 状態。
 *
 * @property detail 取得した詳細(Loaded でも該当なしのとき null)
 */
data class VulnDetailUiState(val phase: LoadPhase, val detail: VulnDetail?, val error: String?)
