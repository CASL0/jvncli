package com.github.casl0.jvncli.core.network.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/** 個々の注意喚起 `<entry>`。 */
@Serializable
@XmlSerialName(value = "entry", namespace = ATOM_NS, prefix = "")
internal data class AlertEntry(
    @XmlElement(true)
    @XmlSerialName(value = "title", namespace = ATOM_NS, prefix = "")
    val title: String,
    @XmlElement(true) @XmlSerialName(value = "id", namespace = ATOM_NS, prefix = "") val id: String,
    @XmlElement(true)
    @XmlSerialName(value = "published", namespace = ATOM_NS, prefix = "")
    val published: String,
    @XmlElement(true)
    @XmlSerialName(value = "updated", namespace = ATOM_NS, prefix = "")
    val updated: String,
    @XmlSerialName(value = "category", namespace = ATOM_NS, prefix = "")
    val category: Category? = null,
    @XmlSerialName(value = "items", namespace = SEC_NS, prefix = "sec") val items: SecItems? = null,
)

/** 重要度を表す `<category label="注意" term="Low"/>`。 */
@Serializable
@XmlSerialName(value = "category", namespace = ATOM_NS, prefix = "")
internal data class Category(val label: String? = null, val term: String? = null)

/** 関連情報のコンテナ `<sec:items>`。 */
@Serializable
@XmlSerialName(value = "items", namespace = SEC_NS, prefix = "sec")
internal data class SecItems(val items: List<SecItem> = emptyList())

/** 関連情報の各項目 `<sec:item>`。 */
@Serializable
@XmlSerialName(value = "item", namespace = SEC_NS, prefix = "sec")
internal data class SecItem(
    @XmlElement(true)
    @XmlSerialName(value = "title", namespace = SEC_NS, prefix = "sec")
    val title: String,
    @XmlElement(true)
    @XmlSerialName(value = "identifier", namespace = SEC_NS, prefix = "sec")
    val identifier: String,
    @XmlSerialName(value = "link", namespace = SEC_NS, prefix = "sec") val link: SecLink? = null,
    @XmlElement(true)
    @XmlSerialName(value = "cpe", namespace = SEC_NS, prefix = "sec")
    val cpe: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "published", namespace = SEC_NS, prefix = "sec")
    val published: String? = null,
    @XmlElement(true)
    @XmlSerialName(value = "updated", namespace = SEC_NS, prefix = "sec")
    val updated: String? = null,
)

/** 関連情報のリンク `<sec:link href="..."/>`。 */
@Serializable
@XmlSerialName(value = "link", namespace = SEC_NS, prefix = "sec")
internal data class SecLink(val href: String? = null)
