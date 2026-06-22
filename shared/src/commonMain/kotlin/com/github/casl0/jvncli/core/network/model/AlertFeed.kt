package com.github.casl0.jvncli.core.network.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * getAlertList のレスポンスルート `<feed>`。
 *
 * 正常時は [entries] に注意喚起が並び、エラー時は [entries] が空で [status] の `retCd` が 0 以外になる。
 */
@Serializable
@XmlSerialName(value = "feed", namespace = ATOM_NS, prefix = "")
internal data class AlertFeed(
    @XmlSerialName(value = "entry", namespace = ATOM_NS, prefix = "")
    val entries: List<AlertEntry> = emptyList(),
    val status: JvnStatus,
)
