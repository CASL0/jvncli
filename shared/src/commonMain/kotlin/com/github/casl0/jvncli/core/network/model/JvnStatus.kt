package com.github.casl0.jvncli.core.network.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * MyJVN 共通のステータス `<status:Status>`。
 *
 * [retCd] が 0 なら正常、0 以外ならエラーで [errCd] / [errMsg] に詳細が入る。
 *
 * status_3.3.xsd で `use="required"` の属性は常に存在する (件数系はエラー時に空文字になるが省略はされない) ため 非 nullable
 * で受ける。`use="optional"` の [feed] のみ nullable。
 */
@Serializable
@XmlSerialName(value = "Status", namespace = STATUS_NS, prefix = "status")
internal data class JvnStatus(
    val version: String,
    val method: String,
    val retCd: Int,
    val retMax: String,
    val errCd: String,
    val errMsg: String,
    val totalRes: String,
    val totalResRet: String,
    val firstRes: String,
    val feed: String? = null,
)
