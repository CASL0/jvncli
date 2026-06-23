package com.github.casl0.jvncli.core.network

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine

/** [JvnApi] を生成するファクトリ。 */
internal object JvnClient {
    private const val BASE_URL = "https://jvndb.jvn.jp/"

    /**
     * [JvnApi] のインスタンスを生成する。
     *
     * @param engine 使用する HTTP エンジン。エンジン選択は実行環境 (app モジュール) の責務とし、 ここでは外部から注入する。テストでは
     *   [io.ktor.client.engine.mock.MockEngine] を渡せる。
     */
    fun createApi(engine: HttpClientEngine): JvnApi {
        val httpClient = HttpClient(engine)
        return Ktorfit.Builder().baseUrl(BASE_URL).httpClient(httpClient).build().createJvnApi()
    }
}
