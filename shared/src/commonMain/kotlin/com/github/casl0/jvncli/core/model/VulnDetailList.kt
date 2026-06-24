package com.github.casl0.jvncli.core.model

/**
 * getVulnDetailInfo の取得結果。XML 由来の詳細を隠した領域モデル。
 *
 * @property items 脆弱性詳細の一覧
 * @property totalResults 該当総件数 (totalRes)
 * @property returnedResults 今回返却された件数 (totalResRet)
 * @property firstResult 返却の開始位置 (firstRes)
 */
data class VulnDetailList(
    val items: List<VulnDetail>,
    val totalResults: Int,
    val returnedResults: Int,
    val firstResult: Int,
)

/**
 * 脆弱性詳細。VULDEF 形式の主要項目を保持する。
 *
 * @property id JVNDB の識別子 (VulinfoID)
 * @property title タイトル
 * @property overview 概要
 * @property affected 影響を受ける製品
 * @property cvssScores CVSS 評価 (バージョンごとに複数)
 * @property impacts 想定される影響の説明
 * @property solutions 対策の説明
 * @property related 関連情報
 * @property history 更新履歴
 * @property dateFirstPublished 初公開日時
 * @property dateLastUpdated 最終更新日時
 * @property datePublic 公開日時
 */
data class VulnDetail(
    val id: String?,
    val title: String?,
    val overview: String?,
    val affected: List<AffectedProductDetail>,
    val cvssScores: List<CvssScore>,
    val impacts: List<String>,
    val solutions: List<String>,
    val related: List<RelatedInfo>,
    val history: List<HistoryEntry>,
    val dateFirstPublished: String?,
    val dateLastUpdated: String?,
    val datePublic: String?,
)

/** 影響を受ける製品の詳細。 */
data class AffectedProductDetail(
    val vendor: String?,
    val productName: String?,
    val cpes: List<String>,
    val versions: List<String>,
)

/** 関連情報。 */
data class RelatedInfo(
    val type: String?,
    val name: String?,
    val vulinfoId: String?,
    val title: String?,
    val url: String?,
)

/** 更新履歴のエントリ。 */
data class HistoryEntry(
    val historyNo: String?,
    val dateTime: String?,
    val descriptions: List<String>,
)
