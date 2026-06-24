package com.github.casl0.jvncli.core.model

/**
 * getProductList の取得結果。ベンダーごとに製品を入れ子で持つ領域モデル。
 *
 * @property vendors 製品を保持するベンダーの一覧
 * @property totalResults 該当総件数 (totalRes)
 * @property returnedResults 今回返却された件数 (totalResRet)
 * @property firstResult 返却の開始位置 (firstRes)
 */
data class ProductList(
    val vendors: List<ProductVendor>,
    val totalResults: Int,
    val returnedResults: Int,
    val firstResult: Int,
)

/**
 * 製品を保持するベンダー。
 *
 * @property id ベンダー ID (vid)
 * @property name ベンダー名 (vname)
 * @property cpe ベンダーの CPE 識別子
 * @property products このベンダーの製品一覧
 */
data class ProductVendor(
    val id: Int,
    val name: String,
    val cpe: String,
    val products: List<Product>,
)

/**
 * 製品情報。
 *
 * @property id 製品 ID (pid)
 * @property name 製品名 (pname)
 * @property cpe 製品の CPE 識別子
 */
data class Product(val id: Int, val name: String, val cpe: String)
