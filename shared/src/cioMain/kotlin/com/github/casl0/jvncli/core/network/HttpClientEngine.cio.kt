package com.github.casl0.jvncli.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

/** JVM / Linux / macOS では CIO エンジンを使う。 */
internal actual fun defaultHttpClientEngine(): HttpClientEngine = CIO.create()
