package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.casl0.jvncli.presentation.event.VulnListEvent
import com.github.casl0.jvncli.presentation.presenter.VulnListPresenter
import com.github.casl0.jvncli.presentation.state.LoadPhase
import com.github.casl0.jvncli.tui.BORDER_SIZE
import com.github.casl0.jvncli.tui.CURSOR_ROW_HEIGHT
import com.github.casl0.jvncli.tui.TAB_BAR_HEIGHT
import com.github.casl0.jvncli.tui.contentWidth
import com.github.casl0.jvncli.tui.ellipsize
import com.github.casl0.jvncli.tui.navigation.Navigator
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text

private val ArrowUp = KeyEvent("ArrowUp")
private val ArrowDown = KeyEvent("ArrowDown")
private val Enter = KeyEvent("Enter")
private val ReloadKey = KeyEvent("r")

/** 脆弱性概要の一覧を描画する。↑↓ でカーソル移動・r で再読み込み・Enter で選択中の項目の詳細へ遷移する。 未処理キーは false を返して親(App)へ伝播させる。 */
@Composable
internal fun VulnListScreen(presenter: VulnListPresenter, navigator: Navigator) {
    val state by presenter.uiState.collectAsState()
    val width = contentWidth()
    Column(
        modifier =
            Modifier.onKeyEvent { key ->
                when (key) {
                    ArrowUp -> {
                        presenter.onEvent(VulnListEvent.MoveUp)
                        true
                    }
                    ArrowDown -> {
                        presenter.onEvent(VulnListEvent.MoveDown)
                        true
                    }
                    ReloadKey -> {
                        presenter.onEvent(VulnListEvent.Reload)
                        true
                    }
                    Enter -> {
                        val id = state.currentId
                        if (id != null) {
                            navigator.openDetail(id)
                            true
                        } else {
                            false
                        }
                    }
                    else -> false
                }
            }
    ) {
        when (state.phase) {
            LoadPhase.Loading -> Text("読み込み中…")
            LoadPhase.Error -> Text((state.error ?: "エラーが発生しました").ellipsize(width))
            LoadPhase.Loaded ->
                if (state.items.isEmpty()) {
                    Text("該当する脆弱性情報はありません")
                } else {
                    ScrollableList(
                        items = state.items,
                        cursor = state.cursor,
                        reservedRows = BORDER_SIZE * 2 + CURSOR_ROW_HEIGHT + TAB_BAR_HEIGHT,
                    ) { item, selected ->
                        val marker = if (selected) "› " else "  "
                        val id = item.id?.let { "$it " } ?: ""
                        Text("$marker$id${item.title}".ellipsize(width))
                    }
                }
        }
    }
}
