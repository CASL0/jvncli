package com.github.casl0.jvncli.core.model

/**
 * getStatistics の取得結果。theme に応じて該当する集計のみが入る。
 *
 * @property vulnCount 脆弱性件数の統計 (theme=sumJvnDb)
 * @property cvssBreakdown CVSS 深刻度別の統計 (theme=sumCvss)
 */
data class Statistics(val vulnCount: StatSummary?, val cvssBreakdown: StatSummary?)

/**
 * 統計の集計。
 *
 * @property total 全体の総数 (itm フィードでは null)
 * @property dataPoints 期間ごとの集計
 */
data class StatSummary(val total: StatTotal?, val dataPoints: List<StatDataPoint>)

/** 全体の総数。 */
data class StatTotal(val vulinfo: Int?, val vendor: Int?, val product: Int?)

/**
 * 期間ごとの集計値。
 *
 * @property date 期間 (年/四半期/月)
 * @property countAll 件数合計
 * @property countCritical CVSS Critical 件数 (sumCvss のみ)
 * @property countHigh CVSS High 件数 (sumCvss のみ)
 * @property countMedium CVSS Medium 件数 (sumCvss のみ)
 * @property countLow CVSS Low 件数 (sumCvss のみ)
 * @property countNone CVSS None 件数 (sumCvss のみ)
 */
data class StatDataPoint(
    val date: String?,
    val countAll: Int?,
    val countCritical: Int?,
    val countHigh: Int?,
    val countMedium: Int?,
    val countLow: Int?,
    val countNone: Int?,
)
