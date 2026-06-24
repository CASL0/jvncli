package com.github.casl0.jvncli.core.model

/**
 * getVendorList の取得結果。XML 由来の詳細を隠した領域モデル。
 *
 * @property vendors ベンダーの一覧
 * @property totalResults 該当総件数 (totalRes)
 * @property returnedResults 今回返却された件数 (totalResRet)
 * @property firstResult 返却の開始位置 (firstRes)
 */
data class VendorList(
    val vendors: List<Vendor>,
    val totalResults: Int,
    val returnedResults: Int,
    val firstResult: Int,
)

/**
 * ベンダー情報。
 *
 * @property id ベンダー ID (vid)
 * @property name ベンダー名 (vname)
 * @property cpe CPE 識別子
 */
data class Vendor(val id: Int, val name: String, val cpe: String)
