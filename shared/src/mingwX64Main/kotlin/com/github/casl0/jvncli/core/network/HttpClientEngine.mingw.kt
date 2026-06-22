package com.github.casl0.jvncli.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.winhttp.WinHttp

/** Windows (mingwX64) では CIO 非対応のため WinHttp エンジンを使う。 */
internal actual fun defaultHttpClientEngine(): HttpClientEngine = WinHttp.create()
