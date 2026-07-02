package com.github.casl0.jvncli.tui

/**
 * 端末上で全角(2 桁)として描画されるコードポイントの範囲。Unicode East Asian Width の Wide / Fullwidth に相当し、CJK
 * 統合漢字・かな・全角記号・主要な絵文字を含む。Ambiguous は半角(1 桁)として扱う。
 */
private val WIDE_RANGES =
    listOf(
        0x1100..0x115F, // Hangul Jamo
        0x2E80..0x303E, // CJK 部首補助〜CJK 記号
        0x3041..0x33FF, // ひらがな・カタカナ・CJK 互換
        0x3400..0x4DBF, // CJK 拡張 A
        0x4E00..0x9FFF, // CJK 統合漢字
        0xA000..0xA4CF, // イ文字
        0xAC00..0xD7A3, // ハングル音節
        0xF900..0xFAFF, // CJK 互換漢字
        0xFE10..0xFE19, // 縦書き形
        0xFE30..0xFE6F, // CJK 互換形
        0xFF00..0xFF60, // 全角英数・記号
        0xFFE0..0xFFE6, // 全角記号
        0x1F300..0x1F64F, // 記号・絵文字
        0x1F900..0x1F9FF, // 補助記号・絵文字
        0x20000..0x3FFFD, // CJK 拡張 B 以降
    )

/** [codePoint] の端末表示幅。全角は 2、それ以外は 1。 */
private fun codePointWidth(codePoint: Int) = if (WIDE_RANGES.any { codePoint in it }) 2 else 1

/** サロゲートペアを 1 コードポイントに束ねて走査し、[action] にコードポイントと UTF-16 長を渡す。 */
private inline fun String.forEachCodePoint(action: (codePoint: Int, charCount: Int) -> Unit) {
    var i = 0
    while (i < length) {
        val high = this[i]
        if (high.isHighSurrogate() && i + 1 < length && this[i + 1].isLowSurrogate()) {
            val codePoint = 0x10000 + ((high.code - 0xD800) shl 10) + (this[i + 1].code - 0xDC00)
            action(codePoint, 2)
            i += 2
        } else {
            action(high.code, 1)
            i += 1
        }
    }
}

/** この文字列を端末へ描画したときの表示幅(全角=2・半角=1 の総和)。 */
internal fun String.displayWidth(): Int {
    var width = 0
    forEachCodePoint { codePoint, _ -> width += codePointWidth(codePoint) }
    return width
}

/**
 * 端末表示幅が [maxWidth] を超えないよう、この文字列を切り詰める。
 *
 * 収まる場合はそのまま返す。超える場合は末尾を [ellipsis] に置き換え、省略後の表示幅も [maxWidth] を 超えないようにする。[maxWidth] が 0 以下、または
 * [ellipsis] の幅未満なら空文字を返す。
 */
internal fun String.ellipsize(maxWidth: Int, ellipsis: String = "…"): String {
    if (maxWidth <= 0) return ""
    if (displayWidth() <= maxWidth) return this
    val ellipsisWidth = ellipsis.displayWidth()
    if (maxWidth < ellipsisWidth) return ""

    val budget = maxWidth - ellipsisWidth
    var used = 0
    var i = 0
    while (i < length) {
        val high = this[i]
        val isPair = high.isHighSurrogate() && i + 1 < length && this[i + 1].isLowSurrogate()
        val codePoint =
            if (isPair) 0x10000 + ((high.code - 0xD800) shl 10) + (this[i + 1].code - 0xDC00)
            else high.code
        val width = codePointWidth(codePoint)
        if (used + width > budget) break
        used += width
        i += if (isPair) 2 else 1
    }
    return substring(0, i) + ellipsis
}

/**
 * この文字列を、各行の表示幅が [maxWidth] を超えないよう複数行へ折り返す。
 *
 * 既存の改行 (`\n`) はそのまま行区切りとして扱い、幅を超える行は表示幅基準で分割する(単語境界では なく表示幅で折るため日本語にも対応する)。空行は空文字の 1
 * 要素として残す。[maxWidth] が 0 以下、 または全角 1 文字が入らない幅では、はみ出さないよう末尾を [ellipsize] で切り詰める。
 */
internal fun String.wrapToWidth(maxWidth: Int): List<String> {
    if (maxWidth <= 0) return emptyList()
    val lines = mutableListOf<String>()
    for (paragraph in split("\n")) {
        var lineStart = 0
        var used = 0
        var i = 0
        while (i < paragraph.length) {
            val high = paragraph[i]
            val isPair =
                high.isHighSurrogate() &&
                    i + 1 < paragraph.length &&
                    paragraph[i + 1].isLowSurrogate()
            val codePoint =
                if (isPair)
                    0x10000 + ((high.code - 0xD800) shl 10) + (paragraph[i + 1].code - 0xDC00)
                else high.code
            val width = codePointWidth(codePoint)
            if (used > 0 && used + width > maxWidth) {
                lines.add(paragraph.substring(lineStart, i).ellipsize(maxWidth))
                lineStart = i
                used = 0
            }
            used += width
            i += if (isPair) 2 else 1
        }
        // 幅より広い単一グリフ(例: 全角 1 文字に対し maxWidth=1)でもはみ出さないよう最後に切り詰める。
        lines.add(paragraph.substring(lineStart).ellipsize(maxWidth))
    }
    return lines
}
