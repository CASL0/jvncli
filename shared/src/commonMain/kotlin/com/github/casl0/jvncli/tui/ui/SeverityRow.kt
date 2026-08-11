package com.github.casl0.jvncli.tui.ui

import androidx.compose.runtime.Composable
import com.github.casl0.jvncli.tui.SeverityLevel
import com.github.casl0.jvncli.tui.displayWidth
import com.github.casl0.jvncli.tui.ellipsize
import com.github.casl0.jvncli.tui.severityColor
import com.github.casl0.jvncli.tui.severityTextStyle
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle

/** 選択行の先頭に置くカーソルマーカー。反転(Invert)と併用し、色や反転が出ない端末でも位置が分かるようにする。 */
private const val CURSOR_MARKER = "› "

/** 非選択行の先頭。マーカーと同じ幅を空けて本文の桁を揃える。 */
private const val NO_CURSOR_MARKER = "  "

/** 一覧 1 行を構成する断片。3 つの表示幅の合計は [severityRowSegments] に渡した幅を超えない。 */
internal data class SeverityRowSegments(val marker: String, val badge: String, val body: String)

/**
 * 一覧 1 行を、カーソルマーカー・深刻度バッジ・本文の 3 つへ切り分ける。
 *
 * バッジだけを着色するため 1 行を複数の [Text] に分ける必要があり、Mosaic は折り返しもクリップもしないので、 先頭から順に残り幅を配って [ellipsize]
 * する。選択行は本文を残り幅ぶんの空白で埋め、反転(Invert)が行いっぱいの バーとして見えるようにする。
 */
internal fun severityRowSegments(
    badge: String?,
    body: String,
    selected: Boolean,
    width: Int,
): SeverityRowSegments {
    val marker = (if (selected) CURSOR_MARKER else NO_CURSOR_MARKER).ellipsize(width)
    val afterMarker = (width - marker.displayWidth()).coerceAtLeast(0)
    val badgeText = badge?.let { "$it ".ellipsize(afterMarker) } ?: ""
    val remaining = (afterMarker - badgeText.displayWidth()).coerceAtLeast(0)
    val bodyText = body.ellipsize(remaining)
    val padding = if (selected) " ".repeat(remaining - bodyText.displayWidth()) else ""
    return SeverityRowSegments(marker = marker, badge = badgeText, body = bodyText + padding)
}

/**
 * 深刻度バッジ付きの一覧 1 行を描画する。バッジのみ [level] の色にし、[selected] の行は全体を反転して示す。
 *
 * 表示幅は [width](枠の内側幅)へ収めてから描画する。
 */
@Composable
internal fun SeverityRow(
    badge: String?,
    body: String,
    level: SeverityLevel,
    selected: Boolean,
    width: Int,
) {
    val segments = severityRowSegments(badge, body, selected, width)
    val base = if (selected) TextStyle.Invert else TextStyle.Unspecified
    Row {
        Text(segments.marker, textStyle = base)
        if (segments.badge.isNotEmpty()) {
            Text(
                segments.badge,
                color = severityColor(level),
                textStyle = base + severityTextStyle(level),
            )
        }
        Text(segments.body, textStyle = base)
    }
}
