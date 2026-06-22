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
     * @param engine テスト時に [io.ktor.client.engine.mock.MockEngine] を差し込めるよう注入可能にしている。
     *   省略時はプラットフォーム既定のエンジンを使う。
     */
    fun createApi(engine: HttpClientEngine = defaultHttpClientEngine()): JvnApi {
        val httpClient = HttpClient(engine)
        return Ktorfit.Builder().baseUrl(BASE_URL).httpClient(httpClient).build().createJvnApi()
    }
}

/** プラットフォーム既定の HTTP エンジン (JVM/Linux/macOS: CIO、Windows: WinHttp)。 */
internal expect fun defaultHttpClientEngine(): HttpClientEngine
