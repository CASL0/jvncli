package com.github.casl0.jvncli.core.datasource

import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.model.AlertList
import com.github.casl0.jvncli.core.model.ProductList
import com.github.casl0.jvncli.core.model.VendorList

/**
 * MyJVN の各 API からデータを取得するデータソース。
 *
 * 後続の MyJVN API もこのインターフェースに追記してまとめる。実装の詳細 (Ktor/Ktorfit/XML) は隠蔽し、 上位レイヤ (リポジトリ等) はこの抽象にのみ依存する。
 */
interface JvnDataSource {
    /**
     * 注意警戒情報の一覧 (getAlertList) を取得する。
     *
     * @param startItem エントリの開始位置 (既定 1)
     * @param maxCountItem エントリの取得数 (既定 50・上限 50)
     * @param datePublished 更新年 (4 桁)
     * @param dateFirstPublished 初公開年 (4 桁)
     * @param cpeName CPE 名称
     */
    suspend fun getAlertList(
        startItem: Int? = null,
        maxCountItem: Int? = null,
        datePublished: Int? = null,
        dateFirstPublished: Int? = null,
        cpeName: String? = null,
    ): JvnResult<AlertList>

    /**
     * ベンダーの一覧 (getVendorList) を取得する。
     *
     * @param startItem エントリの開始位置 (既定 1)
     * @param maxCountItem エントリの取得数 (既定 10000・上限 10000)
     * @param cpeName CPE 識別子 (ワイルドカード `*` 可)
     * @param keyword ベンダー名の検索キーワード
     * @param lang 言語 (`ja` または `en`)
     */
    suspend fun getVendorList(
        startItem: Int? = null,
        maxCountItem: Int? = null,
        cpeName: String? = null,
        keyword: String? = null,
        lang: String? = null,
    ): JvnResult<VendorList>

    /**
     * 製品の一覧 (getProductList) を取得する。ベンダーごとに製品が入れ子で返る。
     *
     * @param startItem エントリの開始位置 (既定 1)
     * @param maxCountItem エントリの取得数 (既定 10000・上限 10000)
     * @param cpeName CPE 名 (`cpe:/{part}:{vendor}:{product}`)
     * @param vendorId ベンダー ID
     * @param productId 製品 ID
     * @param keyword 検索キーワード
     * @param lang 言語 (`ja` または `en`)
     */
    suspend fun getProductList(
        startItem: Int? = null,
        maxCountItem: Int? = null,
        cpeName: String? = null,
        vendorId: Int? = null,
        productId: Int? = null,
        keyword: String? = null,
        lang: String? = null,
    ): JvnResult<ProductList>
}
