package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.casl0.jvncli.presentation.event.VulnListEvent
import com.github.casl0.jvncli.presentation.presenter.VulnListPresenter
import com.github.casl0.jvncli.presentation.state.LoadPhase
import com.github.casl0.jvncli.tui.BORDER_SIZE
import com.github.casl0.jvncli.tui.CURSOR_ROW_HEIGHT
import com.github.casl0.jvncli.tui.ERROR_COLOR
import com.github.casl0.jvncli.tui.KEY_HINT_BAR_HEIGHT
import com.github.casl0.jvncli.tui.SeverityLevel
import com.github.casl0.jvncli.tui.TAB_BAR_HEIGHT
import com.github.casl0.jvncli.tui.bodyHeight
import com.github.casl0.jvncli.tui.contentWidth
import com.github.casl0.jvncli.tui.ellipsize
import com.github.casl0.jvncli.tui.highestScored
import com.github.casl0.jvncli.tui.navigation.Navigator
import com.github.casl0.jvncli.tui.severityBadge
import com.github.casl0.jvncli.tui.severityLevel
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.height
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle

private val ArrowUp = KeyEvent("ArrowUp")
private val ArrowDown = KeyEvent("ArrowDown")
private val Enter = KeyEvent("Enter")
private val ReloadKey = KeyEvent("r")

/** この画面で使えるキーの説明。タブ切替(Tab)の実処理は親(App)だが、利用者から見て使えるキーなので含める。 */
internal const val VULN_LIST_KEY_HINT = "[↑↓] 移動  [Enter] 詳細  [r] 再読込  [Tab] タブ切替"

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
        // 一覧が短くてもキーヒントが最下部に残るよう、一覧側の高さを固定して残りを埋める。
        Column(modifier = Modifier.height(bodyHeight(TAB_BAR_HEIGHT))) {
            when (state.phase) {
                LoadPhase.Loading -> Text("読み込み中…", textStyle = TextStyle.Dim)
                LoadPhase.Error ->
                    Text((state.error ?: "エラーが発生しました").ellipsize(width), color = ERROR_COLOR)
                LoadPhase.Loaded ->
                    if (state.items.isEmpty()) {
                        Text("該当する脆弱性情報はありません", textStyle = TextStyle.Dim)
                    } else {
                        ScrollableList(
                            items = state.items,
                            cursor = state.cursor,
                            reservedRows =
                                BORDER_SIZE * 2 +
                                    CURSOR_ROW_HEIGHT +
                                    TAB_BAR_HEIGHT +
                                    KEY_HINT_BAR_HEIGHT,
                        ) { item, selected ->
                            // 複数バージョンの CVSS があるときはバッジに使う 1 件(最大スコア)へ絞る。
                            val cvss = item.cvssScores.highestScored()
                            val id = item.id?.let { "$it " } ?: ""
                            SeverityRow(
                                badge = severityBadge(cvss),
                                body = "$id${item.title}",
                                level = cvss?.severityLevel() ?: SeverityLevel.NONE,
                                selected = selected,
                                width = width,
                            )
                        }
                    }
            }
        }
        KeyHintBar(VULN_LIST_KEY_HINT)
    }
}
