package com.github.casl0.jvncli.core.model

/**
 * getVulnOverviewList の取得結果。XML 由来の詳細を隠した領域モデル。
 *
 * @property items 脆弱性概要の一覧
 * @property totalResults 該当総件数 (totalRes)
 * @property returnedResults 今回返却された件数 (totalResRet)
 * @property firstResult 返却の開始位置 (firstRes)
 */
data class VulnOverviewList(
    val items: List<VulnOverview>,
    val totalResults: Int,
    val returnedResults: Int,
    val firstResult: Int,
)

/**
 * 脆弱性概要。
 *
 * @property id JVNDB の識別子 (sec:identifier、例: JVNDB-2026-020739)
 * @property title 脆弱性のタイトル
 * @property link 詳細ページの URL
 * @property description 概要説明
 * @property references 関連情報 (CVE/JVN/CWE など)
 * @property affectedProducts 影響を受ける製品
 * @property cvssScores CVSS 評価 (バージョンごとに複数)
 * @property date 日付 (dc:date)
 * @property issued 初公開日時 (dcterms:issued)
 * @property modified 更新日時 (dcterms:modified)
 */
data class VulnOverview(
    val id: String?,
    val title: String,
    val link: String,
    val description: String?,
    val references: List<VulnReference>,
    val affectedProducts: List<AffectedProduct>,
    val cvssScores: List<CvssScore>,
    val date: String?,
    val issued: String?,
    val modified: String?,
)

/** 関連情報。 */
data class VulnReference(val source: String?, val id: String?, val title: String?, val url: String)

/** 影響を受ける製品。 */
data class AffectedProduct(val vendor: String, val product: String?, val cpe: String)

/** CVSS 評価。 */
data class CvssScore(
    val version: String,
    val type: String,
    val severity: String,
    val score: Double?,
    val vector: String?,
)
