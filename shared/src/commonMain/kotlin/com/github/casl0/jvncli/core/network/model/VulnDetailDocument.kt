package com.github.casl0.jvncli.core.network.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

/**
 * getVulnDetailInfo のレスポンスルート `<VULDEF-Document>` (VULDEF 3.2)。
 *
 * 脆弱性詳細は [vulinfos] に並び、エラー時は空で [status] の `retCd` が 0 以外になる。vuldef_3.2.xsd 上 VulinfoData
 * 配下の要素はほぼすべて任意 (`minOccurs=0`) のため、対応する DTO も nullable / 空リストで受ける。
 */
@Serializable
@XmlSerialName(value = "VULDEF-Document", namespace = VULDEF_NS, prefix = "")
internal data class VulnDetailDocument(
    val vulinfos: List<Vulinfo> = emptyList(),
    val status: JvnStatus,
)

/** 脆弱性 1 件 `<Vulinfo>`。 */
@Serializable
@XmlSerialName(value = "Vulinfo", namespace = VULDEF_NS, prefix = "")
internal data class Vulinfo(
    @XmlElement(true)
    @XmlSerialName(value = "VulinfoID", namespace = VULDEF_NS, prefix = "")
    val vulinfoId: String? = null,
    @XmlSerialName(value = "VulinfoData", namespace = VULDEF_NS, prefix = "")
    val data: VulinfoData? = null,
)

/** 脆弱性の本体 `<VulinfoData>`。 */
@Serializable
@XmlSerialName(value = "VulinfoData", namespace = VULDEF_NS, prefix = "")
internal data class VulinfoData(
    @XmlElement(true)
    @XmlSerialName(value = "Title", namespace = VULDEF_NS, prefix = "")
    val title: String? = null,
    @XmlSerialName(value = "VulinfoDescription", namespace = VULDEF_NS, prefix = "")
    val description: VulinfoDescription? = null,
    @XmlSerialName(value = "Affected", namespace = VULDEF_NS, prefix = "")
    val affected: Affected? = null,
    @XmlSerialName(value = "Impact", namespace = VULDEF_NS, prefix = "") val impact: Impact? = null,
    @XmlSerialName(value = "Solution", namespace = VULDEF_NS, prefix = "")
    val solution: Solution? = null,
    @XmlSerialName(value = "Related", namespace = VULDEF_NS, prefix = "")
    val related: Related? = null,
    @XmlSerialName(value = "History", namespace = VULDEF_NS, prefix = "")
    val history: History? = null,
    @XmlElement(true)
    @XmlSerialName(value = "DateFirstPublished", namespace = VULDEF_NS, prefix = "")
    val dateFirstPublished: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "DateLastUpdated", namespace = VULDEF_NS, prefix = "")
    val dateLastUpdated: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "DatePublic", namespace = VULDEF_NS, prefix = "")
    val datePublic: String? = null,
)

/** 概要 `<VulinfoDescription><Overview>...</Overview></VulinfoDescription>`。 */
@Serializable
@XmlSerialName(value = "VulinfoDescription", namespace = VULDEF_NS, prefix = "")
internal data class VulinfoDescription(
    @XmlElement(true)
    @XmlSerialName(value = "Overview", namespace = VULDEF_NS, prefix = "")
    val overview: String? = null
)

/** 影響を受ける製品のコンテナ `<Affected>`。 */
@Serializable
@XmlSerialName(value = "Affected", namespace = VULDEF_NS, prefix = "")
internal data class Affected(val items: List<AffectedItem> = emptyList())

/** 影響を受ける製品 `<AffectedItem>`。 */
@Serializable
@XmlSerialName(value = "AffectedItem", namespace = VULDEF_NS, prefix = "")
internal data class AffectedItem(
    @XmlElement(true)
    @XmlSerialName(value = "Name", namespace = VULDEF_NS, prefix = "")
    val name: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "ProductName", namespace = VULDEF_NS, prefix = "")
    val productName: String? = null,
    val cpes: List<VuldefCpe> = emptyList(),
    @XmlElement(true)
    @XmlSerialName(value = "VersionNumber", namespace = VULDEF_NS, prefix = "")
    val versionNumbers: List<String> = emptyList(),
)

/** 影響製品の CPE `<Cpe version="2.2">CPE</Cpe>`。 */
@Serializable
@XmlSerialName(value = "Cpe", namespace = VULDEF_NS, prefix = "")
internal data class VuldefCpe(val version: String? = null, @XmlValue val value: String = "")

/** 影響度のコンテナ `<Impact>`。 */
@Serializable
@XmlSerialName(value = "Impact", namespace = VULDEF_NS, prefix = "")
internal data class Impact(
    @XmlSerialName(value = "Cvss", namespace = VULDEF_NS, prefix = "")
    val cvssList: List<VuldefCvss> = emptyList(),
    val impactItems: List<ImpactItem> = emptyList(),
)

/** CVSS 評価 `<Cvss version="3.0"><Severity/><Base/><Vector/></Cvss>`。 */
@Serializable
@XmlSerialName(value = "Cvss", namespace = VULDEF_NS, prefix = "")
internal data class VuldefCvss(
    val version: String? = null,
    @XmlSerialName(value = "Severity", namespace = VULDEF_NS, prefix = "")
    val severity: Severity? = null,
    @XmlElement(true)
    @XmlSerialName(value = "Base", namespace = VULDEF_NS, prefix = "")
    val base: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "Vector", namespace = VULDEF_NS, prefix = "")
    val vector: String? = null,
)

/** 深刻度 `<Severity type="Base">High</Severity>`。 */
@Serializable
@XmlSerialName(value = "Severity", namespace = VULDEF_NS, prefix = "")
internal data class Severity(val type: String? = null, @XmlValue val value: String = "")

/** 想定される影響 `<ImpactItem>`。 */
@Serializable
@XmlSerialName(value = "ImpactItem", namespace = VULDEF_NS, prefix = "")
internal data class ImpactItem(
    @XmlElement(true)
    @XmlSerialName(value = "Description", namespace = VULDEF_NS, prefix = "")
    val description: String? = null
)

/** 対策のコンテナ `<Solution>`。 */
@Serializable
@XmlSerialName(value = "Solution", namespace = VULDEF_NS, prefix = "")
internal data class Solution(val items: List<SolutionItem> = emptyList())

/** 対策 `<SolutionItem>`。 */
@Serializable
@XmlSerialName(value = "SolutionItem", namespace = VULDEF_NS, prefix = "")
internal data class SolutionItem(
    @XmlElement(true)
    @XmlSerialName(value = "Description", namespace = VULDEF_NS, prefix = "")
    val description: String? = null
)

/** 関連情報のコンテナ `<Related>`。 */
@Serializable
@XmlSerialName(value = "Related", namespace = VULDEF_NS, prefix = "")
internal data class Related(val items: List<RelatedItem> = emptyList())

/** 関連情報 `<RelatedItem type="advisory">`。 */
@Serializable
@XmlSerialName(value = "RelatedItem", namespace = VULDEF_NS, prefix = "")
internal data class RelatedItem(
    val type: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "Name", namespace = VULDEF_NS, prefix = "")
    val name: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "VulinfoID", namespace = VULDEF_NS, prefix = "")
    val vulinfoId: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "Title", namespace = VULDEF_NS, prefix = "")
    val title: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "URL", namespace = VULDEF_NS, prefix = "")
    val url: String? = null,
)

/** 更新履歴のコンテナ `<History>`。 */
@Serializable
@XmlSerialName(value = "History", namespace = VULDEF_NS, prefix = "")
internal data class History(val items: List<HistoryItem> = emptyList())

/** 更新履歴 `<HistoryItem>`。Description は 1 個以上のためリストで受ける。 */
@Serializable
@XmlSerialName(value = "HistoryItem", namespace = VULDEF_NS, prefix = "")
internal data class HistoryItem(
    @XmlElement(true)
    @XmlSerialName(value = "HistoryNo", namespace = VULDEF_NS, prefix = "")
    val historyNo: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "DateTime", namespace = VULDEF_NS, prefix = "")
    val dateTime: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "Description", namespace = VULDEF_NS, prefix = "")
    val descriptions: List<String> = emptyList(),
)
