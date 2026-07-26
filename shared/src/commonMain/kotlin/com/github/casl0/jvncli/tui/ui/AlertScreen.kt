package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.casl0.jvncli.presentation.event.AlertEvent
import com.github.casl0.jvncli.presentation.presenter.AlertListPresenter
import com.github.casl0.jvncli.presentation.state.LoadPhase
import com.github.casl0.jvncli.tui.BORDER_SIZE
import com.github.casl0.jvncli.tui.CURSOR_ROW_HEIGHT
import com.github.casl0.jvncli.tui.KEY_HINT_BAR_HEIGHT
import com.github.casl0.jvncli.tui.TAB_BAR_HEIGHT
import com.github.casl0.jvncli.tui.contentWidth
import com.github.casl0.jvncli.tui.ellipsize
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text

private val ArrowUp = KeyEvent("ArrowUp")
private val ArrowDown = KeyEvent("ArrowDown")
private val ReloadKey = KeyEvent("r")

/** この画面で使えるキーの説明。タブ切替(Tab)の実処理は親(App)だが、利用者から見て使えるキーなので含める。 */
internal const val ALERT_KEY_HINT = "[↑↓] 移動  [r] 再読込  [Tab] タブ切替"

/** 警戒情報の一覧を描画する。自画面のキー(↑↓ でカーソル移動・r で再読み込み)を処理し、 未処理キーは false を返して親(App)へ伝播させる。 */
@Composable
internal fun AlertScreen(presenter: AlertListPresenter) {
    val state by presenter.uiState.collectAsState()
    val width = contentWidth()
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
            LoadPhase.Error -> Text((state.error ?: "エラーが発生しました").ellipsize(width))
            LoadPhase.Loaded ->
                if (state.alerts.isEmpty()) {
                    Text("該当する警戒情報はありません")
                } else {
                    ScrollableList(
                        items = state.alerts,
                        cursor = state.cursor,
                        reservedRows =
                            BORDER_SIZE * 2 +
                                CURSOR_ROW_HEIGHT +
                                TAB_BAR_HEIGHT +
                                KEY_HINT_BAR_HEIGHT,
                    ) { alert, selected ->
                        val marker = if (selected) "› " else "  "
                        val severity = alert.severityLabel?.let { "[$it] " } ?: ""
                        Text("$marker$severity${alert.title}".ellipsize(width))
                    }
                }
        }
        KeyHintBar(ALERT_KEY_HINT)
    }
}
