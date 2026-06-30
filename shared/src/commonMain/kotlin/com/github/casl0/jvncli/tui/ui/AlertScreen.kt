package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.casl0.jvncli.presentation.event.AlertEvent
import com.github.casl0.jvncli.presentation.presenter.AlertListPresenter
import com.github.casl0.jvncli.presentation.state.LoadPhase
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text

private val ArrowUp = KeyEvent("ArrowUp")
private val ArrowDown = KeyEvent("ArrowDown")
private val ReloadKey = KeyEvent("r")

/** 警戒情報の一覧を描画する。自画面のキー(↑↓ でカーソル移動・r で再読み込み)を処理し、 未処理キーは false を返して親(App)へ伝播させる。 */
@Composable
internal fun AlertScreen(presenter: AlertListPresenter) {
    val state by presenter.uiState.collectAsState()
    Column(
        modifier =
            Modifier.onKeyEvent { key ->
                when (key) {
                    ArrowUp -> {
                        presenter.onEvent(AlertEvent.MoveUp)
                        true
                    }
                    ArrowDown -> {
                        presenter.onEvent(AlertEvent.MoveDown)
                        true
                    }
                    ReloadKey -> {
                        presenter.onEvent(AlertEvent.Reload)
                        true
                    }
                    else -> false
                }
            }
    ) {
        when (state.phase) {
            LoadPhase.Loading -> Text("読み込み中…")
            LoadPhase.Error -> Text(state.error ?: "エラーが発生しました")
            LoadPhase.Loaded ->
                if (state.alerts.isEmpty()) {
                    Text("該当する警戒情報はありません")
                } else {
                    ScrollableList(
                        items = state.alerts,
                        cursor = state.cursor,
                        reservedRows = 4, // 枠(上下2) + カーソル行(1) + タブバー(1)
                    ) { alert, selected ->
                        val marker = if (selected) "› " else "  "
                        val severity = alert.severityLabel?.let { "[$it] " } ?: ""
                        Text("$marker$severity${alert.title}")
                    }
                }
        }
    }
}
