package com.github.casl0.jvncli

import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.di.provideJvnDataSource
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.runBlocking

/** 実行環境ごとの HTTP エンジン (JVM/Linux/macOS: CIO、Windows: WinHttp)。 */
internal expect fun platformHttpClientEngine(): HttpClientEngine

fun main() = runBlocking {
    val dataSource = provideJvnDataSource(platformHttpClientEngine())
    when (val result = dataSource.getAlertList(maxCountItem = 5)) {
        is JvnResult.Success -> {
            val list = result.data
            println("注意警戒情報 ${list.returnedResults}/${list.totalResults} 件")
            list.alerts.forEach { println("- [${it.severityLabel}] ${it.title}") }
        }
        is JvnResult.ApiError -> println("APIエラー (retCd=${result.retCd}): ${result.errMsg}")
        is JvnResult.NetworkError -> println("通信エラー: ${result.cause.message}")
    }
}
