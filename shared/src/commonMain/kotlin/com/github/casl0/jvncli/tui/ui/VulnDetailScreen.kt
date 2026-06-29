package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.casl0.jvncli.core.model.VulnDetail
import com.github.casl0.jvncli.presentation.event.VulnDetailEvent
import com.github.casl0.jvncli.presentation.presenter.VulnDetailPresenter
import com.github.casl0.jvncli.presentation.state.LoadPhase
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle

private val ReloadKey = KeyEvent("r")

/** 脆弱性詳細を描画する。r で再読み込み。戻る(Esc)は App ルートが処理する。 */
@Composable
internal fun VulnDetailScreen(presenter: VulnDetailPresenter) {
    val state by presenter.uiState.collectAsState()
    Column(
        modifier =
            Modifier.onKeyEvent { key ->
                if (key == ReloadKey) {
                    presenter.onEvent(VulnDetailEvent.Reload)
                    true
                } else {
                    false
                }
            }
    ) {
        when (state.phase) {
            LoadPhase.Loading -> Text("読み込み中…")
            LoadPhase.Error -> Text(state.error ?: "エラーが発生しました")
            LoadPhase.Loaded -> {
                val detail = state.detail
                if (detail == null) {
                    Text("詳細が見つかりませんでした")
                } else {
                    DetailBody(detail)
                }
            }
        }
        Text("")
        Text("[Esc] 一覧へ戻る  [r] 再読み込み")
    }
}

@Composable
private fun DetailBody(detail: VulnDetail) {
    Text(detail.title ?: "(タイトルなし)", textStyle = TextStyle.Bold)
    detail.id?.let { Text("JVNDB: $it") }

    detail.overview?.let {
        Text("")
        Text("概要", textStyle = TextStyle.Bold)
        Text(it)
    }

    if (detail.cvssScores.isNotEmpty()) {
        Text("")
        Text("CVSS", textStyle = TextStyle.Bold)
        detail.cvssScores.forEach { cvss ->
            val version = cvss.version?.let { "v$it " } ?: ""
            val severity = cvss.severity ?: "-"
            val score = cvss.score?.toString() ?: "-"
            Text("- $version$severity ($score)")
        }
    }

    if (detail.affected.isNotEmpty()) {
        Text("")
        Text("影響を受ける製品", textStyle = TextStyle.Bold)
        detail.affected.forEach { product ->
            val vendor = product.vendor?.let { "$it / " } ?: ""
            Text("- $vendor${product.productName ?: ""}")
        }
    }

    if (detail.solutions.isNotEmpty()) {
        Text("")
        Text("対策", textStyle = TextStyle.Bold)
        detail.solutions.forEach { Text("- $it") }
    }
}
