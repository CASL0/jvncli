package com.github.casl0.jvncli

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

internal actual fun platformHttpClientEngine(): HttpClientEngine = CIO.create()
