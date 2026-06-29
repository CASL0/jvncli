package com.github.casl0.jvncli

import com.github.casl0.jvncli.tui.runJvnTui
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.runBlocking

/** 実行環境ごとの HTTP エンジン (JVM/Linux/macOS: CIO、Windows: WinHttp)。 */
internal expect fun platformHttpClientEngine(): HttpClientEngine

fun main() = runBlocking { runJvnTui(platformHttpClientEngine()) }
