package com.github.casl0.jvncli.tui

import com.github.casl0.jvncli.core.model.CvssScore

/**
 * 深刻度の 5 段階。CVSS スコアや重要度の語をいったんこの enum へ正規化し、色は [severityColor] でここから引く。
 *
 * この enum 自体は色を持たない。`core/model` へ Mosaic の Color を持ち込まないための境界がここ。
 */
internal enum class SeverityLevel {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW,
    NONE,
}

/** CVSS v3 における各深刻度区分の下限スコア。 */
private const val CRITICAL_MIN_SCORE = 9.0
private const val HIGH_MIN_SCORE = 7.0
private const val MEDIUM_MIN_SCORE = 4.0

/** スコアを持たない CVSS を [highestScored] の比較で最下位に落とすための番兵(実スコアは 0.0 以上)。 */
private const val NO_SCORE = -1.0

/**
 * この CVSS 評価を [SeverityLevel] へ正規化する。
 *
 * 重要度の語([CvssScore.severity])を優先し、未設定または未知の語のときだけ [CvssScore.score] を CVSS v3 の閾値で判定する (v2
 * のスコアも同じ閾値に載せる。段階の目安として色を引くだけで、v2 の公式区分の再現は狙わない)。
 */
internal fun CvssScore.severityLevel(): SeverityLevel =
    severity?.let(::severityLevelOfLabel) ?: severityLevelOfScore(score)

/** 警戒情報の重要度区分(`severityTerm` の Low/High など)を [SeverityLevel] へ写す。未設定・未知の語は [SeverityLevel.NONE]。 */
internal fun severityLevelOfTerm(term: String?): SeverityLevel =
    term?.let(::severityLevelOfLabel) ?: SeverityLevel.NONE

/**
 * 複数バージョンの CVSS 評価から、一覧のバッジに使う 1 件を選ぶ。
 *
 * スコアが最大のものを採り、スコアを持つ評価が無ければ先頭(重要度の語だけでもバッジに使える)を返す。空なら null。
 */
internal fun List<CvssScore>.highestScored(): CvssScore? = maxByOrNull { it.score ?: NO_SCORE }

/** 一覧に出す深刻度バッジ。スコアがあれば `[9.8]`、無ければ重要度の語 `[High]` に落とす。どちらも無ければ null(バッジ無し)。 */
internal fun severityBadge(cvss: CvssScore?): String? {
    val text = cvss?.score?.toString() ?: cvss?.severity?.trim()?.takeIf { it.isNotEmpty() }
    return text?.let { "[$it]" }
}

/** 重要度を表す語を [SeverityLevel] へ写す。前後の空白と大小文字は無視し、未知の語は null を返して呼び出し側のフォールバックに委ねる。 */
private fun severityLevelOfLabel(label: String): SeverityLevel? =
    when (label.trim().lowercase()) {
        "critical" -> SeverityLevel.CRITICAL
        "high" -> SeverityLevel.HIGH
        "medium" -> SeverityLevel.MEDIUM
        "low" -> SeverityLevel.LOW
        "none" -> SeverityLevel.NONE
        else -> null
    }

/** スコアを CVSS v3 の閾値で [SeverityLevel] へ写す。未設定と 0.0 以下は [SeverityLevel.NONE]。 */
private fun severityLevelOfScore(score: Double?): SeverityLevel =
    when {
        score == null -> SeverityLevel.NONE
        score >= CRITICAL_MIN_SCORE -> SeverityLevel.CRITICAL
        score >= HIGH_MIN_SCORE -> SeverityLevel.HIGH
        score >= MEDIUM_MIN_SCORE -> SeverityLevel.MEDIUM
        score > 0.0 -> SeverityLevel.LOW
        else -> SeverityLevel.NONE
    }
