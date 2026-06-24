package com.github.casl0.jvncli.core.model

/**
 * getAlertList の取得結果。XML 由来の詳細を隠した領域モデルで、上位レイヤはこれだけを参照する。
 *
 * @property alerts 注意警戒情報の一覧
 * @property totalResults 該当総件数 (totalRes)
 * @property returnedResults 今回返却された件数 (totalResRet)
 * @property firstResult 返却の開始位置 (firstRes)
 */
data class AlertList(
    val alerts: List<Alert>,
    val totalResults: Int,
    val returnedResults: Int,
    val firstResult: Int,
)

/**
 * 個々の注意警戒情報。
 *
 * @property severityLabel 重要度の表示名 (例: "注意")
 * @property severityTerm 重要度の区分 (例: "Low")
 */
data class Alert(
    val id: String,
    val title: String,
    val published: String?,
    val updated: String,
    val severityLabel: String?,
    val severityTerm: String?,
    val references: List<AlertReference>,
)

/** 注意警戒情報に紐づく関連情報。 */
data class AlertReference(
    val title: String?,
    val identifier: String?,
    val url: String?,
    val cpe: String?,
    val published: String?,
    val updated: String?,
)
