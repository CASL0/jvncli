package com.github.casl0.jvncli.core.network.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

/**
 * getVulnOverviewList のレスポンスルート `<rdf:RDF>` (JVNRSS / RSS 1.0 + mod_sec)。
 *
 * `<channel>` はメタ情報のため読み飛ばし、脆弱性は直下の `<item>` に並ぶ。
 */
@Serializable
@XmlSerialName(value = "RDF", namespace = RDF_NS, prefix = "rdf")
internal data class VulnOverviewFeed(
    val items: List<VulnOverviewItem> = emptyList(),
    val status: JvnStatus,
)

/**
 * 個々の脆弱性概要 `<item>`。
 *
 * jvnrss_3.2.xsd 上 title/link のみ必須で、その他は任意 (references/cpe/cvss は 0 個以上)。
 */
@Serializable
@XmlSerialName(value = "item", namespace = RSS_NS, prefix = "")
internal data class VulnOverviewItem(
    @XmlElement(true)
    @XmlSerialName(value = "title", namespace = RSS_NS, prefix = "")
    val title: String,
    @XmlElement(true)
    @XmlSerialName(value = "link", namespace = RSS_NS, prefix = "")
    val link: String,
    @XmlElement(true)
    @XmlSerialName(value = "description", namespace = RSS_NS, prefix = "")
    val description: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "identifier", namespace = SEC_NS, prefix = "sec")
    val identifier: String? = null,
    val references: List<SecReference> = emptyList(),
    val cpes: List<SecCpe> = emptyList(),
    val cvssList: List<SecCvss> = emptyList(),
    @XmlElement(true)
    @XmlSerialName(value = "date", namespace = DC_NS, prefix = "dc")
    val date: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "issued", namespace = DCTERMS_NS, prefix = "dcterms")
    val issued: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "modified", namespace = DCTERMS_NS, prefix = "dcterms")
    val modified: String? = null,
)

/** 関連情報 `<sec:references source="..." id="..." title="...">URL</sec:references>`。 */
@Serializable
@XmlSerialName(value = "references", namespace = SEC_NS, prefix = "sec")
internal data class SecReference(
    val source: String? = null,
    val id: String? = null,
    val title: String? = null,
    @XmlValue val url: String = "",
)

/** 影響を受ける製品 `<sec:cpe version="..." vendor="..." product="...">CPE</sec:cpe>`。 */
@Serializable
@XmlSerialName(value = "cpe", namespace = SEC_NS, prefix = "sec")
internal data class SecCpe(
    val version: String,
    val vendor: String,
    val product: String? = null,
    @XmlValue val cpe: String = "",
)

/** CVSS 評価 `<sec:cvss score="..." severity="..." vector="..." version="..." type="..."/>`。 */
@Serializable
@XmlSerialName(value = "cvss", namespace = SEC_NS, prefix = "sec")
internal data class SecCvss(
    val version: String,
    val type: String,
    val severity: String,
    val score: String,
    val vector: String? = null,
)
