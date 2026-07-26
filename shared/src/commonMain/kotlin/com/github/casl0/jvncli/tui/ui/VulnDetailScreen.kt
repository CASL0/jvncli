package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.casl0.jvncli.presentation.event.VulnDetailEvent
import com.github.casl0.jvncli.presentation.presenter.VulnDetailPresenter
import com.github.casl0.jvncli.presentation.state.LoadPhase
import com.github.casl0.jvncli.presentation.state.VulnDetailUiState
import com.github.casl0.jvncli.tui.KEY_HINT_BAR_HEIGHT
import com.github.casl0.jvncli.tui.SCROLL_INDICATOR_HEIGHT
import com.github.casl0.jvncli.tui.contentHeight
import com.github.casl0.jvncli.tui.contentWidth
import com.github.casl0.jvncli.tui.ellipsize
import com.github.casl0.jvncli.tui.wrapToWidth
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle

private val ArrowUp = KeyEvent("ArrowUp")
private val ArrowDown = KeyEvent("ArrowDown")
private val ReloadKey = KeyEvent("r")

/** この画面で使えるキーの説明。戻る(Esc)の実処理は親(App)だが、利用者から見て使えるキーなので含める。 */
internal const val VULN_DETAIL_KEY_HINT = "[↑↓] スクロール  [r] 再読込  [Esc] 戻る"

/** 枠内へ描画する 1 行。表示幅は [contentWidth] 以内に整形済みであること。 */
private data class DetailLine(val text: String, val style: TextStyle = TextStyle.Empty)

/**
 * 脆弱性詳細を描画する。r で再読み込み、↑↓ で本文をスクロール。戻る(Esc)は App ルートが処理する。
 *
 * Mosaic は折り返しもクリップもしないため、内容は表示幅([contentWidth])で折り返した行の並びに変換し、
 * 端末の高さ([contentHeight])に収まるぶんだけを窓化して描画する。キーヒント([KeyHintBar])は常に下部へ残す。
 */
@Composable
internal fun VulnDetailScreen(presenter: VulnDetailPresenter) {
    val state by presenter.uiState.collectAsState()
    val width = contentWidth()
    val height = contentHeight()

    val lines = remember(state, width) { buildLines(state, width) }
    // キーヒント(区切り罫線 + 文言)の 2 行を常に確保し、残りを本文の表示枠にする。
    val bodyBudget = (height - KEY_HINT_BAR_HEIGHT).coerceAtLeast(1)
    val scrollable = lines.size > bodyBudget
    // スクロール時は位置インジケータ 1 行ぶんを確保する。
    val visible =
        if (scrollable) (bodyBudget - SCROLL_INDICATOR_HEIGHT).coerceAtLeast(1) else bodyBudget
    val maxOffset = (lines.size - visible).coerceAtLeast(0)

    var offset by remember(lines) { mutableStateOf(0) }
    val start = offset.coerceIn(0, maxOffset)
    val end = (start + visible).coerceAtMost(lines.size)

    Column(
        modifier =
            Modifier.onKeyEvent { key ->
                when (key) {
                    ReloadKey -> {
                        presenter.onEvent(VulnDetailEvent.Reload)
                        true
                    }
                    ArrowUp -> {
                        offset = (start - 1).coerceAtLeast(0)
                        true
                    }
                    ArrowDown -> {
                        offset = (start + 1).coerceAtMost(maxOffset)
                        true
                    }
                    else -> false
                }
            }
    ) {
        for (i in start until end) {
            Text(lines[i].text, textStyle = lines[i].style)
        }
        if (scrollable) {
            Text("($end/${lines.size})  ↑↓ でスクロール".ellipsize(width), textStyle = TextStyle.Dim)
        }
        KeyHintBar(VULN_DETAIL_KEY_HINT)
    }
}

/** 詳細の状態を、各行が [width] 以内に収まる [DetailLine] の並びへ展開する。 */
private fun buildLines(state: VulnDetailUiState, width: Int): List<DetailLine> {
    val lines = mutableListOf<DetailLine>()
    fun add(text: String, style: TextStyle = TextStyle.Empty) =
        text.wrapToWidth(width).forEach { lines.add(DetailLine(it, style)) }
    fun heading(text: String) {
        lines.add(DetailLine(""))
        add(text, TextStyle.Bold)
    }

    when (state.phase) {
        LoadPhase.Loading -> add("読み込み中…")
        LoadPhase.Error -> add(state.error ?: "エラーが発生しました")
        LoadPhase.Loaded -> {
            val detail = state.detail
            if (detail == null) {
                add("詳細が見つかりませんでした")
            } else {
                add(detail.title ?: "(タイトルなし)", TextStyle.Bold)
                detail.id?.let { add("JVNDB: $it") }
                detail.overview?.let {
                    heading("概要")
                    add(it)
                }
                if (detail.cvssScores.isNotEmpty()) {
                    heading("CVSS")
                    detail.cvssScores.forEach { cvss ->
                        val version = cvss.version?.let { "v$it " } ?: ""
                        val severity = cvss.severity ?: "-"
                        val score = cvss.score?.toString() ?: "-"
                        add("- $version$severity ($score)")
                    }
                }
                if (detail.affected.isNotEmpty()) {
                    heading("影響を受ける製品")
                    detail.affected.forEach { product ->
                        val vendor = product.vendor?.let { "$it / " } ?: ""
                        add("- $vendor${product.productName ?: ""}")
                    }
                }
                if (detail.solutions.isNotEmpty()) {
                    heading("対策")
                    detail.solutions.forEach { add("- $it") }
                }
            }
        }
    }
    return lines
}
