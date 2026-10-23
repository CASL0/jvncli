package com.github.casl0.jvncli.tui

import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.TextStyle

/**
 * High の橙。
 *
 * Mosaic の [Color] は RGB 値だけを持ち、名前付きの色にも橙は無い。CVSS の直感(赤→橙→黄→緑)に必要な橙だけを truecolor (xterm の 208
 * 番相当)で補う。名前付きの色を使う他の段階と違い端末のテーマには追従しないが、truecolor 非対応の端末では Mosaic が 256/16 色へ丸めるため、そこでは近い色に落ちる。
 */
private val HIGH_COLOR = Color(red = 0xFF, green = 0x87, blue = 0x00)

/** 選択中のタブの色。 */
internal val TAB_ACTIVE_COLOR: Color = Color.Cyan

/** エラーメッセージの色。 */
internal val ERROR_COLOR: Color = Color.Red

/** [level] に対応する前景色。[SeverityLevel.NONE] だけは色を付けず、[severityTextStyle] の Dim で控えめに見せる。 */
internal fun severityColor(level: SeverityLevel): Color =
    when (level) {
        SeverityLevel.CRITICAL -> Color.Red
        SeverityLevel.HIGH -> HIGH_COLOR
        SeverityLevel.MEDIUM -> Color.Yellow
        SeverityLevel.LOW -> Color.Green
        SeverityLevel.NONE -> Color.Unspecified
    }

/** [level] に対応する装飾。色を持たない [SeverityLevel.NONE] だけ Dim にして、色付きの段階と区別する。 */
internal fun severityTextStyle(level: SeverityLevel): TextStyle =
    if (level == SeverityLevel.NONE) TextStyle.Dim else TextStyle.Unspecified
