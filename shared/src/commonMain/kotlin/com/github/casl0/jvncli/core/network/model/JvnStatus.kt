package com.github.casl0.jvncli.core.network.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * MyJVN 共通のステータス `<status:Status>`。
 *
 * [retCd] が 0 なら正常、0 以外ならエラーで [errCd] / [errMsg] に詳細が入る。 件数系の属性 ([retMax] / [totalRes] など)
 * はエラー時に空文字になるため文字列で受ける。
 */
@Serializable
@XmlSerialName(value = "Status", namespace = STATUS_NS, prefix = "status")
internal data class JvnStatus(
    val version: String? = null,
    val method: String? = null,
    val retCd: Int = 0,
    val retMax: String? = null,
    val errCd: String? = null,
    val errMsg: String? = null,
    val totalRes: String? = null,
    val totalResRet: String? = null,
    val firstRes: String? = null,
    val feed: String? = null,
)
