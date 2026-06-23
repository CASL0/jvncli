package com.github.casl0.jvncli

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.winhttp.WinHttp

internal actual fun platformHttpClientEngine(): HttpClientEngine = WinHttp.create()
