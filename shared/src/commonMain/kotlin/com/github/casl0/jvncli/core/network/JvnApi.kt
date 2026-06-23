package com.github.casl0.jvncli.core.network

import com.github.casl0.jvncli.core.network.model.AlertFeed
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

/**
 * MyJVN API の Ktorfit インターフェース。
 *
 * 後続の MyJVN API もこのインターフェースに追記してまとめる。レスポンスは ContentNegotiation により XML から DTO ([AlertFeed])
 * へ自動デコードされる。
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
}
