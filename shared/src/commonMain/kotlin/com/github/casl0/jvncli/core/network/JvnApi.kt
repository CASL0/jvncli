package com.github.casl0.jvncli.core.network

import com.github.casl0.jvncli.core.network.model.AlertFeed
import com.github.casl0.jvncli.core.network.model.VendorResult
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

/**
 * MyJVN API の Ktorfit インターフェース。
 *
 * 後続の MyJVN API もこのインターフェースに追記してまとめる。レスポンスは ContentNegotiation により XML から DTO へ自動デコードされる。
 */
internal interface JvnApi {
    /**
     * 注意警戒情報の一覧 (getAlertList) を取得する。
     *
     * @param startItem エントリの開始位置 (既定 1)
     * @param maxCountItem エントリの取得数 (既定 50・上限 50)
     * @param datePublished 更新年 (4 桁)
     * @param dateFirstPublished 初公開年 (4 桁)
     * @param cpeName CPE 名称
     */
    @GET("myjvn")
    suspend fun getAlertList(
        @Query("startItem") startItem: Int? = null,
        @Query("maxCountItem") maxCountItem: Int? = null,
        @Query("datePublished") datePublished: Int? = null,
        @Query("dateFirstPublished") dateFirstPublished: Int? = null,
        @Query("cpeName") cpeName: String? = null,
        @Query("method") method: String = "getAlertList",
        @Query("feed") feed: String = "hnd",
        @Query("ft") ft: String = "xml",
    ): AlertFeed

    /**
     * ベンダーの一覧 (getVendorList) を取得する。
     *
     * @param startItem エントリの開始位置 (既定 1)
     * @param maxCountItem エントリの取得数 (既定 10000・上限 10000)
     * @param cpeName CPE 識別子 (ワイルドカード `*` 可)
     * @param keyword ベンダー名の検索キーワード (UTF-8)
     * @param lang 言語 (`ja` または `en`、既定 `ja`)
     */
    @GET("myjvn")
    suspend fun getVendorList(
        @Query("startItem") startItem: Int? = null,
        @Query("maxCountItem") maxCountItem: Int? = null,
        @Query("cpeName") cpeName: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("lang") lang: String? = null,
        @Query("method") method: String = "getVendorList",
        @Query("feed") feed: String = "hnd",
        @Query("ft") ft: String = "xml",
    ): VendorResult

    /**
     * 製品の一覧 (getProductList) を取得する。ベンダーごとに製品が入れ子で返る。
     *
     * @param startItem エントリの開始位置 (既定 1)
     * @param maxCountItem エントリの取得数 (既定 10000・上限 10000)
     * @param cpeName CPE 名 (`cpe:/{part}:{vendor}:{product}`)
     * @param vendorId ベンダー ID
     * @param productId 製品 ID
     * @param keyword 検索キーワード (UTF-8)
     * @param lang 言語 (`ja` または `en`、既定 `ja`)
     */
    @GET("myjvn")
    suspend fun getProductList(
        @Query("startItem") startItem: Int? = null,
        @Query("maxCountItem") maxCountItem: Int? = null,
        @Query("cpeName") cpeName: String? = null,
        @Query("vendorId") vendorId: Int? = null,
        @Query("productId") productId: Int? = null,
        @Query("keyword") keyword: String? = null,
        @Query("lang") lang: String? = null,
        @Query("method") method: String = "getProductList",
        @Query("feed") feed: String = "hnd",
        @Query("ft") ft: String = "xml",
    ): VendorResult
}
